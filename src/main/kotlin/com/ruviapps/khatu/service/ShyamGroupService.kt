package com.ruviapps.khatu.service

import com.ruviapps.khatu.domain.entity.ShyamPremiGroup
import com.ruviapps.khatu.domain.ports.ShyamGroupRepository

class ShyamGroupService(
    private val repository: ShyamGroupRepository,
) {
    suspend fun findAll() = repository.getAllGroup()
    suspend fun findById(id: String) = repository.findById(id)
    suspend fun createGroup(shyamPremiGroup: ShyamPremiGroup) = repository.insertGroup(shyamPremiGroup)
    suspend fun updateGroup(id: String, shyamPremiGroup: ShyamPremiGroup) = repository.updateGroup(id, shyamPremiGroup)
    suspend fun deleteGroup(id: String) = repository.deleteById(id)
}