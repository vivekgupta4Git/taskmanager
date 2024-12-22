package com.ruviapps.calm.modules.user

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.client.model.Updates
import com.ruviapps.calm.CalmModel
import com.ruviapps.calm.system.CalmInsertDTO
import com.ruviapps.calm.system.CalmUpdateDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import org.bson.Document
import org.bson.conversions.Bson

@Serializable
data class User(
    val name: String,
    val email: String,
    @Transient
    val passwordHash: String = "",
    val isActive: Boolean = true
) : CalmModel() {
    override fun CalmInsertDTO.toDocument(): Document {
        return Document.parse(jsonForInsertDTO().encodeToString(this@toDocument))
    }

    override fun CalmUpdateDTO.toUpdates(): Bson {
        return Updates.combine(
            Updates.set("name", name),
            Updates.set("email", email),
            Updates.set("isActive", isActive),
            //Updates.set("passwordHash", passwordHash) this has been removed as we have separated api for password update
        )
    }

    fun getBcryptHashString(password: String): String {
        return BCrypt.withDefaults()
            .hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String): Boolean {
        val result = BCrypt.verifyer().verify(password.toCharArray(), passwordHash)
        return result.verified
    }

}

/**
 * A helper class for updating user password from the client
 */
@Serializable
data class UserPasswordUpdate(val password: String)