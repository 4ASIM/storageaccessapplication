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

class MediaAdapter(private val context: Context, private var items: List<MediaModel>, private val mediaType: MediaType) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): MediaModel = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: MediaViewHolder

        if (convertView == null) {
            // Choose layout based on media type
            view = if (mediaType == MediaType.DOCUMENT || mediaType == MediaType.CONTACT) {
                LayoutInflater.from(context).inflate(R.layout.cv_showdocuments, parent, false)
            } else {
                LayoutInflater.from(context).inflate(R.layout.cv_showimages, parent, false)
            }
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
        private val imageView: ImageView? = itemView.findViewById(R.id.iv_image)
        private val iconView: ImageView? = itemView.findViewById(R.id.iv_icon)
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val sizeTextView: TextView = itemView.findViewById(R.id.tv_size)

        fun bind(item: MediaModel) {
            when (mediaType) {
                MediaType.DOCUMENT -> {
                    iconView?.setImageResource(R.drawable.doc)
                    imageView?.visibility = View.GONE
                }
                MediaType.CONTACT -> {
                    iconView?.setImageResource(R.drawable.contacts)
                    imageView?.visibility = View.GONE
                    sizeTextView.text = item.mediaSize
                }
                MediaType.AUDIO -> {
                    iconView?.setImageResource(R.drawable.volume)
                }
                else -> {
                    Glide.with(context)
                        .load(item.mediaUri)
                        .placeholder(R.drawable.animated_loader_gif)
                        .into(imageView!!)
                    iconView?.visibility = View.GONE

                }
            }

            nameTextView.text = item.mediaName
            if (mediaType != MediaType.CONTACT) {
                sizeTextView.text = item.mediaSize
            } else {
                sizeTextView.text = item.mediaSize
            }
        }
    }

    fun updateItems(newItems: List<MediaModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
