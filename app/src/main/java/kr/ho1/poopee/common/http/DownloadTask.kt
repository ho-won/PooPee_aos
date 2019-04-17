package kr.ho1.poopee.common.http

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.util.SingleMediaScanner
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

@SuppressLint("StaticFieldLeak")
class DownloadTask(private val progressBar: ProgressBar?, private var onSuccess: (() -> Unit)) : AsyncTask<String, String, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
    }

    override fun doInBackground(vararg params: String): String? {
        // 진행상황률을 표현한 원형 프로그레스바 세팅
        var count = 0
        var filePath: String? = null

        try {
            val url = URL(params[0])
            val connection = url.openConnection()
            connection.connect()

            val lengthOfFile = connection.contentLength

            val directory = File(ObserverManager.getPath())
            directory.mkdirs()

            val input = BufferedInputStream(url.openStream())
            val output = FileOutputStream(ObserverManager.getPath() + params[1])
            filePath = ObserverManager.getPath() + params[1]

            val data = ByteArray(1024)
            var total: Long = 0

            while (input.read(data).also { count = it } >= 0) {
                total += count.toLong()
                publishProgress("" + (total * 100 / lengthOfFile).toInt())
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return filePath
    }

    override fun onProgressUpdate(vararg progress: String) {
        if (progressBar != null) {
            progressBar.progress = Integer.parseInt(progress[0])
        }
    }

    override fun onPostExecute(file_path: String?) {
        if (file_path != null) {
            // 파일다운이 완료되면 미디어 스캐너 실행
            val singleMediaScanner = SingleMediaScanner(ObserverManager.context!!, File(file_path))
            singleMediaScanner.connect()
        }
        if (progressBar != null) {
            progressBar.visibility = View.GONE // 프로그래스바 안보이기
        }
        onSuccess()
    }

}