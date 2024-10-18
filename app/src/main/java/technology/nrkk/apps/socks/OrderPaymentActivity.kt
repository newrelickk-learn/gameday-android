package technology.nrkk.apps.socks

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import technology.nrkk.apps.socks.databinding.ActivityOrderPaymentBinding
import technology.nrkk.apps.socks.models.Order
import technology.nrkk.apps.socks.utils.APIUtils


class OrderPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPaymentBinding
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

        binding = ActivityOrderPaymentBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {
            val order = intent.extras?.getSerializable<Order>("Order", Order::class.java)
            binding.btnConfirm.setOnClickListener {
                if (order == null) {
                    return@setOnClickListener
                }
                order.paymentType = binding.spinnerPaymentType.selectedItem.toString()
                order.couponCode = binding.editTextCoupon.text.toString()
                APIUtils.confirmOrder(applicationContext, order, fun(newOrder: Order) {
                    runOnUiThread {
                        val intent = Intent(this, OrderConfirmActivity::class.java)
                        intent.putExtra("Order", newOrder)
                        getContent.launch(intent)
                        Toast.makeText(
                            this@OrderPaymentActivity,
                            newOrder.orderStage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {}
            }
            binding.btnBack.setOnClickListener {
                finish()
            }
            binding.textTotal.text = order?.cart?.totalPrice.toString()
            binding.textAmount.text = order?.cart?.amount.toString()
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
            adapter.add("現金")
            adapter.add("クレジットカード")
            binding.spinnerPaymentType.adapter = adapter
        } catch (e: Exception) {
            Toast.makeText(this@OrderPaymentActivity, e.toString(), Toast.LENGTH_SHORT)
        }
    }
}