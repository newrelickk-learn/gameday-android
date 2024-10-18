package technology.nrkk.apps.socks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listviewsample.ProductListAdapter
import com.newrelic.agent.android.NewRelic
import technology.nrkk.apps.socks.databinding.ActivityCartBinding
import technology.nrkk.apps.socks.databinding.ActivityProductDetailBinding
import technology.nrkk.apps.socks.models.Cart
import technology.nrkk.apps.socks.models.Order
import technology.nrkk.apps.socks.utils.APIUtils

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnPurchace.setOnClickListener {
            NewRelic.recordBreadcrumb("ClickGoToPurchace")
            APIUtils.startOrder(applicationContext, fun (order: Order) {
                runOnUiThread {
                    val intent = if (order.orderStage == "CONFIRM")
                        Intent(this, OrderConfirmActivity::class.java)
                    else
                        Intent(this, OrderPaymentActivity::class.java)

                    intent.putExtra("Order", order)
                    getContent.launch(intent)
                    Toast.makeText(this@CartActivity, order.orderStage, Toast.LENGTH_SHORT).show()
                }
            }) {}
        }
        binding.btnBack.setOnClickListener {
            goBack()
        }
        APIUtils.getCart(applicationContext, this::succeeded, this::failed)
    }

    private fun goBack() {
        runOnUiThread {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun succeeded(cart: Cart) {
        val productList = cart.items.mapNotNull {
            it.product
        }
        runOnUiThread {
            val productListAdapter = ProductListAdapter(applicationContext, productList)
            binding.productList.adapter = productListAdapter
        }
    }

    private fun failed() {

    }
}