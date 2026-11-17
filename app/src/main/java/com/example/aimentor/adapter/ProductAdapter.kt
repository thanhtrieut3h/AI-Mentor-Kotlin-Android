package com.example.aimentor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aimentor.R
import com.example.aimentor.models.Product

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<Product> = emptyList()

    // Gọi hàm này khi có dữ liệu mới từ API
    fun submitList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    // ViewHolder: giữ tham chiếu tới các view của 1 dòng (item_product.xml)
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)

        fun bind(product: Product) {
            tvTitle.text = product.title
            tvCategory.text = product.category
            tvPrice.text = String.format(java.util.Locale.US, "$%.2f", product.price)
            tvRating.text = "★ ${product.rating.rate} (${product.rating.count} đánh giá)"

            Glide.with(itemView.context)
                .load(product.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_delete)
                .into(ivProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}
