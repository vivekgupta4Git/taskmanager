package com.ruviapps

import com.mongodb.client.*
import com.ruviapps.khatu.domain.entity.ShyamPremiGroup
import com.ruviapps.khatu.domain.entity.toResponse
import com.ruviapps.khatu.domain.repository.ShyamGroupRepositoryImpl
import com.ruviapps.khatu.request.ShyamPremiRequest
import com.ruviapps.khatu.request.toDomain
import green
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.bson.Document
import red

fun Application.configureDatabases() {
    val mongoDatabase = connectToMongoDB()
    val repository = ShyamGroupRepositoryImpl(mongoDatabase)
    routing {
        //Create Group
        post("/create-group") {
                try {
                val request = call.receive<ShyamPremiRequest>()
                val inserted = repository.insertGroup(request.toDomain())
                call.respond(HttpStatusCode.Created, inserted)
            } catch (e: NoTransformationFoundException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/groups") {
            val groups = repository.getAllGroup()
            if (groups.isEmpty())
                return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.Found, groups)
        }

        get("/groups/{id}") {
            val id: String = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val found = repository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(
                HttpStatusCode.Found,
                found.toResponse()
            )
        }
        delete("/groups/{id}") {
            val id: String = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = repository.deleteById(id) ?: return@delete call.respond(HttpStatusCode.NotModified)
            call.respond(
                HttpStatusCode.Accepted,
                deleted.toResponse()
            )
        }
        put("/groups/{id}") {
            val id: String = call.parameters["id"]
                ?: return@put call.respond(HttpStatusCode.BadRequest)
            val group = call.receive<ShyamPremiGroup>()
            val updated = repository.updateGroup(id, group) ?: return@put call.respond(HttpStatusCode.NotModified)

            call.respond(
                HttpStatusCode.Accepted,
                updated.toResponse()
            )
        }

    }
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
    val uri = "mongodb+srv://itguru4all:IjrTbKTRR3j4QIpw@vivekcluster.d5jzq.mongodb.net/?retryWrites=true&w=majority&appName=vivekCluster"

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
