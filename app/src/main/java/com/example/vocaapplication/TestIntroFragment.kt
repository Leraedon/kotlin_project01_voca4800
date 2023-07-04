package com.example.vocaapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vocaapplication.databinding.FragmentTestIntroBinding

class TestIntroFragment : Fragment() {
    lateinit var binding: FragmentTestIntroBinding
    private var test_type: String? = null
    private var daynum: Int? = null
    private var max_quantity: Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTestIntroBinding.inflate(inflater, container, false)

        arguments?.let {
            test_type = it.getString("test_type")
            daynum = it.getInt("daynum")
        }
        when(test_type) {
            "word_test" -> {
                binding.IntroText.text = "Word Test: Day ${daynum}"
                if(daynum == 0) max_quantity = 400
                else max_quantity = 80
            }
            "synonym_test" -> {
                binding.IntroText.text = "Synonym Test: Day ${daynum}"
                max_quantity = 20
            }
            "antonym_test" -> {
                binding.IntroText.text = "Antonym Test: Day ${daynum}"
                binding.QuestionsQuantity.text = "10"
                max_quantity = 10
            }
        }

        binding.minusBtn.setOnClickListener {
            if(binding.QuestionsQuantity.text.toString().toInt() > 1) {
                binding.QuestionsQuantity.text = (binding.QuestionsQuantity.text.toString().toInt() - 1).toString()
            }
        }
        binding.plusBtn.setOnClickListener {
            if(binding.QuestionsQuantity.text.toString().toInt() < max_quantity) {
                binding.QuestionsQuantity.text = (binding.QuestionsQuantity.text.toString().toInt() + 1).toString()
            }
        }
        binding.testStartBtn.setOnClickListener {
            val quantity = binding.QuestionsQuantity.text.toString().toInt()
            val wordTestActivity = activity as WordTestActivity
            when(test_type) {
                "word_test" -> {
                    daynum?.let { it1 ->
                        wordTestActivity.setDataAtFragment(WordTestFragment(),
                            it1, quantity)
                    }
                }

                "synonym_test" -> {
                    daynum?.let { it1 ->
                        wordTestActivity.setDataAtFragment(SynonymTestFragment(),
                            it1, quantity)
                    }
                }

                "antonym_test" -> {
                    daynum?.let { it1 ->
                        wordTestActivity.setDataAtFragment(AntonymTestFragment(),
                            it1, quantity)
                    }
                }
            }
        }

        return binding.root
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
        //@JvmStatic
        /*fun newInstance(test_type: String, daynum: Int =
            WordTestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TYPE, test_type)
                    putInt(ARG_DAYNUM, daynum)
                }
            }*/
    }
}