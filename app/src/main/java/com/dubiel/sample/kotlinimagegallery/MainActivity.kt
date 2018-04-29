package com.dubiel.sample.kotlinimagegallery

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dubiel.sample.kotlinimagegallery.retrofit.ImageGalleryClient
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import android.support.v7.widget.GridLayout
import android.view.*
import android.widget.ImageView
import android.widget.ScrollView
import android.graphics.PorterDuff
import android.support.v7.widget.CardView
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import com.squareup.picasso.Picasso
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation


data class GalleryImage(var name : String)
data class GalleryImages(var items: Array<GalleryImage>)

class MainActivity : AppCompatActivity() {
    companion object {
        var GRID_ITEM_WIDTH : Int = 0
        var GRID_ITEM_HEIGHT : Int = 0
        var GRID_ITEM_LARGE_WIDTH : Int = 0
        var GRID_ITEM_LARGE_HEIGHT : Int = 0

        fun rescaleDeleteIcon(view : View?) {
            val deleteIcon: ImageView = view!!.findViewById(R.id.delete_icon)

            val anim = ScaleAnimation(
                    1f, 0f,
                    1f, 0f,
                    Animation.RELATIVE_TO_SELF, .5f,
                    Animation.RELATIVE_TO_SELF, .5f)
            anim.setInterpolator(LinearInterpolator())
            anim.setFillAfter(true)
            anim.setDuration(0)
            deleteIcon.startAnimation(anim)
        }
        fun getGridLayoutParams(width : Int, height : Int,
                                rowspan: Int, colspan: Int,
                                rightMargin : Int = 4, topMargin : Int = 4) : GridLayout.LayoutParams {
            var param = GridLayout.LayoutParams()
            param.width = width
            param.height = height
            param.rightMargin = rightMargin
            param.topMargin = topMargin
            param.setGravity(Gravity.CENTER)
            param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, rowspan)
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, colspan)
            return param
        }
    }

    val TAG = MainActivity::class.java.getSimpleName()
    val SELECT_IMAGE = 1
    var mDeleteIconVisible : Boolean = false

    lateinit var mGridLayout: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scrollView: ScrollView = findViewById(R.id.scroll_view)
        scrollView.setSmoothScrollingEnabled(true)

        GRID_ITEM_WIDTH = resources.displayMetrics.widthPixels / 3 - 8
        GRID_ITEM_HEIGHT = resources.displayMetrics.heightPixels / 4 - 2

        GRID_ITEM_LARGE_WIDTH = resources.displayMetrics.widthPixels / 3 * 2 - 12
        GRID_ITEM_LARGE_HEIGHT = resources.displayMetrics.heightPixels / 2

        mGridLayout = findViewById(R.id.grid_layout)

        ImageGalleryClient(this).getImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    mGridLayout.addView(getGridItem("file:///android_asset/" + images.items.first().name,
                            GRID_ITEM_LARGE_WIDTH,
                            GRID_ITEM_LARGE_HEIGHT,
                            2,
                            2))

                    images.items.drop(1).forEach { image ->
                        mGridLayout.addView(getGridItem("file:///android_asset/" + image.name,
                                GRID_ITEM_WIDTH,
                                GRID_ITEM_HEIGHT,
                                1,
                                1))
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_add_image -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE)
            }
            R.id.action_delete -> {
                var fromX = 0f
                var toX = 0f
                var fromY = 0f
                var toY = 0f

                if(mDeleteIconVisible) {
                    fromX = 1f
                    toX = 0f
                    fromY = 1f
                    toY = 0f
                } else {
                    fromX = 0f
                    toX = 1f
                    fromY = 0f
                    toY = 1f
                }
                for(i in 0..mGridLayout.childCount - 1) {
                    val v: View = mGridLayout.getChildAt(i)
                    val deleteIcon: ImageView = v.findViewById(R.id.delete_icon)
                    val anim = ScaleAnimation(
                            fromX, toX,
                            fromY, toY,
                            Animation.RELATIVE_TO_SELF, .5f,
                            Animation.RELATIVE_TO_SELF, .5f)
                    anim.setInterpolator(BounceInterpolator())
                    anim.setFillAfter(true)
                    anim.setDuration(1000)
                    deleteIcon.startAnimation(anim)
                }
                mDeleteIconVisible = !mDeleteIconVisible
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_IMAGE) {
                val itemView : CardView = getGridItem(data?.data.toString(), GRID_ITEM_LARGE_WIDTH,
                        GRID_ITEM_LARGE_HEIGHT, 2, 2) as CardView
                try {
                    val imageView: ImageView = itemView.findViewById(R.id.image_view)
                    Picasso.with(applicationContext).load(data?.data.toString())
                            .into(imageView)
                    mGridLayout.getChildAt(0).setLayoutParams(getGridLayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT, 1, 1))
                    mGridLayout.addView(itemView, 0)
                } catch (e: Exception) {
                    Log.i(TAG, "error loading new image, " + e.message)
                }
            }
        }
    }

    fun getGridItem(uri: String, width: Int, height: Int, rowspan: Int, colspan: Int) : View  {
        val inflater : LayoutInflater = LayoutInflater.from(this)
        val itemView : View = inflater.inflate(R.layout.grid_item, mGridLayout, false)

        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        Picasso.with(applicationContext).load(Uri.parse(uri)).into(imageView)

        itemView.setLayoutParams(getGridLayoutParams(width, height, rowspan, colspan))

        val handler = Handler()
        val runnable = Runnable {
            val dragData : ClipData = ClipData.newPlainText("", "")
            val shadowBuilder : View.DragShadowBuilder = View.DragShadowBuilder(itemView);
            itemView?.startDrag(dragData, shadowBuilder, itemView, 0);
            itemView?.setVisibility(View.INVISIBLE);
        }
        itemView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handler.postDelayed(runnable, 150)
                    }
                    MotionEvent.ACTION_UP -> {
                        v?.setVisibility(View.VISIBLE)
                        if(event.eventTime - event.downTime < 150) {
                            handler.removeCallbacks(runnable)
                            val newFragment : ImageDialogFragment = ImageDialogFragment.newInstance(uri)
                            val fm = fragmentManager
                            newFragment.show(fm, "Dialog Fragment")
                        }
                    }
                    MotionEvent.ACTION_SCROLL -> return false
                    MotionEvent.ACTION_MOVE -> return false
                    else -> {
                        handler.removeCallbacks(runnable)
                    }
                }
                return false
            }
        })

        val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)
        rescaleDeleteIcon(itemView)

        deleteIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(mDeleteIconVisible) {
                    if(mGridLayout.indexOfChild(itemView) == 0) {
                        mGridLayout.getChildAt(1).setLayoutParams(getGridLayoutParams(GRID_ITEM_LARGE_WIDTH, GRID_ITEM_LARGE_HEIGHT, 2, 2))
                    }
                    mGridLayout.removeView(itemView)
                }
            }
        })

        itemView.setOnDragListener(ViewOnDragListener())

        return itemView
    }

    class ViewOnDragListener : View.OnDragListener {
        override fun onDrag(v: View?, event: DragEvent?): Boolean {
            val action : Int? = event?.action;

            when(action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    if(event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        val iv : ImageView? = v?.findViewById(R.id.image_view)
                        iv?.setColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN)
                        iv?.invalidate()
                        return true
                    }
                    return false
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.setColorFilter(Color.GREEN, PorterDuff.Mode.LIGHTEN)
                    iv?.invalidate()
                    return true
                }
                DragEvent.ACTION_DRAG_LOCATION -> return true
                DragEvent.ACTION_DRAG_EXITED -> {
                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.setColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN)
                    iv?.invalidate()
                    return true
                }
                DragEvent.ACTION_DROP -> {
                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.clearColorFilter()
                    iv?.invalidate()

                    val source : View = event.localState as View
                    val gl : GridLayout = source.parent as GridLayout
//
                    if(gl.indexOfChild(v) == 0) {
                        v?.setLayoutParams(getGridLayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT, 1, 1))
                        source.setLayoutParams(getGridLayoutParams(GRID_ITEM_LARGE_WIDTH, GRID_ITEM_LARGE_HEIGHT, 2, 2))

                        gl.removeView(source)
                        gl.addView(source, 0)
                    } else if(gl.indexOfChild(source) == 0) {
                        gl.getChildAt(1).setLayoutParams(getGridLayoutParams(GRID_ITEM_LARGE_WIDTH, GRID_ITEM_LARGE_HEIGHT, 2, 2))
                        source?.setLayoutParams(getGridLayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT, 1, 1))

                        gl.removeView(source)
                        gl.addView(source, gl.indexOfChild(v) + 1)
                    } else {
                        val viewIndex : Int = gl.indexOfChild(v)
                        val sourceIndex : Int = gl.indexOfChild(source)

                        gl.removeView(source)
                        gl.addView(source, viewIndex)

                        gl.removeView(v)
                        gl.addView(v, sourceIndex)
                    }

                    v?.setVisibility(View.VISIBLE);
                    source?.setVisibility(View.VISIBLE);
                    rescaleDeleteIcon(v)
                    rescaleDeleteIcon(source)

                    return true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.clearColorFilter()
                    iv?.invalidate()

                    val handler = Handler()
                    val runnable = Runnable {
                        val gl : GridLayout = v?.parent as GridLayout
                        for(i in 0..gl.childCount - 1) {
                            val view: View = gl.getChildAt(i)
                            view?.setVisibility(View.VISIBLE);
                        }
                    }

                    if(!event.result) {
                        handler.postDelayed(runnable, 100)
                    }

                    v?.setVisibility(View.VISIBLE);
                    return true
                }
            }
            return false
        }
    }
}
