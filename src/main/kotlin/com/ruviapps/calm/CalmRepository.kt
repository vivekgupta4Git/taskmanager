package com.ruviapps.calm

import com.mongodb.client.MongoDatabase

abstract class CalmRepository<T : CalmModel>(
    mongoDatabase: MongoDatabase,
    collectionName: String
) : CalmCrudRepository<T, T, T>(mongoDatabase, collectionName)