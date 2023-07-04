package com.example.vocaapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vocaapplication.databinding.FragmentTestResultBinding
import com.example.vocaapplication.databinding.ResultRecyclerViewBinding
import com.example.vocaapplication.databinding.WordRecyclerViewBinding
import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONArray

class TestResultFragment : Fragment() {
    lateinit var binding: FragmentTestResultBinding
    private var daynum: Int? = null
    private var quantity: Int? = null
    private var test_type: String? = null
    private var correct_answers_count: Int? = null
    private var previous_answers: ArrayList<Int> = arrayListOf()
    private var previous_answords: ArrayList<String> = arrayListOf()
    private var selected_answers: ArrayList<Int> = arrayListOf()
    private var result_datas = ArrayList<ResultData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTestResultBinding.inflate(inflater, container, false)

        arguments?.let {
            daynum = it.getInt("daynum")
            quantity = it.getInt("quantity")
            test_type = it.getString("test_type")
            previous_answers = it.getIntegerArrayList("previous_answers") as ArrayList<Int>
            if(test_type == "synonym_test" || test_type == "antonym_test") {
                previous_answords = it.getStringArrayList("previous_answords") as ArrayList<String>
            }
            selected_answers = it.getIntegerArrayList("selected_answers") as ArrayList<Int>
            correct_answers_count= it.getInt("answers_count")
        }

        when(test_type) {
            "word_test" -> {
                binding.resultIntroText.text = "Day ${daynum} Word Test Result:"

                val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
                val jsonArray = JSONArray(jsonString)
                var seqNum = 0

                val introData = ResultData(0, "Subject", "Answer", "Selected")
                result_datas.add(introData)
                for(index in previous_answers) {
                    val subjectObject = jsonArray.getJSONObject(index)
                    val subject = subjectObject.getString("word")
                    var answer = subjectObject.getJSONArray("definitions").toArrayList()
                    if(answer.size > 3) answer = answer.slice(0..2) as ArrayList<String>

                    val selectedObject = jsonArray.getJSONObject(selected_answers[seqNum])
                    var selected = selectedObject.getJSONArray("definitions").toArrayList()
                    if(selected.size >3) selected = selected.slice(0..2) as ArrayList<String>

                    seqNum++
                    val newResultData: ResultData = ResultData(seqNum, subject, answer.toString(), selected.toString())
                    result_datas.add(newResultData)
                }
            }
            "synonym_test" -> {
                binding.resultIntroText.text = "Day ${daynum} Synonym Test Result:"

                val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
                val jsonArray = JSONArray(jsonString)
                var seqNum = 0

                val introData = ResultData(0, "Subject", "Answer", "Selected")
                result_datas.add(introData)
                for(index in previous_answers) {
                    val subjectObject = jsonArray.getJSONObject(index)
                    val subject = subjectObject.getString("word")
                    var answer = previous_answords[seqNum]
                    //if(answer.size > 3) answer = answer.slice(0..2) as ArrayList<String>

                    lateinit var selected: String
                    if(index == selected_answers[seqNum]) {
                        selected = previous_answords[seqNum]
                    } else {
                        val selectedObject = jsonArray.getJSONObject(selected_answers[seqNum])
                        selected = selectedObject.getString("word")
                    }

                    //if(selected.size >3) selected = selected.slice(0..2) as ArrayList<String>

                    seqNum++
                    val newResultData: ResultData = ResultData(seqNum, subject, answer, selected)
                    result_datas.add(newResultData)
                }
            }
            "antonym_test" -> {
                binding.resultIntroText.text = "Day ${daynum} Antonym Test Result:"

                val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
                val jsonArray = JSONArray(jsonString)
                var seqNum = 0

                val introData = ResultData(0, "Subject", "Answer", "Selected")
                result_datas.add(introData)
                for(index in previous_answers) {
                    val subjectObject = jsonArray.getJSONObject(index)
                    val subject = subjectObject.getString("word")
                    var answer = previous_answords[seqNum]
                    //if(answer.size > 3) answer = answer.slice(0..2) as ArrayList<String>

                    lateinit var selected: String
                    if (index == selected_answers[seqNum]) {
                        selected = previous_answords[seqNum]
                    } else {
                        val selectedObject = jsonArray.getJSONObject(selected_answers[seqNum])
                        selected = selectedObject.getString("word")
                    }

                    //if(selected.size >3) selected = selected.slice(0..2) as ArrayList<String>

                    seqNum++
                    val newResultData: ResultData = ResultData(seqNum, subject, answer, selected)
                    result_datas.add(newResultData)
                }
            }
        }
        binding.resultCountText.text = "${correct_answers_count} / ${quantity}"

        binding.resultRecycler.layoutManager = LinearLayoutManager(context as WordTestActivity)
        binding.resultRecycler.adapter = ResultAdapter(result_datas)
        binding.resultRecycler.addItemDecoration(DividerItemDecoration(context as WordTestActivity, LinearLayoutManager.VERTICAL))

        binding.backBtn.setOnClickListener {
            activity?.let {
                val intent: Intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return binding.root
    }

    class MyViewHolder(val binding: ResultRecyclerViewBinding): RecyclerView.ViewHolder(binding.root)

    class ResultAdapter(val dataList: ArrayList<ResultData>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            MyViewHolder(ResultRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val binding = (holder as MyViewHolder).binding
            if(dataList[position].num == 0) {
                binding.testNum.text = "Number"
            } else binding.testNum.text = dataList[position].num.toString()
            binding.subjectWord.text = dataList[position].subject
            binding.answer.text = dataList[position].answer
            binding.selected.text = dataList[position].selected

            if(dataList[position].num == 0)  binding.resultLayout.setBackgroundColor(
                    Color.rgb(225, 225, 30))
            else if (binding.answer.text == binding.selected.text) binding.resultLayout.setBackgroundColor(
                Color.rgb(30, 225, 30))
            else binding.resultLayout.setBackgroundColor(
                Color.rgb(225, 30, 0))

            setSingleScroll(binding.subjectWord)
            setSingleScroll(binding.answer)
            setSingleScroll(binding.selected)
        }

    }
}