package com.c22_pc383.wacayang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.databinding.WayangItemBinding
import com.c22_pc383.wacayang.helper.Utils

class ListWayangAdapter(private val listItem: List<Wayang>) :
    RecyclerView.Adapter<ListWayangAdapter.ListWayangViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var binding: WayangItemBinding

    class ListWayangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListWayangViewHolder {
        binding = WayangItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListWayangViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ListWayangViewHolder, position: Int) {
        listItem[position].let { item ->
            holder.apply {
                binding.itemTitle.text = item.name
                binding.itemDesc.text = item.description

                Glide.with(itemView.context)
                    .load(Utils.splitImageUrls(item.image)[0])
                    .placeholder(Utils.getCircularProgressDrawable(itemView.context))
                    .into(binding.itemImage)

                itemView.setOnClickListener { onItemClickCallback.onItemClicked(item, position) }
            }
        }
    }

    override fun getItemCount(): Int = listItem.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: Wayang, position: Int)
    }
}