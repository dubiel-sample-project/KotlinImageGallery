package com.dubiel.sample.kotlinimagegallery

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import com.dubiel.sample.kotlinimagegallery.retrofit.ImageGalleryClient
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.GridLayout


data class GalleryImage(var name : String)
data class GalleryImages(var items: Array<GalleryImage>)

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.getSimpleName()
//    lateinit var gridView: GridView
//    lateinit var gridView: GridView
//    var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gl : GridLayout = findViewById(R.id.grid_layout_main)

//        val layoutManager = GridLayoutManager(this, 3)
//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return if (position == 0) 2 else 1
//            }
//        }

        ImageGalleryClient(this).getImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
//                    val gridView: GridView = findViewById(R.id.gridview)
//                    val recyclerView : RecyclerView = findViewById(R.id.recyclerview)
//                    recyclerView.layoutManager = layoutManager
//                    recyclerView.adapter = ImageGalleryViewAdapter(this, images.items)
//                    gridView = findViewById(R.id.gridview)
//                    gridView.adapter = ImageAdapter(this, images.items)
//
//                    recyclerView.on
//                            AdapterView.OnItemClickListener { parent, v, position, id ->
//                                Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
//                            }
//                    gridView.onItemClickListener =
//                            AdapterView.OnItemClickListener { parent, v, position, id ->
//                                Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
//                            }
//                    imageAdapter.notifyDataSetChanged()
//                    System.out.println(images.javaClass.toString())
//                    System.out.println(images.items.size)
                })


    }
}
