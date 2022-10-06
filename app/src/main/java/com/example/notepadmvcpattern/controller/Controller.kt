package com.example.notepadmvcpattern.controller

import com.example.notepadmvcpattern.viewer.MainActivity
import com.example.notepadmvcpattern.model.Model


class Controller(){

    private lateinit var model: Model

    constructor(viewer: MainActivity) : this() {
        model = Model(viewer = viewer)
    }


}
