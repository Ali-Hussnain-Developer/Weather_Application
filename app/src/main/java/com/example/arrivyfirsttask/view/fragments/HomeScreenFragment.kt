package com.example.arrivyfirsttask.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.arrivyfirsttask.R
import com.example.arrivyfirsttask.databinding.FragmentHomeScreenBinding
import com.example.arrivyfirsttask.utils.NetworkUtil

class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using the binding object
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        checkInternetConnection()
    }

    private fun checkInternetConnection() {
        if (NetworkUtil.isInternetAvailable(requireContext())) {
            // Internet is available, perform API call
         //   performApiCall()
        } else {
            // Show a message or take appropriate action
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }
}