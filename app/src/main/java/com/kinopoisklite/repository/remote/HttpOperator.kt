package com.kinopoisklite.repository.remote

import com.kinopoisklite.exception.ServerSideException
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class HttpOperator {
    private var sessionId: String? = null

    fun logout(address: String): Any {
        val result = post(address, null, "application/x-www-form-urlencoded")
        if (result != false)
            sessionId = null
        return result
    }

    fun get(address: String): JSONArray {
        var conn: HttpURLConnection? = null
        try {
            conn = URL(address).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.doInput = true
            if (sessionId != null)
                conn.setRequestProperty(
                    "Cookie",
                    sessionId
                )
            conn.connect()
            val arrayStringified: String =
                conn.inputStream.bufferedReader().use(BufferedReader::readText)
            if (arrayStringified.contains("\"error\": "))
                throw ServerSideException(JSONObject(arrayStringified).getString("error"))
            if (sessionId == null)
                if (conn.getHeaderField("Set-Cookie") != null)
                    sessionId = conn.getHeaderField("Set-Cookie").split(";")[0]
            var arr = JSONArray(arrayStringified)
            return arr
        } catch (mURL: MalformedURLException) {
            mURL.printStackTrace()
            throw ServerSideException("Неверный URL: $address")
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            throw ServerSideException(ioe.message)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ServerSideException(e.message)
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(ServerSideException::class)
    fun post(address: String, body: String?, contentType: String): Any {
        var conn: HttpURLConnection? = null
        try {
            conn = URL(address).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", contentType)
            conn.setRequestProperty("Accept", "application/json")
            if (sessionId != null)
                conn.setRequestProperty(
                    "Cookie",
                    sessionId
                )
            conn.doInput = true
            if (body != null) {
                conn.doOutput = true
                conn.getOutputStream().use({ os ->
                    val input: ByteArray? = body.toByteArray()
                    os.write(input, 0, if (input?.size != null) input.size else 0)
                })
            }
            val resultStringified: String =
                conn.inputStream.bufferedReader().use(BufferedReader::readText)
            val result = JSONObject(resultStringified)
            if (result.has("error"))
                throw ServerSideException(result.getString("error"))
            else
                return result.get("result")
        } catch (e: Exception) {
            e.printStackTrace()
            throw ServerSideException(e.message)
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(ServerSideException::class)
    fun delete(address: String): Boolean {
        var conn: HttpURLConnection? = null
        try {
            conn = URL(address).openConnection() as HttpURLConnection
            conn.requestMethod = "DELETE"
            if (sessionId != null)
                conn.setRequestProperty(
                    "Cookie",
                    sessionId
                )
            conn.doInput = true
            val resultStringified: String =
                conn.inputStream.bufferedReader().use(BufferedReader::readText)
            val result = JSONObject(resultStringified)
            if (result.has("error"))
                throw ServerSideException(result.getString("error"))
            else
                return result.getBoolean("result")
        } catch (e: Exception) {
            e.printStackTrace()
            throw ServerSideException(e.message)
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}