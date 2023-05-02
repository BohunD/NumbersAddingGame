package com.example.numbersaddinggame.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.numbersaddinggame.R
import com.example.numbersaddinggame.databinding.FragmentChooseLevelBinding
import com.example.numbersaddinggame.domain.entity.Level

class ChooseLevelFragment : Fragment() {
    private var _binding: FragmentChooseLevelBinding? = null
    private val binding: FragmentChooseLevelBinding
    get() = _binding?: throw java.lang.RuntimeException("FragmentChooseLevelBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setOnClickListeners(){
        binding.btnTest.setOnClickListener {
            launchGameFragment(Level.TEST)
        }
        binding.btnEasy.setOnClickListener {
            launchGameFragment(Level.EASY)
        }
        binding.btnMiddle.setOnClickListener {
            launchGameFragment(Level.MEDIUM)
        }
        binding.btnHard.setOnClickListener {
            launchGameFragment(Level.HARD)
        }
    }
    private fun launchGameFragment(level: Level){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFragment.newInstance(level))
            .addToBackStack(GameFragment.NAME).commit()
    }

    companion object{

        const val NAME = "ChooseLevelFragment"
        fun newInstance(): ChooseLevelFragment{
            return ChooseLevelFragment()
        }
    }
}