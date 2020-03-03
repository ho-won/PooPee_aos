package kr.co.ho1.poopee.common.util

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri

import java.io.File

class SingleMediaScanner(context: Context, private val file: File) : MediaScannerConnection.MediaScannerConnectionClient {
    private val mediaScannerConnection: MediaScannerConnection = MediaScannerConnection(context, this)

    fun connect() {
        mediaScannerConnection.connect()
    }

    override fun onMediaScannerConnected() {
        mediaScannerConnection.scanFile(file.absolutePath, null)
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        mediaScannerConnection.disconnect()
    }

}
