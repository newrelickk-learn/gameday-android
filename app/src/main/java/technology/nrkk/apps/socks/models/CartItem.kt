package technology.nrkk.apps.socks.models

import java.io.Serializable

data class CartItem(val id: Int, val amount: Int, val productId: String?, val product: Product?): Serializable