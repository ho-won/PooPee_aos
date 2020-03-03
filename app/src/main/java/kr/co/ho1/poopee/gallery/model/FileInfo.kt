package kr.co.ho1.poopee.gallery.model

import java.io.Serializable

class FileInfo(private var _id: String, private var _path: String, private var _size: String) : Serializable {

    var id: String
        get() {
            return _id
        }
        set(value) {
            _id = value
        }

    var path: String
        get() {
            return _path
        }
        set(value) {
            _path = value
        }

    var size: String
        get() {
            return _size
        }
        set(value) {
            _size = value
        }

}