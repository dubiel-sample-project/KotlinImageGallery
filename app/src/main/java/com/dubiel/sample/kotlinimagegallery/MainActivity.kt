package com.dubiel.sample.kotlinimagegallery

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import com.dubiel.sample.kotlinimagegallery.retrofit.GalleryImages
import com.dubiel.sample.kotlinimagegallery.retrofit.ImageGalleryClient
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

data class GalleryImage(var name : String)
data class GalleryImages(var items: Array<GalleryImage>)

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.getSimpleName()

    var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageGalleryClient = ImageGalleryClient(applicationContext)

        subscription = imageGalleryClient.getImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    images.items.forEach { image -> System.out.println(image.name) }
//                    System.out.println(images.javaClass.toString())
//                    System.out.println(images.items.size)
                })

        val gridView: GridView = findViewById(R.id.gridview)
        gridView.adapter = ImageAdapter(this)

        gridView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, v, position, id ->
                    Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
                }
    }
}
