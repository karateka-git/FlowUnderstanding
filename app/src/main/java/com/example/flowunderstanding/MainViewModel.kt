package com.example.flowunderstanding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
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

    val flowWithException: Flow<Int> = flow {
        emit(1)
        throw Exception("exception")
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

    private val _suspendMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()
    val suspendSharedFlow: SharedFlow<Int> = _suspendMutableSharedFlow
    private val _replaySuspendMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    val replaySuspendSharedFlow: SharedFlow<Int> = _replaySuspendMutableSharedFlow
    private val _extraBufferSuspendMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(extraBufferCapacity = 1)
    val extraBufferSuspendSharedFlow: SharedFlow<Int> = _extraBufferSuspendMutableSharedFlow

    // запрещено, т.к. буфер равен нулю
//    private val _dropOldestMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST)
//    val dropOldestSharedFlow: SharedFlow<Int> = _dropOldestMutableSharedFlow
    private val _replayDropOldestMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val replayDropOldestSharedFlow: SharedFlow<Int> = _replayDropOldestMutableSharedFlow
    private val _extraBufferDropOldestMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val extraBufferDropOldestSharedFlow: SharedFlow<Int> = _extraBufferDropOldestMutableSharedFlow

    // запрещено, т.к. буфер равен нулю
//    private val _dropLatestMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_Latest)
//    val dropLatestSharedFlow: SharedFlow<Int> = _dropLatestMutableSharedFlow
    private val _replayDropLatestMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    val replayDropLatestSharedFlow: SharedFlow<Int> = _replayDropLatestMutableSharedFlow
    private val _extraBufferDropLatestMutableSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    val extraBufferDropLatestSharedFlow: SharedFlow<Int> = _extraBufferDropLatestMutableSharedFlow

    fun startSuspendSharedFlow() {
        viewModelScope.launch {
            for (i in 1..5) {
                _suspendMutableSharedFlow.emit(i)
                _replaySuspendMutableSharedFlow.emit(i)
                _extraBufferSuspendMutableSharedFlow.emit(i)
                delay(1000)
            }
        }
    }

    fun startDropOldestSharedFlow() {
        viewModelScope.launch {
            for (i in 1..5) {
                _replayDropOldestMutableSharedFlow.emit(i)
                _extraBufferDropOldestMutableSharedFlow.emit(i)
                delay(1000)
            }
        }
    }

    fun startDropLatestSharedFlow() {
        viewModelScope.launch {
            for (i in 1..5) {
                _replayDropLatestMutableSharedFlow.emit(i)
                _extraBufferDropLatestMutableSharedFlow.emit(i)
                delay(1000)
            }
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
