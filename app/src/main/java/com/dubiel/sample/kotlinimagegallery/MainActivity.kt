package com.dubiel.sample.kotlinimagegallery

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


data class GalleryImage(var name : String)
data class GalleryImages(var items: Array<GalleryImage>)

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.getSimpleName()
    lateinit var gridLayout: GridLayout
//    var subscription: Subscription? = null

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
                    val item = event.clipData.getItemAt(0)
                    System.out.println("source: " + item.text)

                    val target : View = event.localState as View
                    System.out.println("target: " + target)

                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.clearColorFilter()
                    iv?.invalidate()
                    return true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val iv : ImageView? = v?.findViewById(R.id.image_view)
                    iv?.clearColorFilter()
                    iv?.invalidate()
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

        gridLayout = findViewById(R.id.grid_layout)

        ImageGalleryClient(this).getImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    gridLayout.addView(getGridItem(images.items.first().name,
                            resources.displayMetrics.widthPixels / 3 * 2 - 10,
                            null,
                            2,
                            2))

                    val view : View = gridLayout.getChildAt(0)

                    val viewTreeObserver = view.getViewTreeObserver()
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                view.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                                images.items.drop(1).forEach { image ->
                                    gridLayout.addView(getGridItem(image.name,
                                            resources.displayMetrics.widthPixels / 3 - 8,
                                            view.getHeight() / 2 - 2,
                                            1,
                                            1))
                                }
                            }
                        })
                    }
                })
    }

    fun getGridItem(name: String, width: Int, height: Int?, rowspan: Int, colspan: Int) : View  {
        val inflater : LayoutInflater = LayoutInflater.from(this)
        val itemView : View = inflater.inflate(R.layout.grid_item, gridLayout, false)

        val imageView : ImageView = itemView.findViewById(R.id.image_view)
        imageView.setImageResource(resources.getIdentifier(name, "drawable", packageName))

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
//                val ft = fragmentManager.beginTransaction()
//                val prev = fragmentManager.findFragmentByTag("dialog")
//                if (prev != null) {
//                    ft.remove(prev)
//                }
//                ft.addToBackStack(null)

                val newFragment : ImageDialogFragment = ImageDialogFragment.newInstance(
                        resources.getIdentifier(name, "drawable", packageName))

                val fm = fragmentManager
//                val dialogFragment = ImageDialogFragment()
                newFragment.show(fm, "Dialog Fragment")
            }
        })
        itemView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val dragData : ClipData = ClipData.newPlainText("index", gridLayout.indexOfChild(v).toString())
                val shadowBuilder : View.DragShadowBuilder = View.DragShadowBuilder(v);
                v?.startDrag(dragData, shadowBuilder, v, 0);
                v?.setVisibility(View.INVISIBLE);
                return true;
            }
        })
        itemView.setOnDragListener(ViewOnDragListener())

        return itemView
    }
}
