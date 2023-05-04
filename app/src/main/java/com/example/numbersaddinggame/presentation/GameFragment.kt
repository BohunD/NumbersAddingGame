package com.example.numbersaddinggame.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.numbersaddinggame.R
import com.example.numbersaddinggame.databinding.FragmentGameBinding
import com.example.numbersaddinggame.databinding.FragmentGameFinishedBinding
import com.example.numbersaddinggame.domain.entity.GameResult
import com.example.numbersaddinggame.domain.entity.GameSettings
import com.example.numbersaddinggame.domain.entity.Level

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding?: throw java.lang.RuntimeException("FragmentGameBinding == null")

    private val tvOptions  by lazy{
        mutableListOf<TextView>().apply {
            this.add(binding.tvOptionOne)
            this.add(binding.tvOptionTwo)
            this.add(binding.tvOptionThree)
            this.add(binding.tvOptionFour)
            this.add(binding.tvOptionFive)
            this.add(binding.tvOptionSix)
        }
    }

    private val args by navArgs<GameFragmentArgs>()

    private val viewModelFactory by lazy{
        GameViewModelFactory(args.level, requireActivity().application)
    }

    private val viewModel: GameViewModel by lazy{
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun observeViewModel(){
        viewModel.question.observe(viewLifecycleOwner){
            with(binding){
                tvSum.text = it.sum.toString()
                tvNumLeft.text = it.visibleNumber.toString()
                for(i in 0 until tvOptions.size){
                    tvOptions[i].text = it.options[i].toString()
                }
            }
        }
        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner){
            binding.progressBar.setProgress(it,true)

        }
        viewModel.progressAnswers.observe(viewLifecycleOwner){
            binding.tvCorrectAnswers.text = it
        }
        viewModel.enoughCountOfRightAnswers.observe(viewLifecycleOwner){
            binding.tvCorrectAnswers.setTextColor(getColorByState(it))
        }

        viewModel.enoughPercentOfRightAnswers.observe(viewLifecycleOwner) {
            val color = getColorByState(it)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }
        viewModel.formattedTime.observe(viewLifecycleOwner){
            binding.tvTimer.text = it
        }
        viewModel.gameResult.observe(viewLifecycleOwner){
            launchGameFinishedFragment(it)
        }
    }

    private fun getColorByState(state: Boolean): Int{
        val colorResId = if(state){
            android.R.color.holo_green_light
        }else android.R.color.holo_red_light
        return  ContextCompat.getColor(requireContext(), colorResId)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setClickListenersToOptions()

    }

    private fun setClickListenersToOptions(){
        for(tvOption in tvOptions){
            tvOption.setOnClickListener {
                viewModel.chooseAnswer(tvOption.text.toString().toInt())
            }
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult){
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToGameFinishedFragment(gameResult)
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}