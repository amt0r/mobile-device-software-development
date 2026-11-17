package com.example.emergencypower.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TelemetryRecordEntity::class, NetworkIncidentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EmergencyDatabase : RoomDatabase() {

    abstract fun emergencyDao(): EmergencyDao

    companion object {
        @Volatile
        private var INSTANCE: EmergencyDatabase? = null

        fun getInstance(context: Context): EmergencyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmergencyDatabase::class.java,
                    "emergency_pw3.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
