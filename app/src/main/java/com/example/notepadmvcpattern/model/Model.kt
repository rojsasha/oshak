package com.example.notepadmvcpattern.model

import com.example.notepadmvcpattern.viewer.MainActivity

class Model(private var viewer: MainActivity) {

    init {
        this.viewer = viewer
    }
}