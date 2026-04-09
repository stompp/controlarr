package com.josem.controlarr.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Transaction
    @Query("SELECT * FROM servers ORDER BY name ASC")
    fun getAllServersWithHost(): Flow<List<ServerWithHost>>

    @Transaction
    @Query("SELECT * FROM servers WHERE hostId = :hostId ORDER BY name ASC")
    fun getServersByHost(hostId: Int): Flow<List<ServerWithHost>>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServerById(id: Int): Server?

    @Transaction
    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServerWithHostById(id: Int): ServerWithHost?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertServer(server: Server)

    @Delete
    suspend fun deleteServer(server: Server)
}
