package com.ruviapps.khatu.service

import com.ruviapps.calm.CalmCrudService
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmUpdateDTO
import com.ruviapps.khatu.domain.repository.ShyamGroupCrudRepositoryImpl

class ShyamGroupCrudService(
    private val repository: ShyamGroupCrudRepositoryImpl,
) : CalmCrudService<ShyamPremiGroupCalmInsertDTO, ShyamPremiGroupCalmGetDTO, ShyamPremiGroupCalmUpdateDTO>(repository) {
    suspend fun createGroup(shyamPremiGroup: ShyamPremiGroupCalmInsertDTO) = repository.insert(shyamPremiGroup)
    suspend fun updateGroup(
        id: String,
        shyamPremiGroup: ShyamPremiGroupCalmUpdateDTO
    ) = repository.updateById(id, shyamPremiGroup)
    suspend fun deleteGroup(id: String) = repository.deleteById(id)
 }