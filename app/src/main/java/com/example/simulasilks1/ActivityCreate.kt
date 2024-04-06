package com.example.simulasilks1

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.simulasilks1.databinding.ActivityCreateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ActivityCreate : AppCompatActivity() {
    lateinit var bind : ActivityCreateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.createToolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }


        var creatorid =this.getSharedPreferences("random", Context.MODE_PRIVATE).getString("user", "{}")
        var arrays =JSONObject(creatorid);

        bind.btnCreate.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val conn = URL("http://192.168.1.19:5000/petition").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")

                val jsons = JSONObject().apply {
                    put("title", bind.createTitle.text)
                    put("description", bind.createDescription.text)
                    put("target", bind.createTarget.text)
                    put("creatorID", arrays.getString("userID"))
                }

                conn.outputStream.write(jsons.toString().toByteArray())

                if (conn.responseCode in 200..299) {
                    setResult(Activity.RESULT_OK, Intent(this@ActivityCreate, ActivityUtama::class.java))
                    finish()
//                    startActivity(Intent(this@ActivityCreate, ActivityUtama::class.java))
//                    finish()
                }
                else {
                    runOnUiThread {
                        Toast.makeText(this@ActivityCreate, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}