package de.sodis.monitoring.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*

@Database(
    entities = [InputType::class, OptionChoice::class, Question::class, QuestionImage::class, QuestionOption::class, SurveyHeader::class, SurveySection::class, Interviewee::class, Answer::class, Village::class, Technology::class, IntervieweeTechnology::class, Task::class, Sector::class, User::class, CompletedSurvey::class, TodoPoint::class],
    version = 21
)
abstract class MonitoringDatabase : RoomDatabase() {
    abstract fun inputTypeDao(): InputTypeDao
    abstract fun optionChoiceDao(): OptionChoiceDao
    abstract fun questionDao(): QuestionDao
    abstract fun questionImageDao(): QuestionImageDao
    abstract fun questionOptionDao(): QuestionOptionDao
    abstract fun surveyHeaderDao(): SurveyHeaderDao
    abstract fun surveySectionDao(): SurveySectionDao
    abstract fun intervieweeDao(): IntervieweeDao
    abstract fun answerDao(): AnswerDao
    abstract fun villageDao(): VillageDao
    abstract fun sectorDao(): SectorDao
    abstract fun userDao(): UserDao
    abstract fun technologyDao(): TechnologyDao
    abstract fun intervieweeTechnologyDao(): IntervieweeTechnologyDao
    abstract fun taskDao(): TaskDao
    abstract fun completedSurveyDao(): CompletedSurveyDao
    abstract fun todoPointDao():TodoPointDao

    companion object {
        @Volatile
        private var INSTANCE: MonitoringDatabase? = null

        fun getDatabase(context: Context): MonitoringDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
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