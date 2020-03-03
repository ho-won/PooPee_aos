package kr.co.ho1.poopee.gallery.model

import java.io.File

class GalleryDir(private var _file: File?, private var _name: String, private var _count: Int) {

    var file: File?
        get() {
            return _file
        }
        set(value) {
            _file = value
        }

    var name: String
        get() {
            return _name
        }
        set(value) {
            _name = value
        }

    var count: Int
        get() {
            return _count
        }
        set(value) {
            _count = value
        }

}