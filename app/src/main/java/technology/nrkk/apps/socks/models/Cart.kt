package technology.nrkk.apps.socks.models

import java.io.Serializable

data class Cart(val id: Int, val active: Boolean, val totalPrice: Double, val amount: Int, val items: List<CartItem>, val user: User?): Serializable
