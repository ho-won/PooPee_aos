package kr.ho1.poopee.database

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.util.LocationManager
import kr.ho1.poopee.common.util.MyUtil
import kr.ho1.poopee.home.model.Toilet
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ToiletSQLiteManager : SQLiteOpenHelper(ObserverManager.context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        @SuppressLint("SdCardPath")
        private const val DATABASE_PATH = "/data/data/kr.ho1.poopee/databases/" // DB 경로
        private const val DATABASE_NAME = "PooPee" // DB 이름
        private const val DATABASE_VERSION = 1 // DB 버전

        @Volatile
        private var instance: ToiletSQLiteManager? = null

        @JvmStatic
        fun getInstance(): ToiletSQLiteManager =
                instance ?: synchronized(this) {
                    instance ?: ToiletSQLiteManager().also {
                        instance = it
                    }
                }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    override fun onCreate(p0: SQLiteDatabase?) {

    }

    /**
     * DB 파일 import
     */
    @Throws(IOException::class)
    fun importDatabase(dbPath: String): Boolean {
        writableDatabase.disableWriteAheadLogging()

        val newDb = File(dbPath)
        val oldDb = File(DATABASE_PATH + DATABASE_NAME)
        if (newDb.exists()) {
            MyUtil.copyFile(FileInputStream(newDb), FileOutputStream(oldDb))
            writableDatabase.close()
            return true
        }
        close()
        return false
    }

    /**
     * Toilet 가져오기
     */
    fun getToilet(id: String): Toilet? {
        try {
            val db = readableDatabase
            val selectQuery = "SELECT * FROM toilets WHERE id=$id"
            val cursor = db.rawQuery(selectQuery, null)

            val count = cursor.count
            val toilet = Toilet()
            if (cursor.moveToFirst()) {
                do {
                    toilet.toilet_id = cursor.getInt(0)
                    toilet.type = cursor.getString(1)
                    toilet.name = cursor.getString(2)
                    toilet.address_new = cursor.getString(3)
                    toilet.address_old = cursor.getString(4)
                    toilet.latitude = cursor.getDouble(19)
                    toilet.longitude = cursor.getDouble(20)
                } while (cursor.moveToNext())
            }

            cursor.close()
            return if (count > 0) {
                toilet
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Toilet 목록 가져오기
     */
    fun getToiletList(latitude: Double, longitude: Double): ArrayList<Toilet> {
        try {
            val startLat = (latitude - LocationManager.DISTANCE).toString()
            val endLat = (latitude + LocationManager.DISTANCE).toString()
            val startLong = (longitude - LocationManager.DISTANCE).toString()
            val endLong = (longitude + LocationManager.DISTANCE).toString()

            val db = readableDatabase
            val selectQuery = "SELECT * FROM toilets WHERE (latitude * 1.0 BETWEEN $startLat AND $endLat) AND (longitude * 1.0 BETWEEN $startLong AND $endLong)"
            val cursor = db.rawQuery(selectQuery, null)

            val toiletList: ArrayList<Toilet> = ArrayList()
            if (cursor.moveToFirst()) {
                do {
                    val toilet = Toilet()
                    toilet.toilet_id = cursor.getInt(0)
                    toilet.type = cursor.getString(1)
                    toilet.name = cursor.getString(2)
                    toilet.address_new = cursor.getString(3)
                    toilet.address_old = cursor.getString(4)
                    toilet.latitude = cursor.getDouble(19)
                    toilet.longitude = cursor.getDouble(20)
                    toiletList.add(toilet)
                } while (cursor.moveToNext())
            }

            cursor.close()
            return toiletList
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }
    }

}
