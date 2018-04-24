package com.dubiel.sample.kotlinimagegallery

import android.app.DialogFragment
import android.widget.TextView
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView


class ImageDialogFragment : DialogFragment() {
//    internal var mNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
//        mNum = getArguments().getInt("num")
//
//        // Pick a style based on the num.
//        var style = DialogFragment.STYLE_NORMAL
//        var theme = 0
//        when ((mNum - 1) % 6) {
//            1 -> style = DialogFragment.STYLE_NO_TITLE
//            2 -> style = DialogFragment.STYLE_NO_FRAME
//            3 -> style = DialogFragment.STYLE_NO_INPUT
//            4 -> style = DialogFragment.STYLE_NORMAL
//            5 -> style = DialogFragment.STYLE_NORMAL
//            6 -> style = DialogFragment.STYLE_NO_TITLE
//            7 -> style = DialogFragment.STYLE_NO_FRAME
//            8 -> style = DialogFragment.STYLE_NORMAL
//        }
//        when ((mNum - 1) % 6) {
//            4 -> theme = android.R.style.Theme_Holo
//            5 -> theme = android.R.style.Theme_Holo_Light_Dialog
//            6 -> theme = android.R.style.Theme_Holo_Light
//            7 -> theme = android.R.style.Theme_Holo_Light_Panel
//            8 -> theme = android.R.style.Theme_Holo_Light
//        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View {
        val view = inflater.inflate(R.layout.fragment_dialog, container, false)
        val image : ImageView = view.findViewById(R.id.image)

//        // Watch for button clicks.
//        val button = v.findViewById(R.id.show) as Button
//        button.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View) {
//                // When button is clicked, call up to owning activity.
//                (getActivity() as FragmentDialog).showDialog()
//            }
//        })

        return view
    }

    companion object {
        internal fun newInstance(): ImageDialogFragment {
            val f = ImageDialogFragment()

//            // Supply num input as an argument.
            val args = Bundle()
//            args.putInt("num", num)
            f.setArguments(args)

            return f
        }
    }
}