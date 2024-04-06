package com.example.simulasilks1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.simulasilks1.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var bind : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.loginEmail.setText("aku@gmail.com")
        bind.loginPassword.setText("12345678")

        bind.btnSignin.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val conn = URL("http://192.168.1.19:5000/sign-in").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")

                val jsons = JSONObject().apply {
                    put("email", bind.loginEmail.text)
                    put("password", bind.loginPassword.text)
                }

                conn.outputStream.write(jsons.toString().toByteArray())

                if (conn.responseCode in 200..299) {
                    val inputStream = conn.inputStream.bufferedReader().readText()
                    val preferences = getSharedPreferences("random", Context.MODE_PRIVATE).edit()
                    preferences.putString("user", inputStream)
                    preferences.apply()
                    startActivity(Intent(this@MainActivity, ActivityUtama::class.java).apply {
                        putExtra("userID", JSONObject(inputStream).getString("userID"))
                        putExtra("firstName", JSONObject(inputStream).getString("firstName"))
                        putExtra("lastName", JSONObject(inputStream).getString("lastName"))
                        putExtra("email", JSONObject(inputStream).getString("email"))
                    })

                    finish()
                }
                else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "${conn.errorStream.bufferedReader().readText()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        bind.btnSingup.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActivitySignUp::class.java))
            finish()
        }
    }
}