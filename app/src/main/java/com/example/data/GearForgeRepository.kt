package com.example.data

import kotlinx.coroutines.flow.Flow

class GearForgeRepository(private val dao: GearForgeDao) {

    val allProgress: Flow<List<LevelProgressEntity>> = dao.getAllProgress()
    val playerProfile: Flow<PlayerProfileEntity?> = dao.getPlayerProfile()

    suspend fun recordLevelCompleted(
        levelId: Int,
        starsEarned: Int,
        timeSeconds: Int,
        gearsUsed: Int
    ) {
        val existing = dao.getProgressForLevel(levelId)
        val bestStars = maxOf(existing?.stars ?: 0, starsEarned)
        val bestTime = if (existing == null || existing.bestTimeSeconds == 0) {
            timeSeconds
        } else {
            minOf(existing.bestTimeSeconds, timeSeconds)
        }

        dao.saveLevelProgress(
            LevelProgressEntity(
                levelId = levelId,
                stars = bestStars,
                isCompleted = true,
                bestTimeSeconds = bestTime,
                gearsUsed = gearsUsed,
                completedTimestamp = System.currentTimeMillis()
            )
        )

        // Award Gear Coins & Parts
        val rewardCoins = starsEarned * 25 + 50
        val rewardParts = starsEarned * 5
        dao.addRewards(rewardCoins, rewardParts)

        // Unlock next level
        dao.unlockNextLevel(levelId + 1)
    }

    suspend fun saveProfile(profile: PlayerProfileEntity) {
        dao.savePlayerProfile(profile)
    }

    suspend fun unlockMaterial(materialName: String, currentProfile: PlayerProfileEntity, cost: Int) {
        if (currentProfile.gearCoins >= cost) {
            val list = currentProfile.unlockedMaterials.split(",").toMutableList()
            if (!list.contains(materialName)) {
                list.add(materialName)
            }
            dao.savePlayerProfile(
                currentProfile.copy(
                    gearCoins = currentProfile.gearCoins - cost,
                    unlockedMaterials = list.joinToString(",")
                )
            )
        }
    }
}
