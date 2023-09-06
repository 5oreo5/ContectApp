package com.android.contectapp


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.contectapp.databinding.ActivityRecyclerviewItemListBinding

class RecyclerviewAdapter(val Item: MutableList<Item>) :
    RecyclerView.Adapter<RecyclerviewAdapter.Holder>() {

    interface OnItemClickListener {
        fun onItemClick(data: Item, position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }


    var itemClick: ItemClick? = null
    var log = "로그"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ActivityRecyclerviewItemListBinding.inflate(inflater, parent, false)
        Log.d(log, "onCreateViewHolder called")
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val pos = Item[position]
        holder.name.text = pos.name
        holder.specialist.text = pos.specialist
        holder.image.setImageResource(pos.image)

        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return Item.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class Holder(val binding: ActivityRecyclerviewItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.recyclerviewName
        val specialist = binding.recyclerviewSpeciallist
        val image = binding.recyclerviewIvProfile
    }
}


