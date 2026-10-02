package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: Int,
    val stars: Int = 0,
    val isCompleted: Boolean = false,
    val bestTimeSeconds: Int = 0,
    val gearsUsed: Int = 0,
    val completedTimestamp: Long = 0L
)

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val gearCoins: Int = 150,
    val partsCount: Int = 25,
    val currentLevel: Int = 1,
    val highestLevelUnlocked: Int = 1,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val unlockedMaterials: String = "STEEL,BRASS,CHROME", // Comma-separated names
    val selectedCustomMaterial: String = "BRASS",
    val customTeethCount: Int = 16,
    val achievementsCompleted: String = "" // Comma-separated achievement IDs
)
