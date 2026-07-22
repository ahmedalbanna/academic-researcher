package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ResearchProposalEntity::class,
        LiteratureSummaryEntity::class,
        CitationEntity::class,
        DataAnalysisEntity::class,
        PeerPaperEntity::class,
        PeerFeedbackEntity::class,
        PeerResponseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AcademicDatabase : RoomDatabase() {

    abstract fun academicDao(): AcademicDao

    companion object {
        @Volatile
        private var INSTANCE: AcademicDatabase? = null

        fun getDatabase(context: Context): AcademicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcademicDatabase::class.java,
                    "academic_research_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
