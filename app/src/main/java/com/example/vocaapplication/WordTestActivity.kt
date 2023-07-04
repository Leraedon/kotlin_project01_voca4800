package com.example.vocaapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.vocaapplication.databinding.ActivityWordTestBinding
import com.example.vocaapplication.databinding.FragmentTestIntroBinding

class WordTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWordTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val daynum = intent.getIntExtra("daynum", 1)
        val test_type = intent.getStringExtra("test_type")
        if (test_type != null) {
            setDataAtFragment(TestIntroFragment(), test_type, daynum)
        }
    }
    fun setDataAtFragment(fragment: Fragment, test_type: String, daynum: Int) {
        val bundle = Bundle()
        bundle.putString("test_type", test_type)
        bundle.putInt("daynum", daynum)

        fragment.arguments = bundle
        setFragment(fragment)
    }
    fun setDataAtFragment(fragment: Fragment, daynum: Int, quantity: Int) {
        val bundle = Bundle()
        bundle.putInt("daynum", daynum)
        bundle.putInt("quantity", quantity)

        fragment.arguments = bundle
        setFragment(fragment)
    }
    /*fun setDataAtFragment(fragment: Fragment, daynum: Int, quantity: Int,
                          questionsCount: Int, correctCount: Int, previousAnswerList: ArrayList<Int>) {
        val bundle = Bundle()
        bundle.putInt("daynum", daynum)
        bundle.putInt("quantity", quantity)
        bundle.putInt("questions_count", questionsCount)
        bundle.putInt("answers_count", correctCount)
        bundle.putIntegerArrayList("previous_answers", previousAnswerList)

        fragment.arguments = bundle
        setFragment(fragment)
    }*/
    fun setDataAtFragment(fragment: Fragment, daynum: Int, quantity: Int, test_type: String,
                          previousAnswerList: ArrayList<Int>, selectedAnswerList: ArrayList<Int>, answerCount: Int) {
        val bundle = Bundle()
        bundle.putInt("daynum", daynum)
        bundle.putInt("quantity", quantity)
        bundle.putString("test_type", test_type)
        bundle.putIntegerArrayList("previous_answers", previousAnswerList)
        bundle.putIntegerArrayList("selected_answers", selectedAnswerList)
        bundle.putInt("answers_count", answerCount)

        fragment.arguments = bundle
        setFragment(fragment)
    }
    fun setDataAtFragment(fragment: Fragment, daynum: Int, quantity: Int, test_type: String,
                          previousAnswerList: ArrayList<Int>, previousAnswordList: ArrayList<String>,
                          selectedAnswerList: ArrayList<Int>, answerCount: Int) {
        val bundle = Bundle()
        bundle.putInt("daynum", daynum)
        bundle.putInt("quantity", quantity)
        bundle.putString("test_type", test_type)
        bundle.putIntegerArrayList("previous_answers", previousAnswerList)
        bundle.putStringArrayList("previous_answords", previousAnswordList)
        bundle.putIntegerArrayList("selected_answers", selectedAnswerList)
        bundle.putInt("answers_count", answerCount)

        fragment.arguments = bundle
        setFragment(fragment)
    }

    fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.fragment_word_test_content, fragment)
        transaction.commit()
    }
}

