package kerry.express.th.mobile.camera_test.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kerry.express.th.mobile.camera_test.R
import kotlinx.android.synthetic.main.item_rv_list.view.*
import java.io.File

class RvAdapter(var originList: Array<File>,var compressedList: Array<File>):RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (originList.isNotEmpty()) originList.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var originSize = (originList[position].length()/1024.0)
        var originSizeType = " KB"
        if(originSize>1024) {
            originSize /= 1024.0
            originSizeType = " MB"
            
        }
        holder.itemView.tv_size.text = String.format("%.1f",originSize) + originSizeType
        Glide.with(holder.itemView.context).load(originList[position].absolutePath)
                .apply(RequestOptions()
                        .centerCrop())
                .into(holder.itemView.image)

        var compressedSize = (compressedList[position].length()/1024.0)
        var compressedSizeType = " KB"
        if(compressedSize>1024) {
            compressedSize /= 1024.0
            compressedSizeType = " MB"
        }
        holder.itemView.tv_size2.text = String.format("%.1f",compressedSize) + compressedSizeType
        Glide.with(holder.itemView.context).load(compressedList[position].absolutePath)
                .apply(RequestOptions()
                        .centerCrop())
                .into(holder.itemView.image2)
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

}