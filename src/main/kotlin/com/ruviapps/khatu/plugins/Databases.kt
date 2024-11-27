package com.ruviapps.khatu.plugins

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import green
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.bson.Document
import red

fun Application.configureDatabases() : MongoDatabase{
    val mongoDatabase = connectToMongoDB()
    return mongoDatabase
}

/**
 * Establishes connection with a MongoDB database.
 *
 * The following configuration properties (in application.yaml/application.conf) can be specified:
 * * `db.mongo.user` username for your database
 * * `db.mongo.password` password for the user
 * * `db.mongo.host` host that will be used for the database connection
 * * `db.mongo.port` port that will be used for the database connection
 * * `db.mongo.maxPoolSize` maximum number of connections to a MongoDB server
 * * `db.mongo.database.name` name of the database
 *
 * IMPORTANT NOTE: in order to make MongoDB connection working, you have to start a MongoDB server first.
 * See the instructions here: https://www.mongodb.com/docs/manual/administration/install-community/
 * all the paramaters above
 *
 * @returns [MongoDatabase] instance
 * */
fun Application.connectToMongoDB(): MongoDatabase {
    val uri = System.getenv("Mongo_connection_uri")

    val mongoClient = MongoClients.create(uri)
    val database = mongoClient.getDatabase("learning")
    runBlocking {
        database.runCommand(Document("ping", 1))
    }
    println("Successfully connected to MongoDB!--".green())


    monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
        println("MongoClient Closed".red())
    }

    return database
}
