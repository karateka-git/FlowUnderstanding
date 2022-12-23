package com.example.flowunderstanding

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.flowunderstanding.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

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
        bufferedObserver()
        exceptionObserver()
    }

    private fun exceptionObserver() {
//        lifecycleScope.launch {
//            try {
//                viewModel.flowWithException.collect {
//                    println("before exception collect item $it")
//                }
//            } catch (exception: Exception) {
//                println("collect exception with message '${exception.message}'")
//            }
//        }

//        viewModel.flowWithException
//            .onEach {
//                println("before exception collect item $it")
//            }
//            .catch { exception ->
//                println("collect exception with message '${exception.message}'")
//            }
//            .launchIn(lifecycleScope)

        lifecycleScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                println("collect exception with message '${throwable.message}'")
            }
        ) {
            viewModel.flowWithException.collect {
                println("before exception collect item $it")
            }
        }
    }

    private fun bufferedObserver() {
        viewModel.apply {
            collectFlow(flowNotBuffered, "VLADISLAV NOT_BUFFERED")
            collectFlow(flowWithBuffer, "VLADISLAV BUFFERED")
            collectFlow(flowWithConflate, "VLADISLAV CONFLATE")
            collectFlow(flowWithFlowOn, "VLADISLAV FLOW_ON")
            runBlocking {
                val time = measureTimeMillis {
                    flowNotBuffered.collectLatest {
                        println("${Thread.currentThread()} VLADISLAV COLLECT_LATEST activity receive item $it")
                        println("${Thread.currentThread()} VLADISLAV COLLECT_LATEST activity restart")
                        delay(300)
                        println("${Thread.currentThread()} VLADISLAV COLLECT_LATEST activity collected item $it")
                    }
                }
                println("${Thread.currentThread()} VLADISLAV COLLECT_LATEST activity collected time in $time ms")
            }
        }
    }

    private fun collectFlow(flow: Flow<Int>, tag: String) = runBlocking {
        val time = measureTimeMillis {
            flow.collect {
                delay(300)
                println("${Thread.currentThread()} $tag activity receive item $it")
            }
        }
        println("${Thread.currentThread()} $tag activity collected time in $time ms")
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
