package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.KundliProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface KundliDao {
    @Query("SELECT * FROM kundli_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<KundliProfile>>

    @Query("SELECT * FROM kundli_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): KundliProfile?

    @Query("SELECT * FROM kundli_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): KundliProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: KundliProfile): Long

    @Update
    suspend fun updateProfile(profile: KundliProfile)

    @Delete
    suspend fun deleteProfile(profile: KundliProfile)

    @Query("UPDATE kundli_profiles SET isDefault = 0")
    suspend fun clearDefaultFlags()

    @Query("UPDATE kundli_profiles SET isDefault = 1 WHERE id = :id")
    suspend fun markAsDefault(id: Long)
}
