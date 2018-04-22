package com.dubiel.sample.kotlinimagegallery.retrofit

import android.content.Context
import com.dubiel.sample.kotlinimagegallery.R
import okhttp3.*
import java.io.IOException
import java.nio.charset.Charset


class MockClient(context : Context) : Interceptor{
    val mContext : Context = context

    override fun intercept(chain: Interceptor.Chain?): Response {
        val response = readJsonFile()
        return Response.Builder()
                .code(200)
                .message(response)
                .request(chain?.request())
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(MediaType.parse("application/json"), response?.toByteArray()))
                .addHeader("content-type", "application/json")
                .build()
    }

    fun readJsonFile() : String? {
        var json: String? = null
        try {
            val `is` = mContext.assets.open(mContext.resources.getString(R.string.json_file))
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}