package com.example.vocaapplication

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.preference.PreferenceManager
import com.example.vocaapplication.databinding.FragmentSynonymTestBinding
import com.example.vocaapplication.databinding.FragmentWordTestBinding
import org.json.JSONArray

class SynonymTestFragment : Fragment() {
    lateinit var binding: FragmentSynonymTestBinding
    lateinit var prefs: SharedPreferences
    lateinit var countDownTimer: CountDownTimer
    lateinit var callback: OnBackPressedCallback
    private val TIMED_OUT = 4801
    private var daynum: Int? = null
    private var quantity: Int? = null
    private var minWordIndex: Int? = null
    private var maxWordIndex: Int? = null
    private var questions_count: Int? = null
    private var correct_answers_count: Int? = null
    private var previous_answers: ArrayList<Int> = arrayListOf()
    private var previous_answords: ArrayList<String> = arrayListOf()
    private var selected_answers: ArrayList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSynonymTestBinding.inflate(inflater, container, false)
        arguments?.let {
            daynum = it.getInt("daynum")
            quantity = it.getInt("quantity")
            questions_count = it.getInt("questions_count", 1)
            correct_answers_count = it.getInt("answers_count", 0)
        }

        startTest()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val eventHandler = DialogInterface.OnClickListener { dialog, which ->
                    if(which == DialogInterface.BUTTON_POSITIVE) {
                        countDownTimer.cancel()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .remove(this@SynonymTestFragment)
                            .commit()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }

                AlertDialog.Builder(context).run {
                    setTitle("종료 확인")
                    setMessage("테스트를 중지하고 나가시겠습니까?")
                    setPositiveButton("YES", eventHandler)
                    setNegativeButton("NO", eventHandler)
                    show()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
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


        minWordIndex = (daynum?.minus(1))?.times(80)
        maxWordIndex = (daynum?.times(80))

        val jsonString = context?.assets?.open("words.json")?.reader()?.readText()
        val jsonArray = JSONArray(jsonString)

        //정답이 될 동의어 중 다른 단어와 일치하는 경우를 거르기 위한 단어 집합
        val wordSet: MutableSet<String> = mutableSetOf()
        for(index in minWordIndex!! .. maxWordIndex!!) {
            val jsonObject = jsonArray.getJSONObject(index)
            val word = jsonObject.getString("word")
            wordSet.add(word)
        }

        // 중복 없는 인덱스 집합 4개(정답 1 + 오답 3)
        val set: MutableSet<Int> = mutableSetOf()
        while (set.size < 4) {
            val randomIndex: Int = (minWordIndex!!..maxWordIndex!!).random()
            val jsonObject = jsonArray.getJSONObject(randomIndex)
            val answerSynonyms = jsonObject.getJSONArray("synonyms").toArrayList()

            if(set.size == 0) {
                // 동의어가 입력되지 않은 단어일 경우 다시 뽑기
                if(answerSynonyms.isEmpty()) continue
                // 이미 이전 문제의 정답인 경우 정답 다시 뽑기
                if(previous_answers?.contains(randomIndex) == true) continue
                // 동의어가 동일 Day의 다른 단어와 겹친 경우 다시 뽑기
                for(word in wordSet)  {
                    if(answerSynonyms.contains(word)) continue
                }
            }
            set.add(randomIndex)
        }
        var randomList = set.toList()

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
                            it1, quantity!!, "synonym_test", previous_answers, previous_answords, selected_answers, it2
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

        if(index == answerIndex) {
            val synonyms = jsonObject.getJSONArray("synonyms")
            val answerSynonym = synonyms.toArrayList().random()
            button.text = answerSynonym
            previous_answords.add(answerSynonym)
        } else {
            val word = jsonObject.getString("word")
            button.text = word
        }
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
                binding.resultText.text = "Wrong Answer!"
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

        val buttons = arrayListOf(binding.answerButton1, binding.answerButton2, binding.answerButton3, binding.answerButton4)
        // 다음 퀴즈로 넘어가기 전까지 클릭 이벤트 제거
        for(btn in buttons) {
            btn.isClickable = false
            btn.setBackgroundColor(Color.rgb(225, 30, 0))
        }
        binding.resultText.text = "Timed Out!\n"
        binding.resultText.setTextColor(Color.rgb(225, 30, 0))
        // 결과 텍스트와 다음 퀴즈로 넘어가기 버튼 활성화
        binding.nextButton.visibility = View.VISIBLE
        binding.resultText.visibility = View.VISIBLE
        questions_count = questions_count!! + 1
        previous_answers.add(answerIndex)
        selected_answers.add(TIMED_OUT)
    }
}