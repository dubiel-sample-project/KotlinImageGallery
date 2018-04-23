package com.dubiel.sample.kotlinimagegallery

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dubiel.sample.kotlinimagegallery.retrofit.ImageGalleryClient
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
//import android.support.v7.widget.GridLayoutManager
//import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.support.v7.widget.GridLayout
import android.widget.ImageView
import android.view.Gravity
import android.widget.ScrollView

data class GalleryImage(var name : String)
data class GalleryImages(var items: Array<GalleryImage>)

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.getSimpleName()
    lateinit var gridLayout: GridLayout
//    var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scrollView: ScrollView = findViewById(R.id.scroll_view)
        scrollView.setSmoothScrollingEnabled(true)

        gridLayout = findViewById(R.id.grid_layout)

        ImageGalleryClient(this).getImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    val view = getGridItem(images.items.first().name,
                            resources.displayMetrics.widthPixels / 3 * 2,
                            null,
                            2,
                            2)
                    gridLayout.addView(view)

                    images.items.drop(1).forEach { image ->
                        gridLayout.addView(getGridItem(image.name,
                                resources.displayMetrics.widthPixels / 3,
                                view.layoutParams.height / 2,
                                1,
                                1))
                    }
//                    System.out.println(images.javaClass.toString())
//                    System.out.println(images.items.size)
                })
    }

    fun getGridItem(name: String, width: Int, height: Int?, rowspan: Int, colspan: Int) : View  {
        val inflater : LayoutInflater = LayoutInflater.from(this)
        val itemView : View = inflater.inflate(R.layout.grid_item, gridLayout, false)

        val imageView : ImageView = itemView.findViewById(R.id.image_view)
        imageView.setImageResource(resources.getIdentifier(name, "drawable", packageName))

        var param = GridLayout.LayoutParams()
//        param.width = GridLayout.LayoutParams.WRAP_CONTENT
        param.width = width
//        param.height = GridLayout.LayoutParams.WRAP_CONTENT
        if(height != null) {
            param.height = height
        }
        param.rightMargin = 5
        param.topMargin = 5
        param.setGravity(Gravity.CENTER)
        param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, rowspan)
        param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, colspan)
        itemView.setLayoutParams(param)

        return itemView
    }
}
