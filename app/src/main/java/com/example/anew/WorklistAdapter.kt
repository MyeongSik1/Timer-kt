package com.example.anew

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorklistAdapter(val context: Context, val worklist : ArrayList<TimeLog>, val statrin : Int) : RecyclerView.Adapter<WorklistAdapter.Holder>() {
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
    inner class Holder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val work_item =  itemView.findViewById<TextView>(R.id.text_work)
        val time_item  = itemView.findViewById<TextView>(R.id.text_timer)
        val clear_image = itemView.findViewById<ImageView>(R.id.image_clear)

        fun bind(worklist : TimeLog ,context: Context, statrin: Int) {
            if(statrin == 2) {
                work_item.text = worklist.refkey
                time_item.text = worklist.value
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.work_rv_item, parent, false)
        return Holder(view)
    }


    override fun getItemCount(): Int {
        return worklist.size
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(worklist[position], context,statrin)
        holder.clear_image.setOnClickListener {
            itemClickListner.onClick(it,position)
        }
    }


}

