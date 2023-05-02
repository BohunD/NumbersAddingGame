package com.example.numbersaddinggame.domain.usecases

import com.example.numbersaddinggame.domain.entity.GameSettings
import com.example.numbersaddinggame.domain.entity.Level
import com.example.numbersaddinggame.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(level: Level): GameSettings{
        return repository.getGameSettings(level)
    }
}