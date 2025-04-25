package technology.nrkk.apps.socks.models

import java.io.Serializable

data class Product(val id: String, val name: String?, val description: String?, val price: Double?, val count: Int?, val imageUrl: Array<String>?): Serializable {
    val imageUrl0: String
        get() = if (imageUrl !== null && imageUrl.size > 0) imageUrl[0] else ""
}

class ProductList: ArrayList<Product>()