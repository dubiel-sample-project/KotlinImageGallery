package com.dubiel.sample.kotlinimagegallery

import android.content.Context
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.dubiel.sample.kotlinimagegallery.retrofit.GalleryImage

private val mThumbIds = arrayOf<Int>(
        R.drawable.sample_2, R.drawable.sample_0,
        R.drawable.sample_6, R.drawable.sample_3,
        R.drawable.sample_7, R.drawable.sample_1,
        R.drawable.sample_4, R.drawable.sample_2,
        R.drawable.sample_5, R.drawable.sample_7,
        R.drawable.sample_1, R.drawable.sample_6,
        R.drawable.sample_3, R.drawable.sample_4
)

class ImageAdapter (val images : Array<GalleryImage>, private val mContext : Context) : BaseAdapter() {
    val TAG = MainActivity::class.java.getSimpleName()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val inflater = LayoutInflater.from(mContext)
        val imageView: ImageView
//        var gridView: View

        if(convertView == null) {
            imageView = ImageView(mContext)

//            gridView = View(mContext);

            // get layout from grid_item.xml ( Defined Below )

//            imageView = inflater.inflate( R.layout.grid_item , null);

            val type = getItemViewType(position)
            if (type == 0) {
                imageView.layoutParams = ViewGroup.LayoutParams(170, 170)
//                convertView = inflater.inflate(R.layout.big_layout, parent, false)
            } else {
                imageView.layoutParams = ViewGroup.LayoutParams(85, 85)
////                convertView = inflater.inflate(R.layout.small_layout, parent, false)
            }
//            val imageView = imageView
//                    .findViewById(R.id.grid_item_image) as ImageView

            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//            imageView.layoutParams = ViewGroup.LayoutParams(85, 85)
//            imageView.layoutParams = ViewGroup.LayoutParams(85, 85)
//            val lp = imageView.getLayoutParams()
//            if (position == 0) {
//                val sglp = lp as StaggeredGridLayoutManager.LayoutParams
//                sglp.setFullSpan(true)
//                imageView.setLayoutParams(sglp)
//            }

            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

//        imageView.setImageResource(images[position])
        imageView.setImageResource(mContext.resources.getIdentifier(images.get(position).name, "drawable", mContext.packageName))

//        try {
//            Glide.with(mContext)
//                    .download()
//                    .load()
//                    .into(imageView)
//        } catch (e: NullPointerException) {
//            Log.i(TAG, "image not found")
//        }

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