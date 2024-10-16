package technology.nrkk.apps.socks.models

import java.io.Serializable

data class Order(val id: Int, val cart: Cart?, val user: User?, var couponCode: String?, var paymentType: String?, val orderStage: String?): Serializable