package com.dubiel.sample.kotlinimagegallery.retrofit

import com.dubiel.sample.kotlinimagegallery.MainActivity
import retrofit2.http.GET
import rx.Single

interface ImageGalleryService {
    @GET("images")
    fun queryImages(): Single<MainActivity>
}