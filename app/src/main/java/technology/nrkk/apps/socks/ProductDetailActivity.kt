package technology.nrkk.apps.socks

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.newrelic.agent.android.NewRelic
import technology.nrkk.apps.socks.databinding.ActivityProductDetailBinding
import technology.nrkk.apps.socks.models.Product
import technology.nrkk.apps.socks.utils.APIUtils
import java.io.InputStream

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productId = intent.extras?.getString("productId") ?: return
        APIUtils.getItem(this, productId, fun (product: Product) {
            runOnUiThread {
                binding.textName.text = product.name
                ("%.0f円".format(product.price)).also { binding.textPrice.text = it }
                binding.textCount.text = product.count.toString()
            }
            if (product.imageUrl?.size!! >= 2 &&  product.imageUrl[1].equals(null)) {
                throw Exception("No thumbnail image provided")
            }
            APIUtils.getImageStream(this, product.imageUrl0, fun (inputStream: InputStream?) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    val oBmp = BitmapFactory.decodeStream(inputStream)
                    binding.imageProduct?.setImageBitmap(oBmp)
                    inputStream?.close()
                })
            })
        }, fun (){})
        binding.btnAddToCart.setOnClickListener {
            NewRelic.recordBreadcrumb("ClickItemAdd")
            APIUtils.addToCart(this, productId, 1, {
                runOnUiThread {
                    NewRelic.recordBreadcrumb("ItemAdded")
                    Toast.makeText(this@ProductDetailActivity, "追加しました", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }, {
                Toast.makeText(this@ProductDetailActivity, "追加できませんでした", Toast.LENGTH_SHORT).show()
            })
        }
        binding.btnBack.setOnClickListener {
            finish()
        }


    }
}