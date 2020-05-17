package com.ivancarras.multilinecollapsingtoolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

private const val LAYOUT_RES_ARG = "layout_res_arg"

class SimpleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(requireArguments().getInt(LAYOUT_RES_ARG), container, false)
    }

    companion object {
        fun createInstance(@LayoutRes layoutRes: Int) =
            SimpleFragment()
                .apply {
                    arguments = Bundle().apply {
                        putInt(LAYOUT_RES_ARG, layoutRes)
                    }
                }
    }
}