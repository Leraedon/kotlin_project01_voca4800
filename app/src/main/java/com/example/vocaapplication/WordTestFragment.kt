package com.example.vocaapplication

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.preference.PreferenceManager
import com.example.vocaapplication.databinding.FragmentWordTestBinding
import org.json.JSONArray
import kotlin.random.Random

class WordTestFragment : Fragment() {
    lateinit var binding: FragmentWordTestBinding
    lateinit var prefs: SharedPreferences
    lateinit var countDownTimer: CountDownTimer
    private var daynum: Int? = null
    private var quantity: Int? = null
    private var minWordIndex: Int? = null
    private var maxWordIndex: Int? = null
    private var questions_count: Int? = null
    private var correct_answers_count: Int? = null
    private var previous_answers: ArrayList<Int> = arrayListOf()
    private var selected_answers: ArrayList<Int> = arrayListOf()
    private val TIMED_OUT = 4801

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordTestBinding.inflate(inflater, container, false)
        // 날짜번호, 문항수 가져오기
        arguments?.let {
            daynum = it.getInt("daynum")
            quantity = it.getInt("quantity")
            questions_count = it.getInt("questions_count", 1)
            correct_answers_count = it.getInt("answers_count", 0)
        }

        startTest()
        // 다음 문제로 가는 버튼, 결과 텍스트 가리기
        /*binding.nextButton.visibility = View.GONE
        binding.resultText.visibility = View.GONE
        //
        binding.processText.text = "${questions_count} / ${quantity}"

        val minWordIndex: Int? = (daynum?.minus(1))?.times(80)
        val maxWordIndex: Int? = (daynum?.times(80))

        // 중복 없는 인덱스 집합 4개(정답 1 + 오답 3)
        val set: MutableSet<Int> = mutableSetOf()
        while (set.size < 4) { set.add((minWordIndex!!..maxWordIndex!!).random()) }
        var randomList = set.toList()

        val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
        val jsonArray = JSONArray(jsonString)

        /*for(index in minWordIndex!! until maxWordIndex!!) {
            val jsonObject = jsonArray.getJSONObject(index)
            val word = jsonObject.getString("word")
            val definitions = jsonObject.getJSONArray("definitions")
        }*/
        //정답 설정, 영어 단어를 상단에 보여줌
        var answerIndex = randomList[0]
        val jsonObject = jsonArray.getJSONObject(answerIndex)
        val word = jsonObject.getString("word")
        binding.subjectWord.text = word

        //배열을 다시 섞어서 4개 선지에 랜덤하게 뜻을 배치
        randomList = randomList.shuffled()
        setWordTestAnswer(binding.answerButton1, randomList[0], answerIndex)
        setWordTestAnswer(binding.answerButton2, randomList[1], answerIndex)
        setWordTestAnswer(binding.answerButton3, randomList[2], answerIndex)
        setWordTestAnswer(binding.answerButton4, randomList[3], answerIndex)

        binding.nextButton.setOnClickListener {
            //val wordTestActivity = activity as WordTestActivity
            Log.d("Leraedon", "??")



            /*daynum?.let { it1 ->
                quantity?.let { it2 ->
                    questions_count?.let { it3 ->
                        correct_answers_count?.let { it4 ->
                            previous_answers?.let { it5 ->
                                wordTestActivity.setDataAtFragment(WordTestFragment(),
                                    it1, it2, it3, it4, it5
                                )
                            }
                        }
                    }

                }
            }*/
        }
        */
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun startTest() {
        // 다음 문제로 가는 버튼, 결과 텍스트 가리기
        binding.nextButton.visibility = View.GONE
        binding.resultText.visibility = View.GONE
        //
        binding.processText.text = "${questions_count} / ${quantity}"

        //버튼 색 초기화
        val buttons = arrayListOf(binding.answerButton1, binding.answerButton2, binding.answerButton3, binding.answerButton4)
        for(btn in buttons) btn.setBackgroundColor(Color.rgb(47, 47, 47))

        if(daynum == 0) {
            minWordIndex = 0
            maxWordIndex = 4800
        } else {
            minWordIndex = (daynum?.minus(1))?.times(80)
            maxWordIndex = (daynum?.times(80))
        }


        // 중복 없는 인덱스 집합 4개(정답 1 + 오답 3)
        val set: MutableSet<Int> = mutableSetOf()
        while (set.size < 4) {
            val randomIndex: Int = (minWordIndex!!..maxWordIndex!!).random()
            if(set.size == 0 && previous_answers?.contains(randomIndex) == true) continue
            set.add(randomIndex)
        }
        var randomList = set.toList()

        val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
        val jsonArray = JSONArray(jsonString)

        //정답 설정, 영어 단어를 상단에 보여줌
        var answerIndex = randomList[0]
        val jsonObject = jsonArray.getJSONObject(answerIndex)
        val word = jsonObject.getString("word")
        binding.subjectWord.text = word

        prefs = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!
        val timerValue = prefs?.getString("timer", "None")

        if(timerValue == "None") {
            binding.countDown.visibility = View.GONE
            //Log.d("Leraedon", "1")
        } else {
            binding.countDown.visibility = View.VISIBLE
            binding.countDown.text = timerValue
            //Log.d("Leraedon", "2")
            countDownTimer = object : CountDownTimer((timerValue?.toLong()?.times(1000))!!, 1000) {
                override fun onTick(p0: Long) {
                    binding.countDown.text = (p0 / 1000).toString()
                }

                override fun onFinish() {
                    timedOut(answerIndex)
                }

            }.start()

        }

        //배열을 다시 섞어서 4개 선지에 랜덤하게 뜻을 배치
        randomList = randomList.shuffled()
        setWordTestAnswer(binding.answerButton1, randomList[0], answerIndex)
        setWordTestAnswer(binding.answerButton2, randomList[1], answerIndex)
        setWordTestAnswer(binding.answerButton3, randomList[2], answerIndex)
        setWordTestAnswer(binding.answerButton4, randomList[3], answerIndex)

        binding.nextButton.setOnClickListener {
            if(questions_count!! > quantity!!) {
                val wordTestActivity = activity as WordTestActivity
                daynum?.let { it1 ->
                    correct_answers_count?.let { it2 ->
                        wordTestActivity.setDataAtFragment(TestResultFragment(),
                            it1, quantity!!, "word_test", previous_answers, selected_answers, it2
                        )
                    }
                }
            } else {
                startTest()
            }
        }
    }

    private fun setWordTestAnswer(button: Button, index: Int, answerIndex: Int) {
        val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
        val jsonArray = JSONArray(jsonString)

        val jsonObject = jsonArray.getJSONObject(index)
        val definitions = jsonObject.getJSONArray("definitions")
        var displayDefs: ArrayList<String> = arrayListOf()
        if(definitions.length() > 3) {
            displayDefs.add(definitions[0].toString())
            displayDefs.add(definitions[1].toString())
            displayDefs.add(definitions[2].toString())
        } else {
            for(i in 0 until definitions.length()) displayDefs.add(definitions[i].toString())
        }

        button.text = displayDefs.joinToString()
        button.setOnClickListener {
            prefs = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!
            if(prefs?.getString("timer", "None") != "None") countDownTimer.cancel()
            val buttons = arrayListOf(binding.answerButton1, binding.answerButton2, binding.answerButton3, binding.answerButton4)
            // 다음 퀴즈로 넘어가기 전까지 클릭 이벤트 제거
            for(btn in buttons)  btn.isClickable = false
            // 정답을 고른 경우
            if(index == answerIndex) {
                correct_answers_count = correct_answers_count!! + 1 // 정답 카운트 +1
                button.setBackgroundColor(Color.rgb(30, 225, 30))
                binding.resultText.text = "Correct!"
                binding.resultText.setTextColor(Color.rgb(30, 225, 30))
            } else { // 오답을 고른 경우
                for(btn in buttons) btn.setBackgroundColor(Color.rgb(225, 30, 0))
                binding.resultText.text = "Wrong Answer\n" +
                        "Correct Answer: ${jsonArray.getJSONObject(answerIndex).getJSONArray("definitions")}"
                binding.resultText.setTextColor(Color.rgb(225, 30, 0))
            } // 결과 텍스트와 다음 퀴즈로 넘어가기 버튼 활성화
            binding.nextButton.visibility = View.VISIBLE
            binding.resultText.visibility = View.VISIBLE
            questions_count = questions_count!! + 1
            previous_answers.add(answerIndex)
            selected_answers.add(index)
        }
    }

    private fun timedOut(answerIndex: Int) {
        prefs = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!
        if(prefs?.getString("timer", "None") != "None") countDownTimer.cancel()

        val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
        val jsonArray = JSONArray(jsonString)

        val buttons = arrayListOf(binding.answerButton1, binding.answerButton2, binding.answerButton3, binding.answerButton4)
        // 다음 퀴즈로 넘어가기 전까지 클릭 이벤트 제거
        for(btn in buttons) {
            btn.isClickable = false
            btn.setBackgroundColor(Color.rgb(225, 30, 0))
        }
        binding.resultText.text = "Timed Out!\n" +
                "Correct Answer: ${jsonArray.getJSONObject(answerIndex).getJSONArray("definitions")}"
        binding.resultText.setTextColor(Color.rgb(225, 30, 0))
        // 결과 텍스트와 다음 퀴즈로 넘어가기 버튼 활성화
        binding.nextButton.visibility = View.VISIBLE
        binding.resultText.visibility = View.VISIBLE
        questions_count = questions_count!! + 1
        previous_answers.add(answerIndex)
        selected_answers.add(TIMED_OUT)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WordTestFragment.
         */
        // TODO: Rename and change types and number of parameters
        /*@JvmStatic
        fun newInstance(param1: String, param2: String) =
            WordTestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }*/
    }
}