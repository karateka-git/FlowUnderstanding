package com.example.flowunderstanding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel : ViewModel() {

    companion object {
        const val HELLO_TEXT_CONST: String = "Hello World!"
    }

    val helloTextFlow: Flow<Char?> =
        getCharInfinityFlow(HELLO_TEXT_CONST)

    val helloTextFlowReverse: Flow<Char?> =
        getCharInfinityFlow(HELLO_TEXT_CONST.reversed())

    private val flow1 = flowOf(1, 2, 3).onEach { delay(1500) }
    private val flow2 = flowOf(-1, -2, -3, -4).onEach { delay(1000) }
    private val flow3 = flowOf(1, 2, 3).onEach {
        delay(100)
        println("${Thread.currentThread()} VLADISLAV viewModel emit item $it")
    }

    val flowMerge = merge(flow1, flow2)
    val flowZip = flow1.zip(flow2) { first, second ->
        "$first + $second = ${first + second}"
    }
    val flowCombine = flow1.combine(flow2) { first, second ->
        "$first + $second = ${first + second}"
    }

    @OptIn(FlowPreview::class)
    val flowFlatMapConcat = flow1.flatMapConcat { first ->
        flow2.map { second ->
            "$first + $second = ${first + second}"
        }
    }

    @OptIn(FlowPreview::class)
    val flowFlatMapMerge = flowOf(1, 2, 3).flatMapMerge { first ->
        flow2.map { second ->
            "$first + $second = ${first + second}"
        }
    }

    @OptIn(FlowPreview::class)
    val flowFlatMapMergeWithConcurrency = flowOf(1, 2, 3).flatMapMerge(2) { first ->
        flow2.map { second ->
            "$first + $second = ${first + second}"
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val flowFlatMapLatest = flowOf(1, 2, 3).flatMapLatest { first ->
        flow2.map { second ->
            "$first + $second = ${first + second}"
        }
    }

    val flowReduce = flow {
        emit(
            flow1.reduce { accumulator, value ->
                accumulator + value
            }
        )
    }

    val flowFold = flow {
        emit(
            flow1.fold(0) { accumulator, value ->
                accumulator + value
            }
        )
    }

    val flowScan = flow1.scan(0) { accumulator, value ->
        accumulator + value
    }

    val flowNotBuffered = flow3

    val flowWithBuffer = flow3.buffer()

    val flowWithConflate = flow3.conflate() // == buffer with DROP_OLDEST

    val flowWithFlowOn = flow3.map {
        println("${Thread.currentThread()} VLADISLAV viewModel first map item $it")
        it
    }.flowOn(Dispatchers.IO).map {
        println("${Thread.currentThread()} VLADISLAV viewModel second map item $it")
        it
    }

    private fun getCharInfinityFlow(text: String) = flow {
        while (true) {
            text.forEach {
                emit(it)
                delay(1000)
            }
            emit(null)
        }
    }
}
