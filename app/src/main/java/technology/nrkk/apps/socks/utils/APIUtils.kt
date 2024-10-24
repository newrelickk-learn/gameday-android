package technology.nrkk.apps.socks.utils

import android.content.Context
import android.os.Build
import android.renderscript.ScriptGroup.Input
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.newrelic.agent.android.NewRelic
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import technology.nrkk.apps.socks.models.Cart
import technology.nrkk.apps.socks.models.Order
import technology.nrkk.apps.socks.models.Product
import technology.nrkk.apps.socks.models.User
import java.io.IOException
import java.io.InputStream
import java.util.Base64


object APIUtils {

    private val client = OkHttpClient()
    //val URL_BASE = "http://10.0.2.2:8080"
    val URL_BASE = "https://front.demo.learn.nrkk.technology"
    fun getRequest(context: Context, path: String): Request {
        val token = context.getSharedPreferences("technology.nrkk.demo.front", MODE_PRIVATE).getString("token", "")
        return Request.Builder()
            .header("Content-type", "application/json")
            .header("Authorization", "Basic %s".format(token))
            .url("%s%s".format(URL_BASE, path))
            .get()
            .build()
    }
    fun postRequest(context: Context, path: String, body: RequestBody): Request {
        val token = context.getSharedPreferences("technology.nrkk.demo.front", MODE_PRIVATE).getString("token", "")
        return Request.Builder()
            .header("Content-type", "application/json")
            .header("Authorization", "Basic %s".format(token))
            .url("%s%s".format(URL_BASE, path))
            .post(body)
            .build()
    }

    fun login(context: Context, email: String, password: String, succeeded: (user: User) -> Unit, failed: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        val token = Base64.getEncoder().encodeToString("%s:%s".format(email, password).toByteArray())
        val loginPreference = context.getSharedPreferences("technology.nrkk.demo.front", MODE_PRIVATE)
        loginPreference.edit().putString("token", token).apply();
        val request = Request.Builder()
            .header("Content-type", "application/json")
            .header("Authorization", "Basic %s".format(token))
            .url("%s%s".format(URL_BASE, "/api/user"))
            .post("".toRequestBody()).build()
         /*
        val request = Request.Builder()
            .header("Content-type", "application/json")
            .header("Authorization", "Basic %s".format(token))
            .url("%s%s".format(URL_BASE, "/cart"))
            .get().build()
          */
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                failed()
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().orEmpty()
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val user = mapper.readValue<User>(responseData)
                    NewRelic.setAttribute("user", user.id.toString())
                    succeeded(user)
                } else {
                    failed()
                }
            }
        })
    }
    fun getUser(context: Context, succeeded: (user: User) -> Unit, failed: () -> Unit) {
        val request = getRequest(context, "/login")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                failed()
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val user = mapper.readValue<User>(response.body?.string().orEmpty())
                    succeeded(user)
                } else {
                    failed()
                }
            }
        })
    }
    fun getItems(context: Context, tags: String, succeeded: (List<Product>) -> Unit, failed: () -> Unit) {
        val request = getRequest(context, "/catalogue/items?tags=%s".format(tags))
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().orEmpty()
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val products = mapper.readValue<List<Product>>(responseData)
                    succeeded(products)
                } else {
                    failed()
                }
            }
        })
    }

    fun getItem(context: Context, id: String, succeeded: (Product) -> Unit, failed: () -> Unit) {
        val request = getRequest(context, "/catalogue/item/%s".format(id))
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().orEmpty()
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val products = mapper.readValue<Product>(responseData)
                    succeeded(products)
                } else {
                    failed()
                }
            }
        })
    }

    fun getImageStream (context: Context, path: String, succeeded: (inputStream: InputStream?) -> Unit) {
        val token = context.getSharedPreferences("technology.nrkk.demo.front", MODE_PRIVATE).getString("token", "")
        val request = Request.Builder()
            .header("Content-type", "application/json")
            .header("Authorization", "Basic %s".format(token))
            .url("%s%s".format("https://demo.sockshop.nrkk.technology", path))
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.byteStream()
                    succeeded(responseData)
                } else {

                }
            }
        })
    }

    fun getCart(context: Context, succeeded: (cart: Cart) -> Unit, failed: () -> Unit) {
        val request = getRequest(context, "/cart")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().orEmpty()
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val cart = mapper.readValue<Cart>(responseData)
                    succeeded(cart)
                } else {
                    failed()
                }
            }
        })
    }

    fun addToCart(context: Context, productId: String, amount: Int, succeeded: () -> Unit, failed: () -> Unit) {
        val body = "{\"productId\": \"%s\", \"amount\": %d}".format(productId, amount).toRequestBody()
        val request = postRequest(context, "/cart/add", body)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    succeeded()
                } else {
                    failed()
                }
            }
        })
    }

    fun startOrder(context: Context, succeeded: (order: Order) -> Unit, failed: () -> Unit) {
        val request = getRequest(context, "/order")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().orEmpty()
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val order = mapper.readValue<Order>(responseData)
                    succeeded(order)
                } else {
                    failed()
                }
            }
        })
    }

    fun confirmOrder(context: Context, order: Order, succeeded: (order: Order) -> Unit, failed: () -> Unit) {
        val json = jacksonObjectMapper().writeValueAsString(order)
        val request = postRequest(context, "/order/confirm", json.toRequestBody())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().orEmpty()
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val order = mapper.readValue<Order>(responseData)
                    succeeded(order)
                } else {
                    failed()
                }
            }
        })
    }

    fun purchase(context: Context, order: Order, succeeded: (order: Order) -> Unit, failed: () -> Unit) {
        val json = jacksonObjectMapper().writeValueAsString(order)
        val request = postRequest(context, "/order/purchase", json.toRequestBody())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NewRelic.recordHandledException(e)
                throw e
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string().orEmpty()
                    val mapper = jacksonObjectMapper()
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    val order = mapper.readValue<Order>(responseData)
                    succeeded(order)
                } else {
                    failed()
                }
            }
        })
    }

}