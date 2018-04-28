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
import android.util.Log
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso


data class GalleryImage(var name : String)
data class GalleryImages(var items: Array<GalleryImage>)

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.getSimpleName()
    val SELECT_IMAGE = 1

    lateinit var mGridLayout: GridLayout
    var mGridItemWidth : Int = 0
    var mGridItemHeight : Int = 0

    class ViewOnDragListener : View.OnDragListener {
        override fun onDrag(v: View?, event: DragEvent?): Boolean {
            val action : Int? = event?.action;

            when(action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    if(event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        val iv : ImageView? = v?.findViewById(R.id.image_view)
//                        val cv : CardView? = v as CardView
//                        cv?.setCardBackgroundColor(Color.TRANSPARENT)
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
//                    val item = event.clipData.getItemAt(0)

                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.clearColorFilter()
                    iv?.invalidate()

//                    val cv : CardView? = v as CardView
//                    cv?.setCardBackgroundColor(Color.WHITE)
//                    cv?.invalidate()

                    val source : View = event.localState as View
                    val gl : GridLayout = source.parent as GridLayout

                    System.out.println("source index: " + gl.indexOfChild(source))
                    System.out.println("target index: " + gl.indexOfChild(v))

                    gl.removeView(source)
                    gl.addView(source, gl.indexOfChild(v))

                    v?.setVisibility(View.VISIBLE);
                    source?.setVisibility(View.VISIBLE);

                    return true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.clearColorFilter()
                    iv?.invalidate()

                    System.out.println("drag result: " + event.result)
                    val source : View = event.localState as View
                    System.out.println("source: " + source)

//                    val cv : CardView? = v as CardView
//                    cv?.setCardBackgroundColor(Color.WHITE)
//                    cv?.invalidate()

//                    source?.setVisibility(View.VISIBLE);
                    v?.setVisibility(View.VISIBLE);

//                    if(event.result) {
//                    }
                    return true
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scrollView: ScrollView = findViewById(R.id.scroll_view)
        scrollView.setSmoothScrollingEnabled(true)

        mGridItemWidth = resources.displayMetrics.widthPixels / 3
        mGridItemHeight = resources.displayMetrics.heightPixels / 2
        mGridLayout = findViewById(R.id.grid_layout)

        ImageGalleryClient(this).getImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    mGridLayout.addView(getGridItem("file:///android_asset/" + images.items.first().name + ".jpg",
                            mGridItemWidth * 2 - 12,
                            mGridItemHeight,
                            2,
                            2))

//                    val view : View = mGridLayout.getChildAt(0)

                    images.items.drop(1).forEach { image ->
                        mGridLayout.addView(getGridItem("file:///android_asset/" + image.name + ".jpg",
                                mGridItemWidth - 8,
                                mGridItemHeight / 2,
                                1,
                                1))
                    }

//                    val viewTreeObserver = view.getViewTreeObserver()
//                    if (viewTreeObserver.isAlive()) {
//                        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                            override fun onGlobalLayout() {
//                                view.getViewTreeObserver().removeOnGlobalLayoutListener(this)
////                                mGridItemHeight = view.getHeight() / 2
//                                images.items.drop(1).forEach { image ->
//                                    mGridLayout.addView(getGridItem(image.name,
//                                            mGridItemWidth - 12,
//                                            mGridItemHeight / 2,
//                                            1,
//                                            1))
//                                }
//                            }
//                        })
//                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        System.out.println("menu id: " + id)

        when (id) {
            R.id.action_add_image -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE)
            }
            R.id.action_delete -> System.out.println("delete image")
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_IMAGE) {
                val itemView : CardView = getGridItem(data?.data.toString(), mGridItemWidth - 8,
                        mGridItemHeight / 2, 1, 1) as CardView
                val imageView: ImageView = itemView.findViewById(R.id.image_view)

                try {
                    Picasso.with(applicationContext).load(data?.data.toString())
                            .into(imageView)
                    mGridLayout.addView(itemView)
                } catch (e: Exception) {
                    Log.i(TAG, "error " + e.message)
                }
            }
        }
    }

    fun getGridItem(uri: String, width: Int, height: Int, rowspan: Int, colspan: Int) : View  {
        val inflater : LayoutInflater = LayoutInflater.from(this)
        val itemView : View = inflater.inflate(R.layout.grid_item, mGridLayout, false)

        var param = GridLayout.LayoutParams()
        param.width = width
        if(height != null) {
            param.height = height
        }
        param.rightMargin = 5
        param.topMargin = 5
        param.setGravity(Gravity.CENTER)
        param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, rowspan)
        param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, colspan)
        itemView.setLayoutParams(param)

        itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val newFragment : ImageDialogFragment = ImageDialogFragment.newInstance(uri)

                val fm = fragmentManager
                newFragment.show(fm, "Dialog Fragment")
            }
        })
        itemView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                val dragData : ClipData = ClipData.newPlainText("", "")
                val shadowBuilder : View.DragShadowBuilder = View.DragShadowBuilder(v);
                v?.startDrag(dragData, shadowBuilder, v, 0);
                v?.setVisibility(View.INVISIBLE);
                return true;
            }
        })

        itemView.setOnDragListener(ViewOnDragListener())

        val imageView: ImageView = itemView.findViewById(R.id.image_view)
            Picasso.with(applicationContext).load(Uri.parse(uri))
                    .into(imageView)

        return itemView
    }
}
