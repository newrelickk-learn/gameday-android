package technology.nrkk.apps.socks.utils

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import technology.nrkk.apps.socks.models.Product
import technology.nrkk.apps.socks.models.User
import java.io.IOException
import java.net.URL
import java.util.Base64


object URLUtils {

    val URL_BASE = "https://front.demo.learn.nrkk.technology"
    fun getImageUrl (path: String): String {
       return "%s%s".format(URL_BASE, path)

    }
}