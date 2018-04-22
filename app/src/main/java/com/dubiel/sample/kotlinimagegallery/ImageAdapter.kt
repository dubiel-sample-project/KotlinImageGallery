package com.dubiel.sample.kotlinimagegallery

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.view.LayoutInflater

class ImageAdapter (private val mContext : Context, private val images: Array<GalleryImage>) : BaseAdapter() {
    val TAG = MainActivity::class.java.getSimpleName()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(mContext)
        var imageView: ImageView

        if(convertView == null) {
            when(position) {
                0, 1, 2 -> {
                    when(position) {
                        1, 2 -> {
                            imageView = inflater.inflate(R.layout.grid_item_small, parent, false) as ImageView
//                            imageView.layoutParams = ViewGroup.LayoutParams(170, 170)
                        }
                        else -> {
                            imageView = inflater.inflate(R.layout.grid_item_large, parent, false) as ImageView
//                            imageView.layoutParams = ViewGroup.LayoutParams(170, 170)
                        }
                    }
                }
                else -> {
                    imageView = inflater.inflate(R.layout.grid_item_small, parent, false) as ImageView
//                    imageView.layoutParams = ViewGroup.LayoutParams(85, 85)
                }
            }

            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(mContext.resources.getIdentifier(images.get(position).name, "drawable", mContext.packageName))

        return imageView
    }

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    override fun getCount(): Int = images.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }
}