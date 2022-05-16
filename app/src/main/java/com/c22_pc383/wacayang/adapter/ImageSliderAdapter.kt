package com.c22_pc383.wacayang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.c22_pc383.wacayang.databinding.ImageItemBinding
import com.c22_pc383.wacayang.helper.Utils
import java.util.*

class ImageSliderAdapter(
    private val context: Context,
    private val imageUrls: List<String>): PagerAdapter() {

    private lateinit var binding: ImageItemBinding

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding = ImageItemBinding.inflate(LayoutInflater.from(context), container, false)

        imageUrls[position].let { url ->
            Glide.with(binding.itemImage.context)
                .load(url)
                .placeholder(Utils.getCircularProgressDrawable(binding.itemImage.context))
                .into(binding.itemImage)
        }

        Objects.requireNonNull(container).addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
    override fun getCount(): Int = imageUrls.size
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object` as LinearLayout
}