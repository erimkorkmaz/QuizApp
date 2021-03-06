package com.erimkorkmaz.quizapp.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.R.color
import com.erimkorkmaz.quizapp.model.Category
import com.erimkorkmaz.quizapp.model.Question
import com.erimkorkmaz.quizapp.utils.StringUtils
import com.erimkorkmaz.quizapp.utils.htmlDecode
import com.erimkorkmaz.quizapp.utils.toolbarIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.erimkorkmaz.quizapp.viewmodel.QuizViewModel
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.layout_common_toolbar.view.*
import java.util.*

class QuizActivity : AppCompatActivity() {

    private lateinit var quizViewModel: QuizViewModel
    private lateinit var category: Category
    private lateinit var questions: List<Question>
    private lateinit var currentQuestion: Question
    private lateinit var buttons: List<Button>
    private lateinit var correctButton: Button
    private var timer: CountDownTimer? = null

    private var score = 0
    private var questionId = 0

    companion object {
        private const val CATEGORY_ID = "CATEGORY ID"

        fun newIntent(context: Context, category: Category): Intent {
            val intent = Intent(context, QuizActivity::class.java)
            intent.putExtra(CATEGORY_ID, category)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        category = intent.getParcelableExtra(CATEGORY_ID)
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        if (::questions.isInitialized.not()) {
            loadData()
        }
        toolbarIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbarTitle(StringUtils.formatCategoryNames(category.name.toUpperCase()))

        included_app_bar.toolbar_common.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun loadData() {
        showProgress()
        quizViewModel.getQuestions(category.id.toString())
            .observe(this, androidx.lifecycle.Observer {
                questions = it.toMutableList()
                hideProgress()
                setupView()
                setTimer()

                button_answer1.setOnClickListener {
                    checkAnswer(button_answer1, button_answer1.text.toString())
                }
                button_answer2.setOnClickListener {
                    checkAnswer(button_answer2, button_answer2.text.toString())
                }
                button_answer3.setOnClickListener {
                    checkAnswer(button_answer3, button_answer3.text.toString())
                }
                button_answer4.setOnClickListener {
                    checkAnswer(button_answer4, button_answer4.text.toString())
                }
            })
    }

    private fun setupView() {
        buttons = listOf(button_answer1, button_answer2, button_answer3, button_answer4)
        for (button in buttons) {
            button.isClickable = true
            button.setBackgroundResource(R.drawable.rounded_edit_text)
        }
        text_score.text = "Score : $score"
        text_question_number.text = "${(questionId + 1)} / ${questions?.size}"
        currentQuestion = questions[questionId]
        text_question.text = currentQuestion.questions.htmlDecode()
        mixOptions()
        questionId++
    }

    private fun mixOptions() {
        val random = Random()
        val correctOptionNum = random.nextInt(4)
        var j = 0
        for (i in buttons.indices) {
            if (i == correctOptionNum) {
                correctButton = this.buttons[i]
                buttons[i].text = currentQuestion.correctAnswer.htmlDecode()
            } else {
                this.buttons[i].text = currentQuestion.incorrectAnswers[j].htmlDecode()
                j++
            }
        }
    }

    private fun checkAnswer(button: Button, answer: String) {
        button.isClickable = false
        if (currentQuestion.correctAnswer.htmlDecode() == answer) {
            score += 10
            text_score.text = "Score : $score"
            button.background.setTint(resources.getColor(color.secondaryDarkColor, theme))
        } else {
            button.background.setTint(resources.getColor(color.magenta, theme))
            correctButton.background.setTint(resources.getColor(color.secondaryDarkColor, theme))
        }

        val handler = Handler()
        handler.postDelayed({
            button.background.setTint(resources.getColor(color.primaryTextColor, theme))
            correctButton.background.setTint(resources.getColor(color.primaryTextColor, theme))
            timer?.cancel()
            goToNextQuestion()
        }, 500)
    }

    private fun goToNextQuestion() {
        if (questionId < questions.size) {
            currentQuestion = questions[questionId]
            setupView()
            setTimer()
        } else {
            toResultActivity()
        }
    }

    private fun toResultActivity() {
        val intent = ResultActivity.newIntent(this, category, score)
        startActivity(intent)
        finish()
    }

    private fun setTimer() {
        timer = object : CountDownTimer(20500, 1000) {
            override fun onFinish() {
                goToNextQuestion()
            }

            override fun onTick(milisUntilFinised: Long) {
                text_time_left.text = "Time Left : " + milisUntilFinised / 1000
            }
        }.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("Do you confirm that the quiz will be terminated?")
            .setCancelable(true)
            .setNegativeButton("Cancel") { dialog: DialogInterface, i: Int ->
                dialog.dismiss()
            }
            .setPositiveButton("Confirm") { dialogInterface: DialogInterface, i: Int ->
                timer?.cancel()
                finish()
            }.show()
    }

    private fun showProgress() {
        progress_quiz.visibility = View.VISIBLE
        progress_quiz.setAnimation("loading.json")
        progress_quiz.playAnimation()
        progress_quiz.loop(true)
        layout_time_score.visibility = View.GONE
        text_question_number.visibility = View.GONE
        text_question.visibility = View.GONE
        layout_answers.visibility = View.GONE
    }

    private fun hideProgress() {
        progress_quiz.visibility = View.GONE
        layout_time_score.visibility = View.VISIBLE
        text_question_number.visibility = View.VISIBLE
        text_question.visibility = View.VISIBLE
        layout_answers.visibility = View.VISIBLE
    }

}