package com.groupec.salesb.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.groupec.salesb.core.database.model.User

@Dao
interface UserDao {
    /*@Query(
        """
        SELECT * FROM user
        LIMIT 1 
        """
    )
    fun getDefaultUser(): Flow<User>*/

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)

    @Query("SELECT * FROM User WHERE id = :userId")
    suspend fun getUserById(userId: Int): User

    @Update
    suspend fun changePassword(user: User) : Int

    @Query("UPDATE User SET synchronised = false WHERE id = :id")
    suspend fun markUserForLaterSync(id: Int): Int
}