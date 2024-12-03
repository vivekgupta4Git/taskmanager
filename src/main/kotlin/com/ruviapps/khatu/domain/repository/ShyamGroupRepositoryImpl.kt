package com.ruviapps.khatu.domain.repository

import com.mongodb.client.MongoDatabase
import com.ruviapps.calm.MongoCrudRepository
import com.ruviapps.khatu.domain.entity.GetBaseDTo.Companion.toGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupUpdateDTO
import org.bson.Document
import org.bson.conversions.Bson

class ShyamGroupRepositoryImpl(
    database: MongoDatabase
) :MongoCrudRepository<
        ShyamPremiGroupInsertDTO,
        ShyamPremiGroupGetDTO,
        ShyamPremiGroupUpdateDTO
        >(database, GROUP_COLLECTION) {

    companion object {
        const val GROUP_COLLECTION = "shyam_premi_collection"
    }

    override fun ShyamPremiGroupInsertDTO.insertDtoToDocument(): Document = toDocument()
    override fun ShyamPremiGroupUpdateDTO.toUpdateBson(): Bson = toUpdates()
    override fun Document.documentToGetDTO(): ShyamPremiGroupGetDTO = toGetDTO()
}