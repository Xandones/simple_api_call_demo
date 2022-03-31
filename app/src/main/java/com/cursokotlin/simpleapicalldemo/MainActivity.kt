package com.cursokotlin.simpleapicalldemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallAPILoginAsyncTask().execute()
    }

    private inner class CallAPILoginAsyncTask() : AsyncTask<Any, Void, String>() {

        private lateinit var customProgressDialog : Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()
        }

        override fun doInBackground(vararg p0: Any?): String {
            var result : String
            var connection : HttpsURLConnection? = null

            try {
                val url = URL("https://run.mocky.io/v3/6997a99e-5bd6-478c-9803-0e10f56dadf9")
                connection = url.openConnection() as HttpsURLConnection
                connection.doInput = true
                connection.doOutput = true

                val httpResult : Int = connection.responseCode

                if (httpResult == HttpsURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line : String?
                    try {
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n")
                        }
                    }
                    catch (e : IOException) {
                        e.printStackTrace()
                    }
                    finally {
                        try {
                            inputStream.close()
                        }
                        catch (e : IOException) {
                            e.printStackTrace()
                        }
                    }

                    result = stringBuilder.toString()
                }

                else {
                    result = connection.responseMessage
                }
            }
            catch (e : SocketTimeoutException) {
                result = "Connection Timeout"
            }
            catch (e : Exception) {
                result = "Error : " + e.message
            }
            finally {
                connection?.disconnect()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()

            if (result != null) {
                Log.i("JSON RESPONSE RESULT", result)
            }
        }

        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }
    }
}