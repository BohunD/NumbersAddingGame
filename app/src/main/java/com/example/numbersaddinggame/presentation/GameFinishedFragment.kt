package com.example.numbersaddinggame.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.numbersaddinggame.R
import com.example.numbersaddinggame.databinding.FragmentGameFinishedBinding
import com.example.numbersaddinggame.domain.entity.GameResult

class GameFinishedFragment : Fragment() {
    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding?: throw java.lang.RuntimeException("FragmentGameFinishedBinding == null")

    private lateinit var gameResult: GameResult
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :
            OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                retryGame()
            }
        })
        bindViews()
        binding.buttonRetry.setOnClickListener { retryGame() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs(){
         requireArguments().getParcelable<GameResult>(KEY_RESULT)?.let{
             gameResult = it
         }
    }
    private fun bindViews(){

        binding.ivEmojiResult.setImageResource(getSmileResId())
        binding.tvRequiredAnswers.text = String.format(getString(R.string.finish_required_correct),
            gameResult.gameSettings.minCountOfRightAnswers.toString())
        binding.tvYourScore.text = String.format(getString(R.string.finish_your_score),
            gameResult.countOfRightAnswers.toString())
        binding.tvRequiredPercentage.text = String.format(getString(R.string.finish_required_percent),
            gameResult.gameSettings.minPercentOfRightAnswers.toString())
        binding.tvScorePercentage.text = String.format(getString(R.string.finish_your_percent),
            getPercentOfCorrectAnswers().toString())

    }

    private fun getPercentOfCorrectAnswers()= with(gameResult){
        if( countOfQuestions ==0 ){
            0
        }
        else{
            (countOfRightAnswers / ((countOfQuestions.toDouble()) ) * 100).toInt()
        }
    }
    private fun getSmileResId(): Int{
        val res = if(gameResult.winner) {
            R.drawable.emoji_good
        }else R.drawable.emoji_bad
        return res
    }
    private fun retryGame(){
        requireActivity().supportFragmentManager.popBackStack(GameFragment.NAME, 1)
    }
    companion object{
        private const val KEY_RESULT = "result"
         const val NAME = "GameFinishedFragment"

        fun newInstance(gameResult: GameResult): GameFinishedFragment{
            return GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_RESULT, gameResult)
                }
            }
        }
    }
}