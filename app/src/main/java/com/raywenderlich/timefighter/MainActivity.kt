package com.raywenderlich.timefighter

import android.content.IntentSender.OnFinished
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import  android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity() {

    // Assign the name of the class to TAG.
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button

    private var score = 0
    private var gameStarted = false

    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 60000
    private var countDownInterval: Long = 1000
    private var timeLeft = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Log a message when the Activity is created.
        Log.d(TAG, "onCreate called. Score is: $score")
        setContentView(R.layout.activity_main)
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)

        // Use the animation.
        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this,
                R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        // If savedInstanceState contains value, get the values of score and timeLeft from the Bundle.
        if (savedInstanceState != null) {
            // Then assign those values to the properties
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            // And restore the game.
            restoreGame()
        }
        else {
            resetGame()
        }
    }

    // Called before a configuration change happens to save anything important.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        // Cancel the game timer and add a log to track when the method is called.
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeft")
    }

    // Clean the Activity up when it is being destroyed.
    override fun onDestroy() {
        // Perform any essential cleanup and add a final log to track when the method is called.
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    // Override the Activity callback when it attempts to create the menu.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu)
        // Set up menu layout for the Activity.
        menuInflater.inflate(R.menu.menu, menu)
        // Let the Activity know that the menu is set up.
        return true
    }

    // This method is called when the user selects a menu item.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_item) {
            showInfo()
        }
        return true
    }

    // Set up a dialog View.
    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title,
            BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun incrementScore() {
        score++

        val newScore = getString(R.string.your_score, score)
        gameScoreTextView.text = newScore

        if (!gameStarted) {
            startGame()
        }
    }

    private fun resetGame() {
        score = 0

        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore

        val initialTimeLeft = getString(R.string.time_left, 60)
        timeLeftTextView.text = initialTimeLeft

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    // Set up the TextViews and countDownTimer properties using the values inserted into the Bundle
    // before the change in orientation.
    private fun restoreGame() {

        val restoredScore = getString(R.string.your_score, score)
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = restoredTime

        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(), countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {

                timeLeft = millisUntilFinished.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

    // Track the variables we want to save when the orientation changes.
    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }
}