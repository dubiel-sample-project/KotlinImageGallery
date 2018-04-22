package com.dubiel.sample.kotlinimagegallery.retrofit

import android.content.Context
import com.dubiel.sample.kotlinimagegallery.GalleryImages
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Single
import rx.schedulers.Schedulers

class ImageGalleryClient {

    private val URL = "http://www.example.com/"
    private var imageGalleryService : ImageGalleryService

    constructor(context: Context) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(MockClient(context)).build()

        val gson =
                //                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                GsonBuilder().create()
        val retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        imageGalleryService = retrofit.create<ImageGalleryService>(ImageGalleryService::class.java)
    }

    fun getImages(): Single<GalleryImages> {
        return imageGalleryService.queryImages()
    }
}