package com.example.flowunderstanding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainViewModel : ViewModel() {

    companion object {
        const val HELLO_TEXT_CONST: String = "Hello World!"
    }

    val helloTextFlow: Flow<Char?> =
        getCharInfinityFlow(HELLO_TEXT_CONST)

    val helloTextFlowReverse: Flow<Char?> =
        getCharInfinityFlow(HELLO_TEXT_CONST.reversed())

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
