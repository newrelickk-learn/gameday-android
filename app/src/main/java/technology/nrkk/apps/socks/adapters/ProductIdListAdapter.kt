package com.example.listviewsample

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import technology.nrkk.apps.socks.CartActivity
import technology.nrkk.apps.socks.R
import technology.nrkk.apps.socks.models.Product
import technology.nrkk.apps.socks.utils.APIUtils
import java.io.InputStream


class ProductIdListAdapter(context: Context, var productIds: List<String>): ArrayAdapter<String>(context, 0, productIds) {
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val productId = productIds[position]
        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.product_list, parent, false)
        }
        val imageView = view?.findViewById<ImageView>(R.id.image)
        APIUtils.getItem(context, productId, fun (product) {
            Handler(Looper.getMainLooper()).post(Runnable {
                val nameText = view?.findViewById<TextView>(R.id.name)
                nameText?.text = product.name
                val priceText = view?.findViewById<TextView>(R.id.price)
                priceText?.text = product.price.toString()
            })
            APIUtils.getImageStream(context, product.imageUrl0, fun(inputStream: InputStream?) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    val oBmp = BitmapFactory.decodeStream(inputStream)
                    imageView?.setImageBitmap(oBmp)
                    inputStream?.close()
                })
            })
        }) {}
        return view!!
    }

}