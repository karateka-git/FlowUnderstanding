package com.example.flowunderstanding

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.flowunderstanding.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        sharedFlowObserver()
        stateFlowObserver()
    }

    private fun stateFlowObserver() {
        viewModel.apply {
            startStateFlow()
            binding.stateFlowTesting.apply {
                text = String().println("state flow:")
                stateFlow.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
        }
    }

    private fun sharedFlowObserver() {
        viewModel.apply {
            startSuspendSharedFlow()
            binding.suspendSharedFlowTesting.apply {
                text = String().println("suspend shared flow:")
                lifecycleScope.launch {
                    suspendSharedFlow.collect {
                        delay(2000)
                        text = text.toString().println(it)
                    }
                }
            }
            binding.suspendSharedFlowTesting1.apply {
                text = String().println("suspend shared flow 1:")
                lifecycleScope.launch {
                    suspendSharedFlow.collect {
                        delay(5000)
                        text = text.toString().println(it)
                    }
                }
            }
            binding.replaySuspendSharedFlowTesting.apply {
                text = String().println("replay suspend shared flow:")
                replaySuspendSharedFlow.onEach {
                    delay(1000)
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }
            binding.extraBufferSuspendSharedFlowTesting.apply {
                text = String().println("extra buffer suspend shared flow:")
                extraBufferSuspendSharedFlow.onEach {
                    text = text.toString().println(it)
                }.launchIn(lifecycleScope)
            }

            lifecycleScope.launch {
                startDropOldestSharedFlow()
                delay(2000)
                binding.dropOldestSharedFlowTesting.apply {
                    text = String().println("drop oldest shared flow:")
                    text = text.toString().println("запрещено, т.к. буфер равен нулю")
                    // запрещено, т.к. буфер равен нулю
//                dropOldestSharedFlow.onEach {
//                    text = text.toString().println(it)
//                }.launchIn(lifecycleScope)
                }
                binding.replayDropOldestSharedFlowTesting.apply {
                    text = String().println("replay drop oldest shared flow:")
                    replayDropOldestSharedFlow.onEach {
                        text = text.toString().println(it)
                    }.launchIn(lifecycleScope)
                }
                binding.extraBufferDropOldestSharedFlowTesting.apply {
                    text = String().println("extra buffer drops oldest shared flow:")
                    extraBufferDropOldestSharedFlow.onEach {
                        text = text.toString().println(it)
                    }.launchIn(lifecycleScope)
                }
            }

            lifecycleScope.launch {
                startDropLatestSharedFlow()
                delay(2000)
                binding.dropLatestSharedFlowTesting.apply {
                    text = String().println("drop latest shared flow:")
                    text = text.toString().println("запрещено, т.к. буфер равен нулю")
                    // запрещено, т.к. буфер равен нулю
//                dropOldestSharedFlow.onEach {
//                    text = text.toString().println(it)
//                }.launchIn(lifecycleScope)
                }
                binding.replayDropLatestSharedFlowTesting.apply {
                    text = String().println("replay drop latest shared flow:")
                    replayDropLatestSharedFlow.onEach {
                        text = text.toString().println(it)
                    }.launchIn(lifecycleScope)
                }
                binding.extraBufferDropLatestSharedFlowTesting.apply {
                    text = String().println("extra buffer drops latest shared flow:")
                    extraBufferDropLatestSharedFlow.onEach {
                        text = text.toString().println(it)
                    }.launchIn(lifecycleScope)
                }
            }
        }
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
