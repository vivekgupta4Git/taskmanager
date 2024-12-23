package com.ruviapps.khatu.domain.repository

import com.mongodb.client.MongoDatabase
import com.ruviapps.calm.system.CalmCrudRepository
import com.ruviapps.calm.system.CalmGetDTO.Companion.toGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmUpdateDTO
import org.bson.Document
import org.bson.conversions.Bson

class ShyamGroupCrudRepositoryImpl(
    database: MongoDatabase
) : CalmCrudRepository<
        ShyamPremiGroupCalmInsertDTO,
        ShyamPremiGroupCalmGetDTO,
        ShyamPremiGroupCalmUpdateDTO
        >(database, GROUP_COLLECTION) {

    companion object {
        const val GROUP_COLLECTION = "shyam_premi_collection"
    }

    override fun ShyamPremiGroupCalmInsertDTO.insertDtoToDocument(): Document = toDocument()
    override fun ShyamPremiGroupCalmUpdateDTO.toUpdateBson(): Bson = toUpdates()
    override fun Document.documentToGetDTO(): ShyamPremiGroupCalmGetDTO = toGetDTO()
}