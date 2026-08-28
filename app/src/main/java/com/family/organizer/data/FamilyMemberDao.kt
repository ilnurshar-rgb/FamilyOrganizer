package com.family.organizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {

    @Query("SELECT * FROM family_members ORDER BY name ASC")
    fun observeAll(): Flow<List<FamilyMember>>

    @Insert
    suspend fun insert(member: FamilyMember)

    @Insert
    suspend fun insertAll(members: List<FamilyMember>)

    @Upsert
    suspend fun upsert(member: FamilyMember)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM family_members")
    suspend fun count(): Int
}
