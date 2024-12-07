package com.ruviapps.khatu.service

import com.ruviapps.calm.system.CalmCrudController
import com.ruviapps.calm.system.CalmCrudService
import com.ruviapps.calm.system.ModuleName
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmUpdateDTO
import com.ruviapps.khatu.domain.repository.ShyamGroupCrudRepositoryImpl
import com.ruviapps.khatu.util.ListWrapperDto
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

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

class ShyamGroupCrudController(shyamGroupCrudService: ShyamGroupCrudService) :
    CalmCrudController<
            ShyamPremiGroupCalmInsertDTO,
            ShyamPremiGroupCalmGetDTO,
            ShyamPremiGroupCalmUpdateDTO>(
        service = shyamGroupCrudService,
        moduleName = ModuleName("shyamPremiGroup"),
        authenticateRoute = true
    ) {
    override fun insertDtoTypeOf(): TypeInfo  = typeInfo<ShyamPremiGroupCalmInsertDTO>()
    override fun insertListDtoTypeOf(): TypeInfo = typeInfo<ListWrapperDto<ShyamPremiGroupCalmInsertDTO>>()
    override fun updateDtoTypeOf(): TypeInfo  = typeInfo<ShyamPremiGroupCalmUpdateDTO>()
    override fun getDtoTypeOf(): TypeInfo = typeInfo<ShyamPremiGroupCalmGetDTO>()
    override fun getListDtoTypeOf(): TypeInfo = typeInfo<ListWrapperDto<ShyamPremiGroupCalmGetDTO>>()
    override fun Route.additionalRoutesForRegistration() {

    }
}