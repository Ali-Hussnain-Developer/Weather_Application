package com.example.arrivyfirsttask.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.arrivyfirsttask.R
import com.example.arrivyfirsttask.databinding.FragmentDetailScreenBinding
import com.example.arrivyfirsttask.databinding.FragmentHomeScreenBinding

class DetailScreenFragment : Fragment() {
    private var _binding: FragmentDetailScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using the binding object
        _binding = FragmentDetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }

}