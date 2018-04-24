package com.dubiel.sample.kotlinimagegallery

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.view.LayoutInflater


class ImageGalleryViewAdapter(private val mContext : Context, private val images: Array<GalleryImage>) :
        RecyclerView.Adapter<ImageGalleryViewAdapter.ImageGalleryViewHolder>(){

    override fun onBindViewHolder(holder: ImageGalleryViewHolder, position: Int) {
        holder.mImageView.setImageResource(mContext.resources.getIdentifier(images.get(position).name, "drawable", mContext.packageName));
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryViewHolder {
        val inflater = parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = if(viewType == 0) inflater.inflate(R.layout.grid_item, parent, false) else
            inflater.inflate(R.layout.grid_item, parent, false)
        return ImageGalleryViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ImageGalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var mImageView: ImageView

        init {
            mImageView = itemView.findViewById(R.id.image_view)
        }
    }
}