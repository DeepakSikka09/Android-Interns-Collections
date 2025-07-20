package com.example.ktorapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: RepositoryImpl) : ViewModel() {
    private val _createDataFlow = MutableStateFlow<Response>(Response.Empty)
    val createDataFlow: StateFlow<Response> get() = _createDataFlow

    private val _fetchDataFlow = MutableStateFlow<Response>(Response.Empty)
    val fetchDataFlow: StateFlow<Response> get() = _fetchDataFlow
    fun createEmployee() {
        viewModelScope.launch {
            _createDataFlow.emit(Response.Loading)
                _createDataFlow.emit(
                    repository.createData(
                        Request(
                            name = "tedst",
                            salary = "123",
                            age = "23"
                        )
                    )
                )

        }
    }

    fun fetch() {
        viewModelScope.launch {
            _fetchDataFlow.emit(Response.Loading)
            _fetchDataFlow.emit(repository.fetchData())
        }
    }

}