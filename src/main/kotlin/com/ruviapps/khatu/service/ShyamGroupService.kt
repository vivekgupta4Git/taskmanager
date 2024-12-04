package com.ruviapps.khatu.service

import com.ruviapps.calm.CalmRepository
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmUpdateDTO

class ShyamGroupService(
    private val repository: CalmRepository<
            ShyamPremiGroupCalmInsertDTO,
            ShyamPremiGroupCalmGetDTO,
            ShyamPremiGroupCalmUpdateDTO>,
) {
    suspend fun findAll() = repository.findAll()
    suspend fun findById(id: String) = repository.findById(id)
    suspend fun createGroup(shyamPremiGroup: ShyamPremiGroupCalmInsertDTO) = repository.insert(shyamPremiGroup)
    suspend fun updateGroup(id: String,
                            shyamPremiGroup: ShyamPremiGroupCalmUpdateDTO) = repository.updateById(id, shyamPremiGroup)
    suspend fun deleteGroup(id: String) = repository.deleteById(id)
    suspend fun deleteAll() = repository.deleteAll()
    suspend fun insertMany(insertDto: List<ShyamPremiGroupCalmInsertDTO>) = repository.insertMany(insertDto)
}