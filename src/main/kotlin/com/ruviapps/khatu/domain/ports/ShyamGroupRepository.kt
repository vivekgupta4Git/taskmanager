package com.ruviapps.khatu.domain.ports

import com.ruviapps.khatu.domain.entity.ShyamPremiGroupGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupUpdateDTO

interface ShyamGroupRepository {
    suspend fun insertGroup(shyamPremiGroup: ShyamPremiGroupInsertDTO) : ShyamPremiGroupGetDTO?
    suspend fun getAllGroup() : List<ShyamPremiGroupGetDTO>
    suspend fun deleteById(id : String) : ShyamPremiGroupGetDTO?
    suspend fun findById(id : String) : ShyamPremiGroupGetDTO?
    suspend fun updateGroup(id : String,shyamPremiGroupUpdateDTO: ShyamPremiGroupUpdateDTO) : ShyamPremiGroupGetDTO?
}