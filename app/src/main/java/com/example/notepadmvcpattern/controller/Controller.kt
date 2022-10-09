package com.example.notepadmvcpattern.controller

import com.example.notepadmvcpattern.viewer.ColorsType
import com.example.notepadmvcpattern.model.Model
import com.example.notepadmvcpattern.viewer.MainActivity


class Controller() {

    private lateinit var model: Model
    private var color: ColorsType = ColorsType.BLACK
    private var size: Int = 20

    constructor(viewer: MainActivity) : this() {
        model = Model(viewer = viewer)
    }

    fun setTextColor(color: ColorsType) {
        this.color = color
    }

    fun setTextSize(size: Int) {
        this.size = size
    }

    fun getTextColor() = color
    fun getTextSize() = size

}
