package bangkit.daya.repository.auth

import bangkit.daya.model.User

interface AuthRepository {

    fun login(user: User)
    fun register(user: User)
}