package com.example.notepadmvcpattern.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.notepadmvcpattern.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TextColorDialog : BottomSheetDialogFragment() {

    private var rbColors: RadioGroup? = null
    private var rbRed: RadioButton? = null
    private var rbGreen: RadioButton? = null
    private var rbBlack: RadioButton? = null

    private val color by lazy { arguments?.getSerializable(COLOR) ?: ColorsType.BLACK }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.text_color_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rbColors = view.findViewById(R.id.rbColors)
        rbRed = view.findViewById(R.id.rbRed)
        rbGreen = view.findViewById(R.id.rbGreen)
        rbBlack = view.findViewById(R.id.rbBlack)

        when (color) {
            ColorsType.RED -> rbRed?.isChecked = true
            ColorsType.GREEN -> rbGreen?.isChecked = true
            ColorsType.BLACK -> rbBlack?.isChecked = true
        }

        rbColors?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbRed -> {
                    (activity as? MainActivity)?.setTextColor(ColorsType.RED)
                }
                R.id.rbGreen -> {
                    (activity as? MainActivity)?.setTextColor(ColorsType.GREEN)
                }
                R.id.rbBlack -> {
                    (activity as? MainActivity)?.setTextColor(ColorsType.BLACK)
                }
            }
            dismiss()
        }
    }

    companion object {
        private const val COLOR = "color"

        fun newInstance(color: ColorsType): TextColorDialog {
            return TextColorDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(COLOR, color)
                }
            }
        }
    }
}


enum class ColorsType(val value: Int) {
    RED(R.color.red),
    GREEN(R.color.green),
    BLACK(R.color.black)
}