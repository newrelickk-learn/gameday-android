package technology.nrkk.apps.socks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
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
import technology.nrkk.apps.socks.models.Product
import technology.nrkk.apps.socks.models.User
import technology.nrkk.apps.socks.utils.APIUtils

class MainActivity : AppCompatActivity() {
    private lateinit var goToCartButton: Button
    val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewRelic.recordBreadcrumb("OpenMain")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        goToCartButton = findViewById(R.id.btn_gotocart)
        goToCartButton.setOnClickListener {
            runOnUiThread {
                NewRelic.recordBreadcrumb("ClickGoToCart")
                val intent = Intent(this, CartActivity::class.java)
                getContent.launch(intent)
            }
        }

        APIUtils.getItems(applicationContext, "", fun (items: List<Product>) {
            runOnUiThread {
                val productListAdapter = ProductListAdapter(applicationContext, items)
                val listView = findViewById<ListView>(R.id.product_list)
                listView.adapter = productListAdapter
                listView.onItemClickListener =  ListItemClick()
            }
        }) {}
    }

    private inner class ListItemClick : AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            val product = parent?.getItemAtPosition(pos) as Product
            val context = this@MainActivity
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("productId", product.id)
            context.startActivity(intent)
        }
    }
}