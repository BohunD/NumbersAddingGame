package com.example.numbersaddinggame.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.numbersaddinggame.R
import com.example.numbersaddinggame.data.GameRepositoryImpl
import com.example.numbersaddinggame.domain.entity.GameResult
import com.example.numbersaddinggame.domain.entity.GameSettings
import com.example.numbersaddinggame.domain.entity.Level
import com.example.numbersaddinggame.domain.entity.Question
import com.example.numbersaddinggame.domain.usecases.GenerateQuestionUseCase
import com.example.numbersaddinggame.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level : Level): ViewModel() {

    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private lateinit var gameSettings: GameSettings
    private var timer: CountDownTimer?= null

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _enoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val enoughCountOfRightAnswers: LiveData<Boolean>
        get() = _enoughCountOfRightAnswers

    private val _enoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val enoughPercentOfRightAnswers: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    init{
        startGame()
    }
    private fun startGame(){
        getGameSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    private fun getGameSettings(){
        gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer(){
        timer = object : CountDownTimer(
            (gameSettings.gameTimeInSeconds* MILLIS_IN_SECOND).toLong(),
            MILLIS_IN_SECOND){
            override fun onTick(p0: Long) {
                _formattedTime.value = formatTime(p0)
            }

            override fun onFinish() {
                finishGame()
            }

        }
        timer?.start()
    }

    fun chooseAnswer(number: Int){
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun checkAnswer(number: Int):Boolean {
        val rightAnswer = question.value?.rightAnswer
        countOfQuestions++
        if(number == rightAnswer){
            countOfRightAnswers++
            return true
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    private fun generateQuestion(){
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun updateProgress(){
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers)
        _enoughCountOfRightAnswers.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercentOfRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int{
        if(countOfRightAnswers == 0)
            return 0
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
    private fun finishGame(){
        val gameResult = GameResult(
            winner = enoughCountOfRightAnswers.value == true
                    && enoughPercentOfRightAnswers.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
        )
        _gameResult.value = gameResult
    }

    private fun formatTime(p0: Long): String{
        val seconds = p0 / MILLIS_IN_SECOND
        val minutes = seconds / SECONDS_IN_MINUTE
        val extraSeconds = seconds % SECONDS_IN_MINUTE
        return String.format("%02d:%02d", minutes, extraSeconds)
    }

    companion object{
        private const val MILLIS_IN_SECOND = 1000L
        private const val SECONDS_IN_MINUTE = 60
    }

}