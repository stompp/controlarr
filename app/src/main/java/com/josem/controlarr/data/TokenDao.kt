package com.josem.controlarr.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM tokens ORDER BY type ASC, name ASC")
    fun getAllTokens(): Flow<List<Token>>

    @Query("SELECT * FROM tokens WHERE id = :id")
    suspend fun getTokenById(id: Int): Token?

    @Query("SELECT * FROM tokens WHERE type = :type ORDER BY name ASC")
    fun getTokensByType(type: TokenType): Flow<List<Token>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertToken(token: Token)

    @Delete
    suspend fun deleteToken(token: Token)
}
