package com.example.simulasilks1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simulasilks1.databinding.ActivityProfileBinding

class ActivityProfile : AppCompatActivity() {
    lateinit var bind : ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(bind.root)
    }
}