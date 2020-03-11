package kr.co.ho1.poopee.common.http

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.database.ToiletSQLiteManager
import org.json.JSONException
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class DBVersionTask(progress: ProgressBar, private var onSuccess: (() -> Unit), private var onFailed: (() -> Unit)) {
    private val progressBar: ProgressBar? // 프로그레스바
    private var dbVer = 0

    init {
        this.progressBar = progress
        start()
    }

    /**
     * [GET] toilet db 버전체크
     */
    private fun start() {
        val params = RetrofitParams()

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).dbCheck(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        try {
                            if (it.getInt("rst_code") == 0) {
                                dbVer = it.getInt("db_ver")

                                if (SharedManager.getDbVer() == dbVer) {
                                    onSuccess()
                                } else {
                                    val fileName = it.getString("file_name")
                                    val url = RetrofitService.BASE_APP + "sql/" + fileName
                                    DBDownloadTask().execute(url, fileName)
                                }
                            } else {
                                onFailed()
                            }
                        } catch (e: Exception) {
                            onFailed()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                onFailed = {

                }
        )
    }

    @SuppressLint("StaticFieldLeak")
    inner class DBDownloadTask internal constructor() : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (progressBar != null) {
                progressBar.visibility = View.VISIBLE
            }
        }

        override fun doInBackground(vararg params: String): String? {
            // 진행상황률을 표현한 원형 프로그레스바 세팅
            var count: Int
            var filePath: String? = null

            try {
                val url = URL(params[0])
                val connection = url.openConnection()
                connection.connect()

                val lengthOfFile = connection.contentLength

                val input = BufferedInputStream(url.openStream())
                filePath = ObserverManager.context!!.getExternalFilesDir(null)!!.absolutePath  + File.separator + params[1]
                val output = FileOutputStream(filePath)

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

        override fun onPostExecute(file_path: String) {
            try {
                if (ToiletSQLiteManager.getInstance().importDatabase(file_path)) {
                    val oldDbVersion = SharedManager.getDbVer()
                    val newDbVersion = dbVer
                    for (i in newDbVersion downTo oldDbVersion) {
                        val file = File(ObserverManager.getPath() + "toilet_v" + i + ".sqlite")
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    SharedManager.setDbVer(dbVer)
                    onSuccess()
                } else {
                    onFailed()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailed()
            }
        }
    }

}