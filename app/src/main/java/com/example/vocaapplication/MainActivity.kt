package com.example.vocaapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import com.example.vocaapplication.databinding.ActivityMainBinding
import org.json.JSONArray
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val favorites_daynum: Int = 61

        // word list button event
        binding.wordlistBtn.setOnClickListener {
            val intent: Intent = Intent(this, DaySelectActivity::class.java)
            intent.putExtra("menu_type", "list")
            startActivity(intent)
        }

        binding.wordtestBtn.setOnClickListener {
            val intent: Intent = Intent(this, DaySelectActivity::class.java)
            intent.putExtra("menu_type", "word_test")
            startActivity(intent)
        }

        binding.synonymtestBtn.setOnClickListener {
            val intent: Intent = Intent(this, DaySelectActivity::class.java)
            intent.putExtra("menu_type", "synonym_test")
            startActivity(intent)
        }

        binding.antonymtestBtn.setOnClickListener {
            val intent: Intent = Intent(this, DaySelectActivity::class.java)
            intent.putExtra("menu_type", "antonym_test")
            startActivity(intent)
        }

        binding.favoritesBtn.setOnClickListener {
            val intent: Intent = Intent(this, WordListActivity::class.java)
            intent.putExtra("daynum", favorites_daynum)
            startActivity(intent)
        }

        //today's word
        val jsonString = assets.open("words.json").reader().readText()
        val jsonArray = JSONArray(jsonString)
        val randomIndex: Int = Random().nextInt(3999)
        val jsonObject = jsonArray.getJSONObject(randomIndex)
        val todayWord = jsonObject.getString("word")
        val todayDef = jsonObject.getJSONArray("definitions")

        binding.todayWord.text = todayWord
        binding.todayDesc.text = todayDef.toArrayList().toString().replace("[","").replace("]","")
    }
}

fun setSingleScroll(textView: TextView) {
    textView.apply {
        setSingleLine()
        marqueeRepeatLimit = -1
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isSelected = true
    }
}