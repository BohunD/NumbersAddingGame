package com.example.numbersaddinggame.domain.repository

import com.example.numbersaddinggame.domain.entity.GameSettings
import com.example.numbersaddinggame.domain.entity.Level
import com.example.numbersaddinggame.domain.entity.Question

interface GameRepository {

    fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question

    fun getGameSettings(level: Level): GameSettings
}