package kr.co.ho1.poopee.common.http

import android.view.View
import android.widget.ProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.database.ToiletSQLiteManager
import org.json.JSONException
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class DBVersionTask(
    private val progressBar: ProgressBar?,
    private val onSuccess: () -> Unit,
    private val onFailed: () -> Unit
) {
    private var dbVer = 0

    init {
        CoroutineScope(Dispatchers.Main).launch {
            start()
        }
    }

    /**
     * [GET] toilet db 버전체크
     */
    private suspend fun start() {
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
                                CoroutineScope(Dispatchers.Main).launch {
                                    downloadDatabase(url, fileName)
                                }
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

    private suspend fun downloadDatabase(url: String, fileName: String) {
        withContext(Dispatchers.Main) {
            progressBar?.visibility = View.VISIBLE
        }

        try {
            var filePath: String? = null

            withContext(Dispatchers.IO) {
                try {
                    val connection = URL(url).openConnection()
                    connection.connect()

                    val lengthOfFile = connection.contentLength
                    val input = BufferedInputStream(URL(url).openStream())
                    filePath = ObserverManager.context!!.getExternalFilesDir(null)!!.absolutePath +
                            File.separator + fileName
                    val output = FileOutputStream(filePath)
                    val data = ByteArray(1024)
                    var total: Long = 0
                    var count: Int

                    while (input.read(data).also { count = it } >= 0) {
                        total += count.toLong()
                        // Update progress on main thread
                        withContext(Dispatchers.Main) {
                            val progress = (total * 100 / lengthOfFile).toInt()
                            progressBar?.progress = progress
                        }
                        output.write(data, 0, count)
                    }

                    output.flush()
                    output.close()
                    input.close()

                } catch (e: IOException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        onFailed()
                    }
                    return@withContext
                }
            }

            // Process downloaded file
            if (ToiletSQLiteManager.getInstance().importDatabase(filePath!!)) {
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
        } finally {
            withContext(Dispatchers.Main) {
                progressBar?.visibility = View.GONE
            }
        }
    }
}