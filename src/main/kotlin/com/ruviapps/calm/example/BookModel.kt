package com.ruviapps.calm.example

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Updates
import com.ruviapps.calm.*
import com.ruviapps.calm.system.CalmGetDTO.Companion.toGetDTO
import com.ruviapps.calm.system.CalmInsertDTO
import com.ruviapps.calm.system.CalmUpdateDTO
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.bson.Document
import org.bson.conversions.Bson

@Serializable
data class BookModel @OptIn(ExperimentalSerializationApi::class) constructor(
    val name: String,
    @EncodeDefault
    val authors: List<String> = emptyList(),
    @EncodeDefault
    val nop: Int = 0,
    @EncodeDefault
    val price: Double = 0.0
) : CalmModel() {
    override fun CalmInsertDTO.toDocument(): Document {
        return Document.parse(jsonForInsertDTO().encodeToString(this@toDocument))
    }

    override fun CalmUpdateDTO.toUpdates(): Bson {
            return Updates.combine(
                Updates.set("name", name),
                Updates.set("authors", authors),
                Updates.set("nop", nop),
                Updates.set("price", price)
            )
    }
}

class BookRepository(mongoDatabase: MongoDatabase) : CalmRepository<BookModel>(mongoDatabase,"MyBooks") {
    override fun BookModel.insertDtoToDocument(): Document = toDocument()
    override fun BookModel.toUpdateBson(): Bson = toUpdates()

    override fun Document.documentToGetDTO(): BookModel = toGetDTO()
}
class BookService( bookRepository: BookRepository) : CalmService<BookModel>(bookRepository)

class BookController(bookService: BookService) : CalmController<BookModel>(
    modelName = "book",
    service = bookService,
    makePluralize = true,
    authenticateRoute = true
) {
    override fun insertDtoTypeOf(): TypeInfo = typeInfo<BookModel>()

    override fun updateDtoTypeOf(): TypeInfo = typeInfo<BookModel>()
    override fun getDtoTypeOf(): TypeInfo = typeInfo<BookModel>()

    override fun getListDtoTypeOf(): TypeInfo = typeInfo<List<BookModel>>()
    override fun customRoutes(route: Route, groupName : String) {}

    override fun documentToDto(document: Document): BookModel = document.toGetDTO()
}
class BookRouter(
    basePath : String,
    controller: BookController
) : CalmRouter<BookModel>(basePath, groupName = "Book Api",controller)