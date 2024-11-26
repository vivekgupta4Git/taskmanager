package com.ruviapps.khatu.domain.ports

import com.ruviapps.khatu.domain.entity.ShyamPremiGroup
import com.ruviapps.khatu.response.ShyamPremiGroupResponse

interface ShyamGroupRepository {
    suspend fun insertGroup(shyamPremiGroup: ShyamPremiGroup) : ShyamPremiGroupResponse
    suspend fun getAllGroup() : List<ShyamPremiGroup>
    suspend fun deleteById(id : String) : ShyamPremiGroup?
    suspend fun findById(id : String) : ShyamPremiGroup?
    suspend fun updateGroup(id : String,shyamPremiGroup: ShyamPremiGroup) : ShyamPremiGroup?
}