package com.uade.ticket_mobile.data.local.dao

import androidx.room.*
import com.uade.ticket_mobile.data.local.entities.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets ORDER BY createdAt DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>
    
    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    suspend fun getTicketById(ticketId: Int): TicketEntity?
    
    @Query("SELECT * FROM tickets WHERE status = :status ORDER BY createdAt DESC")
    fun getTicketsByStatus(status: String): Flow<List<TicketEntity>>
    
    @Query("SELECT * FROM tickets WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTicketsByPriority(priority: String): Flow<List<TicketEntity>>
    
    @Query("SELECT * FROM tickets WHERE creatorId = :userId ORDER BY createdAt DESC")
    fun getTicketsByCreator(userId: Int): Flow<List<TicketEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<TicketEntity>)
    
    @Update
    suspend fun updateTicket(ticket: TicketEntity)
    
    @Delete
    suspend fun deleteTicket(ticket: TicketEntity)
    
    @Query("DELETE FROM tickets")
    suspend fun deleteAllTickets()
    
    @Query("DELETE FROM tickets WHERE id = :ticketId")
    suspend fun deleteTicketById(ticketId: Int)
}

