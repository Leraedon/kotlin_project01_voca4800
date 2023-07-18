package com.example.vocaapplication

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.annotation.Dimension
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import com.example.vocaapplication.databinding.ActivityDaySelectBinding

class DaySelectActivity : AppCompatActivity() {
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDaySelectBinding.inflate(layoutInflater)
        val menu_type = intent.getStringExtra("menu_type")
        setContentView(binding.root)

        when(menu_type) {
            "list" -> {
                binding.dayallBtn.setOnClickListener {
                    val intent: Intent = Intent(this, WordListActivity::class.java)
                    intent.putExtra("daynum", 0)
                    startActivity(intent)
                }
            }
            "word_test" -> {
                binding.dayallBtn.setOnClickListener {
                    val intent: Intent = Intent(this, WordTestActivity::class.java)
                    intent.putExtra("daynum", 0)
                    intent.putExtra("test_type", "word_test")
                    startActivity(intent)
                }
            }
            "synonym_test" -> {
                binding.dayallBtn.visibility = View.GONE
            }
            "antonym_test" -> {
                binding.dayallBtn.visibility = View.GONE
            }
        }

        if(menu_type == "antonym_test") { // ANTONYM TEST 메뉴 선택 시
            for(i in 1..12) {
                val row = TableRow(this)
                val btn = Button(this)

                btn.text = "Day ${5*(i-1)+1} ~ Day ${5*i}"
                btn.height = 70.dp
                btn.width = 350.dp
                btn.setBackgroundColor(getResources().getColor(R.color.yellow_theme))
                btn.setTextColor(getResources().getColor(R.color.cyan_theme))
                btn.setTextSize(Dimension.SP, 22F)
                btn.setOnClickListener {
                    val intent: Intent = Intent(this, WordTestActivity::class.java)
                    intent.putExtra("daynum", 5*(i-1)+1)
                    intent.putExtra("test_type", "antonym_test")
                    startActivity(intent)
                }

                row.addView(btn)
                binding.daysTable.addView(row)
            }
        }

        else { // 이외의 메뉴 선택 시
            for(i in 1..15) { // Day 1~60에 해당하는 60개 버튼을 코드로 생성
                val row = TableRow(this)
                for(j in 1..4) { // 줄당 4개 버튼, 총 15줄
                    val btn = Button(this)

                    // 버튼 속성 설정
                    btn.text = "Day ${4*(i-1)+j}"
                    btn.height = 76.dp
                    btn.setBackgroundColor(getResources().getColor(R.color.yellow_theme))
                    btn.setTextColor(getResources().getColor(R.color.cyan_theme))
                    btn.setTextSize(Dimension.SP, 22F)
                    /*val layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.WRAP_CONTENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )*/
                    //layoutParams.setMargins(5.dp, 5.dp, 5.dp, 0)
                    //btn.layoutParams = layoutParams
                    btn.setOnClickListener {
                        when(menu_type) {
                            "list" -> {
                                val intent: Intent = Intent(this, WordListActivity::class.java)
                                intent.putExtra("daynum", 4*(i-1)+j)
                                startActivity(intent)
                            }
                            "word_test" -> {
                                val intent: Intent = Intent(this, WordTestActivity::class.java)
                                intent.putExtra("daynum", 4*(i-1)+j)
                                intent.putExtra("test_type", "word_test")
                                startActivity(intent)
                            }
                            "synonym_test" -> {
                                val intent: Intent = Intent(this, WordTestActivity::class.java)
                                intent.putExtra("daynum", 4*(i-1)+j)
                                intent.putExtra("test_type", "synonym_test")
                                startActivity(intent)
                            }
                        }

                    }
                    row.addView(btn)
                }
                binding.daysTable.addView(row)
            }
        }


        /*for(i in 1..60) {
            val btn = Button(this)
            btn.text = "Day ${i}"
            btn.height = 76.dp
            btn.setBackgroundColor(getResources().getColor(R.color.yellow_theme))
            binding.daysGrid.addView(btn)
        }*/
    }
}