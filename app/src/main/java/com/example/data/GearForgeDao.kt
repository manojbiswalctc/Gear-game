package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GearForgeDao {

    @Query("SELECT * FROM level_progress")
    fun getAllProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId")
    suspend fun getProgressForLevel(levelId: Int): LevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLevelProgress(progress: LevelProgressEntity)

    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfile(): Flow<PlayerProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerProfile(profile: PlayerProfileEntity)

    @Query("UPDATE player_profile SET gearCoins = gearCoins + :coins, partsCount = partsCount + :parts WHERE id = 1")
    suspend fun addRewards(coins: Int, parts: Int)

    @Query("UPDATE player_profile SET highestLevelUnlocked = MAX(highestLevelUnlocked, :unlockedLevel) WHERE id = 1")
    suspend fun unlockNextLevel(unlockedLevel: Int)
}
