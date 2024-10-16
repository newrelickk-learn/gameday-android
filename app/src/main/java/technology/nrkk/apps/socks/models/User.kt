package technology.nrkk.apps.socks.models

import java.io.Serializable

data class User(val id: Int, val username: String, val name: String, val email: String, val companyId: String): Serializable
