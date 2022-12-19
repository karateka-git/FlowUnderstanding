package com.example.flowunderstanding

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.flowunderstanding.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.coroutineContext

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
        joinMethodsObserver()
        accumulatorObserver()
    }

    private fun accumulatorObserver() {
        viewModel.apply {
            binding.reduceTesting.apply {
                text = String().println("reduce:")
                flowReduce.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.foldTesting.apply {
                text = String().println("fold:")
                flowFold.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.scanTesting.apply {
                text = String().println("scan:")
                flowScan.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
        }
    }

    private fun joinMethodsObserver() {
        viewModel.apply {
            binding.mergeTesting.apply {
                text = String().println("merge:")
                flowMerge.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.zipTesting.apply {
                text = String().println("zip:")
                flowZip.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.combineTesting.apply {
                text = String().println("combine:")
                flowCombine.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.flatMapConcatTesting.apply {
                text = String().println("flatMapConcat:")
                flowFlatMapConcat.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.flatMapMergeTesting.apply {
                text = String().println("flatMapMerge:")
                flowFlatMapMerge.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.flatMapMergeWithConcurrencyTesting.apply {
                text = String().println("flatMapMerge").println("with concurrency = 2")
                flowFlatMapMergeWithConcurrency.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.flatMapLatestTesting.apply {
                text = String().println("flatMapLatest")
                flowFlatMapLatest.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
        }
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

    private fun String.println(str: Any) =
        plus(str.toString()).plus("\n")
}
