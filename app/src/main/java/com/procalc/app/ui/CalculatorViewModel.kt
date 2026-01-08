package com.procalc.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.procalc.app.data.HistoryItem
import com.procalc.app.data.HistoryManager
import com.procalc.app.domain.Evaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "",
    val history: List<HistoryItem> = emptyList(),
    val isHistoryVisible: Boolean = false
)

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction() // +, -, *, /
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    object Decimal : CalculatorAction()
    object ToggleHistory : CalculatorAction()
    object ClearHistory : CalculatorAction()
    data class UseHistoryItem(val item: HistoryItem) : CalculatorAction()
}

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    
    private val historyManager = HistoryManager(application)
    
    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val items = historyManager.getHistory()
            _state.update { it.copy(history = items) }
        }
    }

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> {
                _state.update { it.copy(expression = it.expression + action.number) }
                calculatePreview()
            }
            is CalculatorAction.Operation -> {
                if (_state.value.expression.isNotEmpty()) {
                    _state.update { it.copy(expression = it.expression + action.operation) }
                } else if (_state.value.result.isNotEmpty()) {
                    // Chain operation
                     _state.update { it.copy(expression = it.result + action.operation, result = "") }
                }
            }
            is CalculatorAction.Decimal -> {
                 if (!_state.value.expression.endsWith(".")) {
                    _state.update { it.copy(expression = it.expression + ".") }
                 }
            }
            is CalculatorAction.Clear -> {
                _state.update { it.copy(expression = "", result = "") }
            }
            is CalculatorAction.Delete -> {
                _state.update { 
                    it.copy(expression = it.expression.dropLast(1)) 
                }
                calculatePreview()
            }
            is CalculatorAction.Calculate -> {
                performCalculation(saveToHistory = true)
            }
            is CalculatorAction.ToggleHistory -> {
                _state.update { it.copy(isHistoryVisible = !it.isHistoryVisible) }
                if (_state.value.isHistoryVisible) loadHistory()
            }
            is CalculatorAction.ClearHistory -> {
                historyManager.clearHistory()
                loadHistory()
            }
            is CalculatorAction.UseHistoryItem -> {
                _state.update { it.copy(expression = action.item.result, isHistoryVisible = false) }
            }
        }
    }

    private fun calculatePreview() {
        if (_state.value.expression.isEmpty()) {
            _state.update { it.copy(result = "") }
            return
        }
        // Don't save to history on preview
        val res = Evaluator.evaluate(_state.value.expression)
        if (res != "Error") {
            _state.update { it.copy(result = res) }
        }
    }

    private fun performCalculation(saveToHistory: Boolean) {
        val expr = _state.value.expression
        val res = Evaluator.evaluate(expr)
        
        _state.update { it.copy(result = res) }

        if (saveToHistory && res != "Error" && expr.isNotEmpty()) {
            val item = HistoryItem(expression = expr, result = res)
            historyManager.saveHistory(item)
            loadHistory()
            // Move result to expression for next calculation
            _state.update { it.copy(expression = res, result = "") }
        }
    }
}