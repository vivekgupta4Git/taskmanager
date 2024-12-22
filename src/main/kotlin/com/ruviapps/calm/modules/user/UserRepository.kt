package com.ruviapps.calm.modules.user

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.ruviapps.calm.CalmRepository
import com.ruviapps.calm.system.CalmGetDTO.Companion.toGetDTO
import com.ruviapps.calm.system.MongoException
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class UserRepository(private val mongoDatabase: MongoDatabase)
    : CalmRepository<User>(mongoDatabase, "users"){
    override fun User.insertDtoToDocument(): Document = toDocument()
    override fun User.toUpdateBson(): Bson = toUpdates()
    override fun Document.documentToGetDTO(): User = toGetDTO()

    suspend fun updatePassword(id : String, password : String) : User?{
       return try {
            val query = Filters.eq("_id", ObjectId(id))
            val updates = Updates.set("password", password)
            val result = mongoDatabase.getCollection("users").updateOne(query, updates)
            result.wasAcknowledged().let { isAcknowledged ->
                if (isAcknowledged)
                    findById(id)
                else
                    throw MongoException("Failed to update")
            }
        } catch (ex : Exception) {
            throw MongoException(ex.message)
        }
    }
}