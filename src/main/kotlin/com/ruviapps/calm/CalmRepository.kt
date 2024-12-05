package com.ruviapps.calm

import com.mongodb.client.MongoDatabase
import com.ruviapps.calm.system.CalmCrudRepository

abstract class CalmRepository<T : CalmModel>(
    mongoDatabase: MongoDatabase,
    collectionName: String
) : CalmCrudRepository<T, T, T>(mongoDatabase, collectionName)