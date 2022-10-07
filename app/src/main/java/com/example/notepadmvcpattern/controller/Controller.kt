package com.example.notepadmvcpattern.controller

import com.example.notepadmvcpattern.model.Model
import com.example.notepadmvcpattern.viewer.MainActivity


class Controller(){

    private lateinit var model: Model

    constructor(viewer: MainActivity) : this() {
        model = Model(viewer = viewer)
    }
}
