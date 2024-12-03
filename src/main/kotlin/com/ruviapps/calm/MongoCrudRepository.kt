package com.ruviapps.calm

import com.mongodb.client.AggregateIterable
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.ruviapps.khatu.domain.entity.GetBaseDTo
import com.ruviapps.khatu.domain.entity.InsertBaseDTO
import com.ruviapps.khatu.domain.entity.UpdateBaseDTO
import com.ruviapps.khatu.util.getObjectIdAsString
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId

abstract class MongoCrudRepository<INSERT_DTO : InsertBaseDTO, GET_DTO : GetBaseDTo, UPDATE_DTO : UpdateBaseDTO>(
    database: MongoDatabase,
    collectionName: String,
) {
    abstract fun INSERT_DTO.insertDtoToDocument(): Document
    abstract fun UPDATE_DTO.toUpdateBson(): Bson
    abstract fun Document.documentToGetDTO(): GET_DTO
    open var collection: MongoCollection<Document> = database.getCollection(collectionName)
    open suspend fun withCollection(
        block: suspend MongoCollection<Document>.() -> GET_DTO?
    ): GET_DTO? = with(collection) { block() }

    private fun requireValidId(id: String) {
        if (!ObjectId.isValid(id))
            throw MongoException("Invalid ID")
    }

    open suspend fun withCollectionReturningList(
        block: suspend MongoCollection<Document>.() -> List<GET_DTO?>?
    ): List<GET_DTO?>? = with(collection) { block() }

    open suspend fun insert(insertDto: INSERT_DTO): GET_DTO? = withCollection {
        try {
            val result = insertOne(insertDto.insertDtoToDocument())
            val id = result.getObjectIdAsString()
            if (id == null)
                throw MongoException("Failed to insert")
            else
                findById(id)
        } catch (ex: Exception) {
            throw MongoException(ex.message)
        }
    }

    open suspend fun insertMany(insertDto: List<INSERT_DTO>): List<GET_DTO?>? = withCollectionReturningList {
        try {
            val result = mutableListOf<GET_DTO?>()
            insertDto.forEach {
                result.add(insert(it))
            }
            result.toList()
        } catch (ex: Exception) {
            throw MongoException(ex.message)
        }
    }

    open suspend fun findById(id: String): GET_DTO? = withCollection {
        try {
            requireValidId(id)
            find(
                Filters.eq("_id", ObjectId(id))
            ).first()?.documentToGetDTO()
        } catch (ex: Exception) {
            throw MongoException(ex.message)
        }
    }

    open suspend fun findAll(): List<GET_DTO?>? = withCollectionReturningList {
        try {
            find().toList().map { it?.documentToGetDTO() }
        } catch (ex: Exception) {
            throw MongoException(ex.message)
        }
    }

    open suspend fun deleteById(id: String): GET_DTO? = withCollection {
        try {
            requireValidId(id)
            val deletedDocument = findById(id)
            val result = deleteOne(Filters.eq("_id", ObjectId(id)))
            if (result.wasAcknowledged())
                deletedDocument
            else
                throw MongoException("Failed to delete")
        } catch (ex: Exception) {
            throw MongoException(ex.message)
        }
    }

    open suspend fun updateById(
        id: String,
        updateDto: UPDATE_DTO): GET_DTO? = withCollection {
        try {
            requireValidId(id)
            val query = Filters.eq("_id", ObjectId(id))
            val updates = updateDto.toUpdateBson()
            val result = updateOne(
                query,
                updates,
            )
            result.wasAcknowledged().let { isAcknowledged ->
                if (isAcknowledged)
                    findById(id)
                else
                    throw MongoException("Failed to update")
            }

        } catch (ex: Exception) {
            throw MongoException(ex.message)
        }
    }

    open suspend fun deleteAll(): Long = with(collection) {
        try {
            deleteMany(Filters.empty()).deletedCount
        } catch (ex: Exception) {
            throw MongoException(ex.message)
        }
    }

    open fun aggregationFlow(pipeline: () -> List<Bson>)
            : AggregateIterable<Document> = with(collection) {
        aggregate(pipeline())
    }

    open fun deleteWhere(filter: () -> Bson) = with(collection) {
        deleteMany(filter()).deletedCount
    }

    open fun findWhere(filter: () -> Bson): FindIterable<Document> = with(collection) {
        find(filter())
    }
}

open class MongoException(message: String?) : Exception(message)


