package com.example.simulasilks1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.simulasilks1.databinding.ActivitySignUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ActivitySignUp : AppCompatActivity() {
    lateinit var bind : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.signinBtn.setOnClickListener {
            startActivity(Intent(this@ActivitySignUp, MainActivity::class.java))
            finish()
        }

        bind.signupBtn.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val conn = URL("http://192.168.1.19:5000/sign-up").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")

                val jsons = JSONObject().apply {
                    put("firstName", bind.signupFirstname.text)
                    put("lastName", bind.signupLastname.text)
                    put("email", bind.singupEmail.text)
                    put("password", bind.signupPassword.text)
                }

                Log.d("data", jsons.toString())

                conn.outputStream.write(jsons.toString().toByteArray())

                if (conn.responseCode in 200..299) {
                    val inputStream = conn.inputStream.bufferedReader().readText()
                    val preferences = getSharedPreferences("random", Context.MODE_PRIVATE).edit()
                    preferences.putString("user", inputStream)
                    preferences.apply()
                    startActivity(Intent(this@ActivitySignUp, ActivityUtama::class.java).apply {
                        putExtra("userID", JSONObject(inputStream).getString("userID"))
                        putExtra("firstName", JSONObject(inputStream).getString("firstName"))
                        putExtra("lastName", JSONObject(inputStream).getString("lastName"))
                        putExtra("email", JSONObject(inputStream).getString("email"))
                    })
                    finish()
                }
                else {
                    runOnUiThread {
                        Toast.makeText(this@ActivitySignUp, "${conn.errorStream.bufferedReader().readText()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}