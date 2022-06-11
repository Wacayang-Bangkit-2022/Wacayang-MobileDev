package com.c22_pc383.wacayang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c22_pc383.wacayang.data.Comment
import com.c22_pc383.wacayang.databinding.CommentItemBinding
import com.c22_pc383.wacayang.helper.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.apache.commons.lang3.StringEscapeUtils

class ListCommentAdapter(private val listItem: List<Comment>) :
    RecyclerView.Adapter<ListCommentAdapter.ListCommentViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var binding: CommentItemBinding

    class ListCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCommentViewHolder {
        binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListCommentViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ListCommentViewHolder, position: Int) {
        listItem[position].let { item ->
            holder.apply {
                binding.itemTitle.text = item.userName
                binding.itemSubtitle.text = Utils.convertToLocalDateTime(item.createdAt)

                val fromUnicode = StringEscapeUtils.unescapeJava(item.comment)
                binding.itemDesc.text = fromUnicode

                Glide.with(itemView.context)
                    .load(item.userPhoto)
                    .placeholder(Utils.getCircularProgressDrawable(itemView.context))
                    .circleCrop()
                    .into(binding.itemImage)

                binding.trashButton.isVisible = item.userId == Firebase.auth.currentUser?.uid
                binding.trashButton.setOnClickListener { onItemClickCallback.onItemClicked(item, position) }
            }
        }
    }

    override fun getItemCount(): Int = listItem.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(item: Comment, position: Int)
    }
}