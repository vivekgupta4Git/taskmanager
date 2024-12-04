package com.ruviapps.calm

import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bson.conversions.Bson

abstract class CalmCrudService<
        INSERT_DTO : CalmInsertDTO,
        GET_DTO : CalmGetDTO,
        UPDATE_DTO : CalmUpdateDTO>(
    private val repository: CalmCrudRepository<INSERT_DTO, GET_DTO, UPDATE_DTO>
) {
    open suspend fun withCollection(
        block: suspend MongoCollection<Document>.() -> GET_DTO?
    ): GET_DTO? = repository.withCollection(block)

    open suspend fun withCollectionReturningList(
        block: suspend MongoCollection<Document>.() -> List<GET_DTO?>?
    ): List<GET_DTO?>? = repository.withCollectionReturningList(block)

    open suspend fun insert(insertDto: INSERT_DTO): GET_DTO? = repository.insert(insertDto)
    open suspend fun updateById(id: String, updateDto: UPDATE_DTO): GET_DTO? = repository.updateById(id, updateDto)
    open suspend fun findById(id: String): GET_DTO? = repository.findById(id)
    open suspend fun findAll(): List<GET_DTO?>? = repository.findAll()
    open suspend fun deleteById(id: String): GET_DTO? = repository.deleteById(id)
    open suspend fun deleteAll(): Long = repository.deleteAll()
    open suspend fun insertMany(insertDto: List<INSERT_DTO>): List<GET_DTO?>? = repository.insertMany(insertDto)
    open fun deleteWhere(filter: () -> Bson): Long = repository.deleteWhere(filter)
    open fun findWhere(filter: () -> Bson): FindIterable<Document> = repository.findWhere(filter)
    open fun aggregationFlow(pipeline: () -> List<Bson>) = repository.aggregationFlow(pipeline)
}