package com.ruviapps.calm.modules.user

import com.ruviapps.calm.CalmService

class UserService(private val userRepository: UserRepository) : CalmService<User>(userRepository) {
    suspend fun updatePassword(id: String, password: String): User? {
        val user = userRepository.findById(id)
        return if (user != null)
            userRepository.updatePassword(id, password)
        else
            null
    }
}