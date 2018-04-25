package com.dubiel.sample.kotlinimagegallery

import android.content.ClipData
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dubiel.sample.kotlinimagegallery.retrofit.ImageGalleryClient
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import android.view.LayoutInflater
import android.view.View
import android.support.v7.widget.GridLayout
import android.widget.ImageView
import android.view.Gravity
import android.widget.ScrollView
import android.view.ViewTreeObserver


data class GalleryImage(var name : String)
data class GalleryImages(var items: Array<GalleryImage>)

//class LongPressListener : View.OnLongClickListener {
//    override public onLongClick(val view : View) {
//        val data : ClipData = ClipData.newPlainText("", "");
//        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//        view.startDrag(data, shadowBuilder, view, 0);
//        view.setVisibility(View.INVISIBLE);
//        return true;
//    }

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
//                newFragment.show(ft, "dialog")

                val fm = fragmentManager
//                val dialogFragment = ImageDialogFragment()
                newFragment.show(fm, "Dialog Fragment")
            }
        })

        return itemView
    }
}
