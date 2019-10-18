package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.OptionChoice

@Dao
interface OptionChoiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(optionChoice: OptionChoice)

    @Query("SELECT COUNT(*) FROM OptionChoice WHERE id=:id")
    fun exists(id: Int): Int

    @Query("SELECT * FROM OptionChoice WHERE id=:id")
    fun getById(id:Int): OptionChoice
}
