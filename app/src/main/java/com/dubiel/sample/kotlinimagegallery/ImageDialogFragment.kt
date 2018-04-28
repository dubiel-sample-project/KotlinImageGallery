package com.dubiel.sample.kotlinimagegallery

import android.app.Dialog
import android.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import com.squareup.picasso.Picasso


class ImageDialogFragment : DialogFragment() {
    internal var mUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUri = getArguments().getString("uri")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                     savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_dialog, container, false)
        val image : ImageView = view.findViewById(R.id.image)

        Picasso.with(activity).load(Uri.parse(mUri))
                        .resize(resources.displayMetrics.widthPixels, 0)
                .into(image)

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    companion object {
        internal fun newInstance(uri : String): ImageDialogFragment {
            val f = ImageDialogFragment()

            val args = Bundle()
            args.putString("uri", uri)
            f.setArguments(args)

            return f
        }
    }
}