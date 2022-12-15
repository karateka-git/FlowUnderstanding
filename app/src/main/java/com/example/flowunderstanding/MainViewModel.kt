package com.example.flowunderstanding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val helloTextLD: LiveData<String> = MutableLiveData("Hello World!")
}