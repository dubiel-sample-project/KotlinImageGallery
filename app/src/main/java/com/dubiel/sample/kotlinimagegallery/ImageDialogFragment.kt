package com.dubiel.sample.kotlinimagegallery

import android.app.Dialog
import android.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView



class ImageDialogFragment : DialogFragment() {
    internal var mIdentifier: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIdentifier = getArguments().getInt("identifier")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                     savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_dialog, container, false)
        val image : ImageView = view.findViewById(R.id.image)

        image.setImageResource(mIdentifier)

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
        internal fun newInstance(identifier : Int): ImageDialogFragment {
            val f = ImageDialogFragment()

            val args = Bundle()
            args.putInt("identifier", identifier)
            f.setArguments(args)

            return f
        }
    }
}