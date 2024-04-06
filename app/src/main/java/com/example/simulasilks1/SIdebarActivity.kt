package com.example.simulasilks1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.simulasilks1.databinding.ActivityDetailBinding
import com.example.simulasilks1.databinding.SidebarLayoutBinding

class SIdebarActivity : AppCompatActivity() {
    lateinit var binding: SidebarLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SidebarLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}