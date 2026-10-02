package com.example.viewmodel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.MechanicalAudioEngine
import com.example.data.GearForgeDatabase
import com.example.data.GearForgeRepository
import com.example.data.LevelProgressEntity
import com.example.data.PlayerProfileEntity
import com.example.model.Gear
import com.example.model.GearMaterial
import com.example.model.GearType
import com.example.model.Level
import com.example.model.LevelObjectiveType
import com.example.model.LevelRepository
import com.example.physics.GearSimulationEngine
import com.example.physics.MeshStatus
import com.example.ui.renderer.Camera3DState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.hypot

enum class Screen {
    HOME,
    LEVEL_SELECT,
    GAME,
    GEAR_LAB,
    CHALLENGES,
    PROFILE,
    SETTINGS
}

data class GameUiState(
    val currentScreen: Screen = Screen.HOME,
    val currentLevel: Level = LevelRepository.getLevel(1),
    val activeGearsOnBoard: List<Gear> = LevelRepository.getLevel(1).fixedGears,
    val inventoryGears: List<Gear> = LevelRepository.getLevel(1).inventoryGears,
    val isPowerOn: Boolean = false,
    val isLevelCompleted: Boolean = false,
    val isSystemOverloaded: Boolean = false,
    val overloadMessage: String? = null,
    val elapsedSeconds: Int = 0,
    val hintLevel: Int = 0, // 0 = off, 1 = gear, 2 = slot, 3 = direction, 4 = full
    val camera: Camera3DState = Camera3DState(),
    val draggedGear: Gear? = null,
    val dragScreenOffset: Offset = Offset.Zero,
    val starsEarned: Int = 0,
    val selectedGearForLab: Gear = Gear("lab_gear", GearType.STANDARD_SPUR, GearMaterial.BRASS, 16, 52f),
    val labRpm: Float = 60f,
    val labCamera: Camera3DState = Camera3DState(pitchDeg = 30f, yawDeg = 15f)
)

class GameViewModel(
    private val repository: GearForgeRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val levelProgressList: StateFlow<List<LevelProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playerProfile: StateFlow<PlayerProfileEntity?> = repository.playerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val simulationEngine = GearSimulationEngine()
    val audioEngine = MechanicalAudioEngine()

    private var simulationLoopJob: Job? = null
    private var timerJob: Job? = null

    init {
        loadLevel(1)
        startPhysicsLoop()
    }

    fun navigateTo(screen: Screen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun loadLevel(levelId: Int) {
        val level = LevelRepository.getLevel(levelId)
        audioEngine.stopMachineHum()
        _uiState.value = _uiState.value.copy(
            currentLevel = level,
            activeGearsOnBoard = level.fixedGears,
            inventoryGears = level.inventoryGears,
            isPowerOn = false,
            isLevelCompleted = false,
            isSystemOverloaded = false,
            overloadMessage = null,
            elapsedSeconds = 0,
            hintLevel = 0,
            camera = Camera3DState(),
            starsEarned = 0
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                if (!_uiState.value.isLevelCompleted) {
                    _uiState.value = _uiState.value.copy(elapsedSeconds = _uiState.value.elapsedSeconds + 1)
                }
            }
        }
    }

    fun togglePower() {
        val newPower = !_uiState.value.isPowerOn
        triggerHaptic(if (newPower) 60 else 30)

        if (newPower) {
            audioEngine.startMachineHum(75f)
        } else {
            audioEngine.stopMachineHum()
        }

        val simResult = simulationEngine.computeKinematics(
            gears = _uiState.value.activeGearsOnBoard,
            isPowerOn = newPower,
            objectiveType = _uiState.value.currentLevel.objectiveType
        )

        _uiState.value = _uiState.value.copy(
            isPowerOn = newPower,
            activeGearsOnBoard = simResult.updatedGears,
            isSystemOverloaded = simResult.isSystemOverloaded,
            overloadMessage = simResult.overloadMessage
        )

        if (simResult.isSystemOverloaded) {
            audioEngine.playGrindingJam()
            triggerHaptic(180)
        } else if (simResult.targetSatisfied && !_uiState.value.isLevelCompleted) {
            handleLevelVictory()
        }
    }

    private fun handleLevelVictory() {
        audioEngine.playVictoryChime()
        triggerHaptic(120)

        val level = _uiState.value.currentLevel
        val usedGearsCount = _uiState.value.activeGearsOnBoard.count { !it.isFixed }
        val timeSec = _uiState.value.elapsedSeconds

        // Compute 3-Star Rating
        var stars = 1
        if (usedGearsCount <= level.minGearsFor3Stars) stars++
        if (level.timeLimitSeconds == null || timeSec <= level.timeLimitSeconds) stars++

        _uiState.value = _uiState.value.copy(
            isLevelCompleted = true,
            starsEarned = stars
        )

        viewModelScope.launch {
            repository.recordLevelCompleted(
                levelId = level.id,
                starsEarned = stars,
                timeSeconds = timeSec,
                gearsUsed = usedGearsCount
            )
        }
    }

    fun resetLevel() {
        loadLevel(_uiState.value.currentLevel.id)
    }

    fun requestNextHint() {
        val nextHint = (_uiState.value.hintLevel + 1).coerceAtMost(4)
        _uiState.value = _uiState.value.copy(hintLevel = nextHint)
        audioEngine.playGearSnap()
    }

    // Camera Orbit & Pan Controls
    fun rotateCamera(deltaYaw: Float, deltaPitch: Float) {
        val cam = _uiState.value.camera
        val newYaw = (cam.yawDeg + deltaYaw).coerceIn(-65f, 65f)
        val newPitch = (cam.pitchDeg - deltaPitch).coerceIn(15f, 75f)
        _uiState.value = _uiState.value.copy(camera = cam.copy(yawDeg = newYaw, pitchDeg = newPitch))
    }

    fun panCamera(deltaX: Float, deltaY: Float) {
        val cam = _uiState.value.camera
        _uiState.value = _uiState.value.copy(camera = cam.copy(panX = cam.panX + deltaX, panY = cam.panY + deltaY))
    }

    fun zoomCamera(scaleMultiplier: Float) {
        val cam = _uiState.value.camera
        val newZoom = (cam.zoom * scaleMultiplier).coerceIn(0.55f, 2.3f)
        _uiState.value = _uiState.value.copy(camera = cam.copy(zoom = newZoom))
    }

    fun resetCamera() {
        _uiState.value = _uiState.value.copy(camera = Camera3DState())
    }

    // Drag and Drop Placement
    fun startDragging(gear: Gear, startOffset: Offset) {
        _uiState.value = _uiState.value.copy(
            draggedGear = gear,
            dragScreenOffset = startOffset
        )
        audioEngine.playGearSnap()
        triggerHaptic(20)
    }

    fun updateDragPosition(offset: Offset) {
        _uiState.value = _uiState.value.copy(dragScreenOffset = offset)
    }

    fun dropGearOnBoard(boardX: Float, boardY: Float, viewportCenter: Offset) {
        val gear = _uiState.value.draggedGear ?: return

        // Check if near any peg slot for soft magnetic snap
        val slots = _uiState.value.currentLevel.pegSlots
        var finalX = boardX
        var finalY = boardY
        var finalLayer = gear.layer

        val nearestSlot = slots.minByOrNull { hypot(it.x - boardX, it.y - boardY) }
        if (nearestSlot != null && hypot(nearestSlot.x - boardX, nearestSlot.y - boardY) < 45f) {
            finalX = nearestSlot.x
            finalY = nearestSlot.y
            finalLayer = nearestSlot.layer
            audioEngine.playGearSnap()
            triggerHaptic(40)
        } else {
            audioEngine.playHeavyClank()
        }

        val placedGear = gear.copy(
            x = finalX,
            y = finalY,
            layer = finalLayer,
            isDragging = false
        )

        // Add to active board gears and remove from inventory
        val updatedBoard = _uiState.value.activeGearsOnBoard + placedGear
        val updatedInv = _uiState.value.inventoryGears.filter { it.id != gear.id }

        _uiState.value = _uiState.value.copy(
            activeGearsOnBoard = updatedBoard,
            inventoryGears = updatedInv,
            draggedGear = null
        )

        // Recompute kinematics if power is active
        if (_uiState.value.isPowerOn) {
            val sim = simulationEngine.computeKinematics(
                gears = updatedBoard,
                isPowerOn = true,
                objectiveType = _uiState.value.currentLevel.objectiveType
            )
            _uiState.value = _uiState.value.copy(
                activeGearsOnBoard = sim.updatedGears,
                isSystemOverloaded = sim.isSystemOverloaded,
                overloadMessage = sim.overloadMessage
            )
            if (sim.targetSatisfied && !_uiState.value.isLevelCompleted) {
                handleLevelVictory()
            }
        }
    }

    fun removeGearFromBoard(gear: Gear) {
        if (gear.isFixed) return
        val updatedBoard = _uiState.value.activeGearsOnBoard.filter { it.id != gear.id }
        val updatedInv = _uiState.value.inventoryGears + gear.copy(x = 0f, y = 0f)

        _uiState.value = _uiState.value.copy(
            activeGearsOnBoard = updatedBoard,
            inventoryGears = updatedInv
        )
        audioEngine.playGearSnap()
        triggerHaptic(25)

        if (_uiState.value.isPowerOn) {
            val sim = simulationEngine.computeKinematics(
                gears = updatedBoard,
                isPowerOn = true,
                objectiveType = _uiState.value.currentLevel.objectiveType
            )
            _uiState.value = _uiState.value.copy(
                activeGearsOnBoard = sim.updatedGears,
                isSystemOverloaded = sim.isSystemOverloaded,
                overloadMessage = sim.overloadMessage
            )
        }
    }

    // Gear Lab customizer
    fun updateLabGearMaterial(material: GearMaterial) {
        val current = _uiState.value.selectedGearForLab
        _uiState.value = _uiState.value.copy(
            selectedGearForLab = current.copy(material = material)
        )
        audioEngine.playGearSnap()
    }

    fun updateLabTeethCount(teeth: Int) {
        val current = _uiState.value.selectedGearForLab
        val newRadius = (teeth * 3.2f).coerceIn(24f, 80f)
        _uiState.value = _uiState.value.copy(
            selectedGearForLab = current.copy(teethCount = teeth, radius = newRadius)
        )
    }

    fun updateLabRpm(rpm: Float) {
        _uiState.value = _uiState.value.copy(labRpm = rpm)
    }

    fun rotateLabCamera(dYaw: Float, dPitch: Float) {
        val cam = _uiState.value.labCamera
        _uiState.value = _uiState.value.copy(
            labCamera = cam.copy(
                yawDeg = (cam.yawDeg + dYaw) % 360f,
                pitchDeg = (cam.pitchDeg - dPitch).coerceIn(10f, 80f)
            )
        )
    }

    private fun startPhysicsLoop() {
        simulationLoopJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            while (isActive) {
                delay(16) // ~60 FPS
                val now = System.nanoTime()
                val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.005f, 0.05f)
                lastTime = now

                // Advance board gears
                val boardGears = _uiState.value.activeGearsOnBoard
                val advancedBoard = simulationEngine.advanceAnimationFrame(boardGears, dt)

                // Advance lab gear
                val labG = _uiState.value.selectedGearForLab
                val labAngle = (labG.rotationAngle + _uiState.value.labRpm * 6f * dt) % 360f
                val advancedLab = labG.copy(rotationAngle = labAngle)

                _uiState.value = _uiState.value.copy(
                    activeGearsOnBoard = advancedBoard,
                    selectedGearForLab = advancedLab
                )
            }
        }
    }

    private fun triggerHaptic(durationMs: Long) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stopMachineHum()
        simulationLoopJob?.cancel()
        timerJob?.cancel()
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = GearForgeDatabase.getInstance(context)
            val repo = GearForgeRepository(db.gearForgeDao())
            return GameViewModel(repo, context) as T
        }
    }
}
