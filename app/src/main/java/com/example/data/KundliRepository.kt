package com.example.data

import com.example.data.model.KundliProfile
import kotlinx.coroutines.flow.Flow

class KundliRepository(private val dao: KundliDao) {
    val allProfiles: Flow<List<KundliProfile>> = dao.getAllProfiles()

    suspend fun getProfileById(id: Long): KundliProfile? = dao.getProfileById(id)

    suspend fun getDefaultProfile(): KundliProfile? = dao.getDefaultProfile()

    suspend fun insertProfile(profile: KundliProfile): Long = dao.insertProfile(profile)

    suspend fun updateProfile(profile: KundliProfile) = dao.updateProfile(profile)

    suspend fun deleteProfile(profile: KundliProfile) = dao.deleteProfile(profile)

    suspend fun setDefaultProfile(id: Long) {
        dao.clearDefaultFlags()
        dao.markAsDefault(id)
    }
}
