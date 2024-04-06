package com.example.simulasilks1

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.simulasilks1.databinding.ActivityDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ActivityDetail : AppCompatActivity() {
    lateinit var  bind : ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.detailToolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }

        val id = intent.getStringExtra("petID")

        GlobalScope.launch(Dispatchers.IO) {
            val conn = URL("http://192.168.1.19:5000/petition/${id.toString()}").openStream().bufferedReader().readText()
            val jsons = JSONObject(conn)
            Log.d("oke", jsons.toString())

            runOnUiThread {
                bind.detailTitle.text = jsons.getString("title")
                bind.detailCreator.text = jsons.getString("creatorName")
                bind.detailDesc.text = jsons.getString("description")

                bind.signedBy.text = "Signed by ${jsons.getString("totalSigners")} people"
                bind.target.text = "${jsons.getString("target")} more to go"
            }
        }

        var creatorid = this.getSharedPreferences("random", Context.MODE_PRIVATE).getString("user", "{}")
        var arrays = JSONObject(creatorid);

        GlobalScope.launch(Dispatchers.IO) {
            val conn = URL("http://192.168.1.19:5000/petition/${id.toString()}/is-signed?signerID=${arrays.getString("userID")}").openStream().bufferedReader().readText()
            Log.d("oke", conn)

            if (JSONObject(conn).getBoolean("isSigned") == false) {
                bind.btnSign.setText("Sign")
            }
            else {
                bind.btnSign.setText("Signed")
            }
        }

        bind.btnSign.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val conn = URL("http://192.168.1.19:5000/petition/${id}/sign").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")

                val jsons = JSONObject().apply {
                    put("signerID", arrays.getString("userID"))
                }

                conn.outputStream.write(jsons.toString().toByteArray())

                if (conn.responseCode in 200..299) {
                    runOnUiThread {
                        bind.btnSign.setText("Signed")
                    }
                }
                else {
                    runOnUiThread {
                        Toast.makeText(this@ActivityDetail, "${conn.errorStream.bufferedReader().readText()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}