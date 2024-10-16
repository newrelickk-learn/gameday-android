package technology.nrkk.apps.socks

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import technology.nrkk.apps.socks.databinding.ActivityOrderConfirmBinding
import technology.nrkk.apps.socks.databinding.ActivityOrderPaymentBinding
import technology.nrkk.apps.socks.models.Order
import technology.nrkk.apps.socks.utils.APIUtils

class OrderConfirmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderConfirmBinding
    val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderConfirmBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val order = intent.extras?.getSerializable<Order>("Order", Order::class.java)
        binding.btnConfirm.setOnClickListener {
            if (order == null) {
                return@setOnClickListener
            }
            APIUtils.purchase(applicationContext, order, fun (order: Order) {
                runOnUiThread {
                    val intent = Intent(this, OrderCompleteActivity::class.java)
                    getContent.launch(intent)
                }
            }) {}
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.textTotal.text = order?.cart?.totalPrice.toString()
        binding.textAmount.text = order?.cart?.amount.toString()
        binding.textCoupon.text = order?.couponCode.toString()
        binding.textPaymentType.text = order?.paymentType.toString()
    }
}