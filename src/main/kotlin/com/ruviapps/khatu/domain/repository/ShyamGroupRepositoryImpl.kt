package com.ruviapps.khatu.domain.repository

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.ruviapps.khatu.domain.entity.ShyamPremiGroup
import com.ruviapps.khatu.domain.entity.ShyamPremiGroup.Companion.toDocument
import com.ruviapps.khatu.domain.entity.ShyamPremiGroup.Companion.toShyamPremiGroup
import com.ruviapps.khatu.domain.entity.toResponse
import com.ruviapps.khatu.domain.ports.ShyamGroupRepository
import com.ruviapps.khatu.response.ShyamPremiGroupResponse
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

    override suspend fun getAllGroup(): List<ShyamPremiGroup> = withContext(Dispatchers.IO) {
        val result = collection.find()
        val list = mutableListOf<ShyamPremiGroup>()
        result.forEach { document ->
            val id = document["_id"]
            val group = ShyamPremiGroup(
                id = id.toString(),
                name = document["name"].toString(),
                createdDate = document["createdDate"].toString(),
                feeFrequency = document["feeFrequency"].toString(),
                joiningFee = if (document["joiningFee"] == null) 0.0 else document["joiningFee"].toString().toDouble()
            )
            list.add(group)
        }

        list
    }

    override suspend fun insertGroup(shyamPremiGroup: ShyamPremiGroup): ShyamPremiGroupResponse =
        withContext(Dispatchers.IO) {
            val result = collection.insertOne(shyamPremiGroup.toDocument())
            shyamPremiGroup.toResponse().copy(
                id = result.getObjectIdAsString()
            )
        }

    override suspend fun deleteById(id: String): ShyamPremiGroup? = withContext(Dispatchers.IO) {
        val query = Filters.eq("_id", ObjectId(id))
        val result = collection.findOneAndDelete(query)
        result?.toShyamPremiGroup()?.copy(
            id = id
        )
    }

    override suspend fun findById(id: String): ShyamPremiGroup? {

        return withContext(Dispatchers.IO) {
            collection.find(
                Filters.eq("_id", ObjectId(id))
            ).first()?.toShyamPremiGroup()?.copy(
                id = id
            )

        }
    }

    override suspend fun updateGroup(id: String, shyamPremiGroup: ShyamPremiGroup): ShyamPremiGroup {
        return withContext(Dispatchers.IO) {
            val query = Filters.eq("_id", ObjectId(id))
            //below line causing problem in the _id field
            //val result = collection.findOneAndUpdate(query, shyamPremiGroup.toDocument())
            val updates = Updates.combine(
                Updates.set("name", shyamPremiGroup.name),
                Updates.set("joiningFee",shyamPremiGroup.joiningFee),
                Updates.set("feeFrequency",shyamPremiGroup.feeFrequency),
                Updates.set("createdDate",shyamPremiGroup.createdDate)
            )
            val updateOptions = FindOneAndUpdateOptions().upsert(true)
            val result = collection.findOneAndUpdate(
                query,
                updates,
                updateOptions
            )
            shyamPremiGroup.copy(id = result?.get("_id").toString())
        }
    }
}