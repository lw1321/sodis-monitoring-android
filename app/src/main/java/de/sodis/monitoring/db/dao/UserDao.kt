package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("SELECT * FROM User WHERE type=0")
    fun getAllLocalExperts(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE id=:localExpertId")
    fun getByLocalExpertId(localExpertId: Int): User

    @Query("SELECT COUNT(*) FROM User WHERE id=:localExpertId")
    fun count(localExpertId: Int): Int
}