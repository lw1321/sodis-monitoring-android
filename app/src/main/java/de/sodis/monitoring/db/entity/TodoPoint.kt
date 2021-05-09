package de.sodis.monitoring.db.entity

import androidx.room.*
import java.util.*


@Entity(tableName = "TodoPoint",
    foreignKeys = [
        ForeignKey(
            entity = Interviewee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("family")
        ),
        ForeignKey(
            entity = Village::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("village")
        )
    ]

    )
@TypeConverters(CalendarConverter::class)
data class TodoPoint(




    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "done")
    var done: Boolean? = false,

    @ColumnInfo(name = "creationdate")
    val creationdate: Calendar? = Calendar.getInstance(),

    @ColumnInfo(name = "duedate")
    var duedate: Calendar?,

    @ColumnInfo(name = "donedate")
    var donedate: Calendar?,

    @ColumnInfo(name = "family")
    var family: String?,

    @ColumnInfo(name = "village")
    var village: Int?,

    @ColumnInfo(name = "text")
    var text: String,

    @ColumnInfo(name="subText")
    var subText: String?,

    @ColumnInfo(name="picturePath")
    var picturePath: String?



    )

class CalendarConverter {

        @TypeConverter
        fun toCalendar(timestamp: Long?): Calendar?  {
            if(timestamp!=null) {
                var toReturn = Calendar.getInstance()
                toReturn.timeInMillis = timestamp
                return toReturn
            }
            else {
                return null
            }
        }

        @TypeConverter
        fun toTimestamp(calendar: Calendar?): Long? {
            if(calendar!=null) {
                return calendar.timeInMillis
            }
            else {
                return null
            }
        }

}