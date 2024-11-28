package com.ruviapps.khatu.domain.repository

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupGetDTO.Companion.toGetDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupInsertDTO.Companion.toDocument
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupUpdateDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupUpdateDTO.Companion.toUpdates
import com.ruviapps.khatu.domain.ports.ShyamGroupRepository
import com.ruviapps.khatu.util.getObjectIdAsString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId

class ShyamGroupRepositoryImpl(
    database: MongoDatabase
) : ShyamGroupRepository {

    private var collection: MongoCollection<Document>

    init {
        database.createCollection(GROUP_COLLECTION)
        collection = database.getCollection(GROUP_COLLECTION)
    }

    companion object {
        const val GROUP_COLLECTION = "shyam_premi_collection"
    }

    override suspend fun getAllGroup(): List<ShyamPremiGroupGetDTO> = withContext(Dispatchers.IO) {
        collection.find().toList().map { it.toGetDTO() }
    }

    override suspend fun insertGroup(shyamPremiGroup: ShyamPremiGroupInsertDTO): ShyamPremiGroupGetDTO? =
        withContext(Dispatchers.IO) {
            val result = collection.insertOne(shyamPremiGroup.toDocument())
            val id = result.getObjectIdAsString()
            findById(id ?: "")
        }

    override suspend fun deleteById(id: String): ShyamPremiGroupGetDTO? = withContext(Dispatchers.IO) {
        val query = Filters.eq("_id", ObjectId(id))
        val result = collection.findOneAndDelete(query)
        result?.toGetDTO()
    }

    override suspend fun findById(id: String): ShyamPremiGroupGetDTO? {

        return withContext(Dispatchers.IO) {
            collection.find(
                Filters.eq("_id", ObjectId(id))
            ).first()?.toGetDTO()

        }
    }

    override suspend fun updateGroup(
        id: String,
        shyamPremiGroupUpdateDTO: ShyamPremiGroupUpdateDTO
    ): ShyamPremiGroupGetDTO? {
        return withContext(Dispatchers.IO) {
            val query = Filters.eq("_id", ObjectId(id))
            val updates = shyamPremiGroupUpdateDTO.toUpdates()
            val updateOptions = FindOneAndUpdateOptions().upsert(true)
            val result = collection.findOneAndUpdate(
                query,
                updates,
                updateOptions
            )
            result?.toGetDTO()
        }
    }
}