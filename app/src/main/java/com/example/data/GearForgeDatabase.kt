package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LevelProgressEntity::class, PlayerProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GearForgeDatabase : RoomDatabase() {

    abstract fun gearForgeDao(): GearForgeDao

    companion object {
        @Volatile
        private var INSTANCE: GearForgeDatabase? = null

        fun getInstance(context: Context): GearForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GearForgeDatabase::class.java,
                    "gearforge_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
