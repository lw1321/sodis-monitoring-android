package de.sodis.monitoring.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*

@Database(entities = [InputType::class, OptionChoice::class, Question::class, QuestionImage::class, QuestionOption::class, SurveyHeader::class, SurveySection::class], version = 1)
abstract class MonitoringDatabase : RoomDatabase() {
    abstract fun inputTypeDao(): InputTypeDao
    abstract fun optionChoiceDao(): OptionChoiceDao
    abstract fun questionDao(): QuestionDao
    abstract fun questionImageDao(): QuestionImageDao
    abstract fun questionOptionDao(): QuestionOptionDao
    abstract fun surveyHeaderDao(): SurveyHeaderDao
    abstract fun surveySectionDao(): SurveySectionDao
    abstract fun intervieweeDao(): IntervieweeDao

    companion object {
        @Volatile
        private var INSTANCE: MonitoringDatabase? = null

        fun getDatabase(context: Context): MonitoringDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MonitoringDatabase::class.java,
                    "monitoring_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}