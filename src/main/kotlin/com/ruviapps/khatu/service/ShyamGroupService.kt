package com.ruviapps.khatu.service

import com.ruviapps.calm.MongoCrudRepository
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupUpdateDTO

class ShyamGroupService(
    private val repository: MongoCrudRepository<
            ShyamPremiGroupInsertDTO,
            ShyamPremiGroupGetDTO,
            ShyamPremiGroupUpdateDTO>,
) {
    suspend fun findAll() = repository.findAll()
    suspend fun findById(id: String) = repository.findById(id)
    suspend fun createGroup(shyamPremiGroup: ShyamPremiGroupInsertDTO) = repository.insert(shyamPremiGroup)
    suspend fun updateGroup(id: String,
                            shyamPremiGroup: ShyamPremiGroupUpdateDTO) = repository.updateById(id, shyamPremiGroup)
    suspend fun deleteGroup(id: String) = repository.deleteById(id)
    suspend fun deleteAll() = repository.deleteAll()
    suspend fun insertMany(insertDto: List<ShyamPremiGroupInsertDTO>) = repository.insertMany(insertDto)
}