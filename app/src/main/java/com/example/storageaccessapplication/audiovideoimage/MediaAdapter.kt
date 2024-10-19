package com.example.storageaccessapplication.audiovideoimage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.storageaccessapplication.R

class MediaAdapter(private val context: Context, private var items: List<MediaModel>) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): MediaModel = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: MediaViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.cv_showimages, parent, false)
            viewHolder = MediaViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as MediaViewHolder
        }

        viewHolder.bind(getItem(position))
        return view
    }

    inner class MediaViewHolder(itemView: View) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_image)
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val sizeTextView: TextView = itemView.findViewById(R.id.tv_size)

        fun bind(item: MediaModel) {
            Glide.with(context)
                .load(item.mediaUri)
                .into(imageView)

            nameTextView.text = item.mediaName
            sizeTextView.text = item.mediaSize
        }
    }

    fun updateItems(newItems: List<MediaModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
