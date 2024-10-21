package com.example.storageaccessapplication.audiovideoimage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.storageaccessapplication.R
import com.example.storageaccessapplication.databinding.CvShowdocumentsBinding
import com.example.storageaccessapplication.databinding.CvShowimagesBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class MediaAdapter(
    private val context: Context,
    private var items: List<MediaModel>,
    private val mediaType: MediaType
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): MediaModel = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: MediaViewHolder

        if (convertView == null) {
            if (mediaType == MediaType.DOCUMENT || mediaType == MediaType.CONTACT) {
                val binding = CvShowdocumentsBinding.inflate(LayoutInflater.from(context), parent, false)
                viewHolder = MediaViewHolder(binding)
                view = binding.root
            }
            else {
                val binding = CvShowimagesBinding.inflate(LayoutInflater.from(context), parent, false)
                viewHolder = MediaViewHolder(binding)
                view = binding.root
            }
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as MediaViewHolder
        }

        viewHolder.bind(getItem(position))
        return view
    }

    inner class MediaViewHolder {
        private var documentBinding: CvShowdocumentsBinding? = null
        private var imageBinding: CvShowimagesBinding? = null

        constructor(binding: CvShowdocumentsBinding) {
            this.documentBinding = binding
        }

        constructor(binding: CvShowimagesBinding) {
            this.imageBinding = binding
        }

        fun bind(item: MediaModel) {
            when (mediaType) {
                MediaType.DOCUMENT -> {
                    documentBinding?.ivIcon?.setImageResource(R.drawable.doc)
                    documentBinding?.tvName?.text = item.mediaName
                    documentBinding?.tvSize?.text = item.mediaSize
                }
                MediaType.CONTACT -> {
                    documentBinding?.ivIcon?.setImageResource(R.drawable.contacts)
                    documentBinding?.tvName?.text = item.mediaName
                    documentBinding?.tvSize?.text = item.mediaSize
                }
                MediaType.AUDIO -> {
                    imageBinding?.tvName?.text = item.mediaName
                    imageBinding?.tvSize?.text = item.mediaSize
                }
                MediaType.VIDEO -> {
                    Glide.with(context)
                        .load(item.mediaUri)
                        .placeholder(R.drawable.animated_loader_gif)
                        .into(imageBinding!!.ivImage)
                    imageBinding?.ivImagess?.setImageResource(R.drawable.video)
                    imageBinding?.tvName?.text = item.mediaName
                    imageBinding?.tvSize?.text = item.mediaSize
                }
                else -> {
                    Glide.with(context)
                        .load(item.mediaUri)
                        .placeholder(R.drawable.animated_loader_gif)
                        .into(imageBinding!!.ivImage)
                    imageBinding?.tvName?.text = item.mediaName
                    imageBinding?.tvSize?.text = item.mediaSize
                }
            }
        }
    }
    fun updateItems(newItems: List<MediaModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
