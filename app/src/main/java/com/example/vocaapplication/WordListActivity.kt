package com.example.vocaapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.ListPreference
import androidx.preference.PreferenceManager
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // 색상 세팅 메뉴
        2131296822 -> {
            /*val builder = AlertDialog.Builder(this)
            builder.setTitle("선택")
            val test_value = R.array.setting_color_values
            test_value
            var itemList = resources.getStringArray(R.array.setting_color_values)

            var checkedItemIndex = 0 // 선택된 항목을 저장하는 변수

            builder.setSingleChoiceItems(itemList, checkedItemIndex) { dialog, which ->
                checkedItemIndex = which
            }

            builder.setPositiveButton("확인") { dialog, which ->
                val prefs2 = PreferenceManager.getDefaultSharedPreferences(this)
                prefs2.edit().run {
                    putString("color", itemList[checkedItemIndex])
                }
                //prefs.setString("color", itemList[checkedItemIndex])
                Log.d("Leraedon", itemList[checkedItemIndex])
                binding.wordsRecycler.adapter = WordAdapter(this, original_datas)
            }
            builder.setNegativeButton("취소") { dialog, which ->

            }

            builder.show()*/
            val intent: Intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)

            true
        }
        else -> {
            Log.d("Leraedon", item.itemId.toString())
            super.onOptionsItemSelected(item)
        }
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
            val prefs2 = PreferenceManager.getDefaultSharedPreferences(context)
            when(prefs2.getString("color", "Teal")) {
                "Teal" -> {
                    binding.WordLayout.background = getDrawable(context, R.drawable.round_recycler)
                    Log.d("Leraedon", "0")
                }
                "Purple" -> {
                    binding.WordLayout.background = getDrawable(context, R.drawable.round_recycler_purple)
                    Log.d("Leraedon", "1")
                }
                "Red" -> {
                    binding.WordLayout.background = getDrawable(context, R.drawable.round_recycler_red)
                    Log.d("Leraedon", "2")
                }
            }

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
