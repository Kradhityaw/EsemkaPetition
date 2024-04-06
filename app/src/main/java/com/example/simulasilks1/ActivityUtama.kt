package com.example.simulasilks1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simulasilks1.databinding.ActivityUtamaBinding
import com.example.simulasilks1.databinding.CardPetititonBinding
import com.example.simulasilks1.databinding.SidebarLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class ActivityUtama : AppCompatActivity() {

    lateinit var bind : ActivityUtamaBinding
    lateinit var toggle: ActionBarDrawerToggle

    var launcher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityUtamaBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.menuNv.setNavigationItemSelectedListener {
            if (it.itemId == R.id.new_petition) {
                launcher2.launch(Intent(this@ActivityUtama, ActivityCreate::class.java))
            }
            if (it.itemId == R.id.logout) {
                startActivity(Intent(this@ActivityUtama, MainActivity::class.java))
                finish()
            }
            return@setNavigationItemSelectedListener true
        }

        setSupportActionBar(bind.menuToolbar)
        toggle = ActionBarDrawerToggle(this@ActivityUtama, bind.menuDrawer, R.string.open_text, R.string.close_text)

        bind.menuDrawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        GlobalScope.launch(Dispatchers.IO) {
            val conn = URL("http://192.168.1.19:5000/petition").openStream().bufferedReader().readText()
            val jsons = JSONArray(conn)

            runOnUiThread {
                val adapter = object : RecyclerView.Adapter<PetitionViewHolder>() {
                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): PetitionViewHolder {
                        val binding = CardPetititonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                        return PetitionViewHolder(binding)
                    }

                    override fun getItemCount(): Int {
                        return jsons.length()
                    }

                    override fun onBindViewHolder(holder: PetitionViewHolder, position: Int) {
                        val petitions =jsons.getJSONObject(position)
                        holder.binding.petitionName.text =petitions.getString("title")
                        holder.binding.petitionDesc.text = petitions.getString("description")
                        holder.binding.petitionMaker.text = petitions.getString("creatorName")

                        holder.itemView.setOnClickListener {
                            launcher2.launch(Intent(this@ActivityUtama, ActivityDetail::class.java).apply {
                                putExtra("petID", petitions.getString("petitionID"))
                            })
                        }
                    }
                }
                bind.petitionRv.adapter = adapter
                bind.petitionRv.layoutManager = LinearLayoutManager(this@ActivityUtama)
            }
        }
    }

    class PetitionViewHolder(val binding: CardPetititonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        toggle.onOptionsItemSelected(item)
        if (item.itemId == R.id.new_petition) {
        }
        return true
    }

}