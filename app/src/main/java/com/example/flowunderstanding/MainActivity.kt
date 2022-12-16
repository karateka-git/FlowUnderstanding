package com.example.flowunderstanding

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.flowunderstanding.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initObservers()
    }

    private fun initObservers() {
        helloTextObserve()
    }

    private fun helloTextObserve() {
        viewModel.helloTextFlow.onEach {
            binding.helloText.apply {
                text = it?.let {
                    text.toString().plus(it)
                } ?: ""
            }
        }.launchIn(lifecycleScope)

        viewModel.helloTextFlowReverse.onEach {
            binding.helloTextReverse.apply {
                text = it?.let {
                    text.toString().plus(it)
                } ?: ""
            }
        }.launchIn(lifecycleScope)

        // or same but different
//        lifecycleScope.launch {
//            viewModel.helloTextFlow.collect { // or forEach{ // something }.collect()
//                binding.helloText.apply {
//                    text = it?.let {
//                        text.toString().plus(it)
//                    } ?: ""
//                }
//            }
//        }
//        lifecycleScope.launch {
//            viewModel.helloTextFlowReverse.collect { // or forEach{ // something }.collect()
//                binding.helloTextReverse.apply {
//                    text = it?.let {
//                        text.toString().plus(it)
//                    } ?: ""
//                }
//            }
//        }
    }
}
