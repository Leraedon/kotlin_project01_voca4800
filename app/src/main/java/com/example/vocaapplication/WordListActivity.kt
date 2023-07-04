package com.example.vocaapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.vocaapplication.databinding.ActivityWordListBinding
import com.example.vocaapplication.databinding.WordRecyclerViewBinding
import org.json.JSONArray
import java.security.AccessController.getContext
import java.util.*
import kotlin.collections.ArrayList

class WordListActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    companion object {
        lateinit var prefs: PreferenceUtil
        lateinit var binding: ActivityWordListBinding
    }

    private val original_datas = ArrayList<WordData>()
    private val filtered_datas = ArrayList<WordData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate(savedInstanceState)
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val daynum = intent.getIntExtra("daynum", 1)
        var maxWordIndex = 4800
        var minWordIndex = 0
        val favoriteMenu = 61

        if(daynum == favoriteMenu) supportActionBar?.title = "My Favorites"
        else if (daynum in 1..60) {
            supportActionBar?.title = "Day $daynum"
            maxWordIndex = daynum*80
            minWordIndex = (daynum-1)*80
        }
        else supportActionBar?.title = "All Words List"

        Log.d("leraedon", daynum.toString())

        if(daynum in 1..60) {
            val jsonString = assets.open("words.json").reader().readText()
            val jsonArray = JSONArray(jsonString)
            for(index in minWordIndex until maxWordIndex) {
                val jsonObject = jsonArray.getJSONObject(index)
                val num = jsonObject.getInt("num")
                val word = jsonObject.getString("word")
                val definitions = jsonObject.getJSONArray("definitions")
                val synonyms = jsonObject.getJSONArray("synonyms")
                val antonyms = jsonObject.getJSONArray("antonyms")
                //Log.d("jsonObject", jsonObject.toString())
                //Log.d("jsonIndex", num.toString() + word + definitions + synonyms + antonyms)
                val newWord: WordData = WordData(num, word, definitions.toArrayList(), synonyms.toArrayList(), antonyms.toArrayList())
                original_datas.add(newWord)
            }
        } else if (daynum == favoriteMenu)  { // 즐겨찾기 메뉴
            val jsonString = assets.open("words.json").reader().readText()
            val jsonArray = JSONArray(jsonString)
            var index = 1
            prefs = PreferenceUtil(this)

            while(index < jsonArray.length()) {
                if(prefs.getBoolean(index.toString(), false)) {
                    val jsonObject = jsonArray.getJSONObject(index)
                    val num = jsonObject.getInt("num")
                    val word = jsonObject.getString("word")
                    val definitions = jsonObject.getJSONArray("definitions")
                    val synonyms = jsonObject.getJSONArray("synonyms")
                    val antonyms = jsonObject.getJSONArray("antonyms")
                    //Log.d("jsonObject", jsonObject.toString())
                    //Log.d("jsonIndex", num.toString() + word + definitions + synonyms + antonyms)
                    val newWord: WordData = WordData(
                        num,
                        word,
                        definitions.toArrayList(),
                        synonyms.toArrayList(),
                        antonyms.toArrayList()
                    )
                    original_datas.add(newWord)
                }
                index++
            }
        } else { // all day 선택 시
            val jsonString = assets.open("words.json").reader().readText()
            val jsonArray = JSONArray(jsonString)
            var index = 0

            while(index < jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(index)
                val num = jsonObject.getInt("num")
                val word = jsonObject.getString("word")
                val definitions = jsonObject.getJSONArray("definitions")
                val synonyms = jsonObject.getJSONArray("synonyms")
                val antonyms = jsonObject.getJSONArray("antonyms")
                //Log.d("jsonObject", jsonObject.toString())
                //Log.d("jsonIndex", num.toString() + word + definitions + synonyms + antonyms)
                val newWord: WordData = WordData(num, word, definitions.toArrayList(), synonyms.toArrayList(), antonyms.toArrayList())
                original_datas.add(newWord)
                index++
            }
        }

        binding.wordsRecycler.layoutManager = LinearLayoutManager(this)
        binding.wordsRecycler.adapter = WordAdapter(this, original_datas)
        binding.wordsRecycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu?.findItem(R.id.menu_search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        return true
    }

    override fun onQueryTextChange(entry: String?): Boolean {
        filtered_datas.clear()
        (0 until original_datas.size).filter {
            original_datas[it].word.lowercase().contains(entry!!.lowercase()) ||
                    original_datas[it].num.toString().contains(entry!!.lowercase()) ||
                    original_datas[it].definitions.joinToString().lowercase().contains(entry!!.lowercase()) ||
                    original_datas[it].synonyms.joinToString().lowercase().contains(entry!!.lowercase()) ||
                    original_datas[it].antonyms.joinToString().lowercase().contains(entry!!.lowercase())
        }.mapTo(filtered_datas) { original_datas[it] }

        binding.wordsRecycler.adapter = WordAdapter(this, filtered_datas)

        return true
    }

    class MyViewHolder(val binding: WordRecyclerViewBinding):RecyclerView.ViewHolder(binding.root)

    class WordAdapter(val context: Context, val dataList: ArrayList<WordData>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            MyViewHolder(WordRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val binding = (holder as MyViewHolder).binding
            prefs = PreferenceUtil(context)

            binding.wordNum.text = dataList[position].num.toString()
            binding.wordTitle.text = dataList[position].word
            binding.wordDef.text = dataList[position].definitions.joinToString()
            binding.wordSyn.text = dataList[position].synonyms.joinToString()
            binding.wordAnt.text = dataList[position].antonyms.joinToString()
            setSingleScroll(binding.wordTitle)
            setSingleScroll(binding.wordDef)
            setSingleScroll(binding.wordSyn)
            setSingleScroll(binding.wordAnt)
            if(binding.wordTitle.length() >= 14) {
                binding.wordTitle.textSize = 11F
            }

            if(prefs.getBoolean((dataList[position].num-1).toString(), false)) {
                binding.featuredIcon.background = getDrawable(context, R.drawable.baseline_star_24)
                } else {
                binding.featuredIcon.background = getDrawable(context, R.drawable.baseline_star_border_24)
            }

            binding.featuredIcon.setOnClickListener {
                if(!prefs.getBoolean((dataList[position].num-1).toString(), false)) {
                    binding.featuredIcon.background = getDrawable(context, R.drawable.baseline_star_24)
                    prefs.setBoolean((dataList[position].num-1).toString(), true)
                    Toast.makeText(context, "'${dataList[position].word}' 단어 즐겨찾기 등록", Toast.LENGTH_SHORT).show()
                    Log.d("Leraedon", (dataList[position].num-1).toString())
                }
                else {
                    binding.featuredIcon.background = getDrawable(context, R.drawable.baseline_star_border_24)
                    prefs.setBoolean((dataList[position].num-1).toString(), false)
                    Toast.makeText(context, "'${dataList[position].word}' 단어 즐겨찾기 해제", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount(): Int = dataList.size
    }
}



fun JSONArray.toArrayList(): ArrayList<String> {
    val list = arrayListOf<String>()
    for(i in 0 until this.length()) {
        list.add(this.getString(i))
    }
    return list
}