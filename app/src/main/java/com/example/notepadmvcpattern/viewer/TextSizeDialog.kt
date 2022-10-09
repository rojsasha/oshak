package com.example.notepadmvcpattern.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import com.example.notepadmvcpattern.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TextSizeDialog : BottomSheetDialogFragment() {

    private var textSize: Int = -1
    private var imgPlus: ImageView? = null
    private var imgMinus: ImageView? = null
    private var etSize: EditText? = null
    private var btnOk: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.text_size_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textSize = arguments?.getInt(TEXT_SIZE) ?: 0
        etSize = view.findViewById(R.id.etSize)
        imgPlus = view.findViewById(R.id.plus)
        imgMinus = view.findViewById(R.id.minus)
        btnOk = view.findViewById(R.id.ok)

        imgPlus?.setOnClickListener {
            changeTextSize(1)
        }

        imgMinus?.setOnClickListener {
            changeTextSize(-1)
        }

        btnOk?.setOnClickListener {
            (activity as? MainActivity)?.setTextSize(textSize)
            dismiss()
        }

        etSize?.doAfterTextChanged { text ->
            if (text.toString().isNotEmpty()) {
                textSize = text.toString().toInt()
            }
        }

        etSize?.setText(textSize.toString())
    }

    private fun changeTextSize(value: Int) {
        textSize += value
        etSize?.setText(textSize.toString())
    }

    companion object {
        private const val TEXT_SIZE = "text_size"

        fun newInstance(size: Int): TextSizeDialog {
            return TextSizeDialog().apply {
                arguments = Bundle().apply {
                    putInt(TEXT_SIZE, size)
                }
            }
        }
    }
}
