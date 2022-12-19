package com.example.flowunderstanding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
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
