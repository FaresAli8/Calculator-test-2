package com.procalc.app.domain

import java.text.DecimalFormat
import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

object Evaluator {

    fun evaluate(expression: String): String {
        try {
            if (expression.isBlank()) return ""
            
            // Normalize: Replace symbols
            var saneExpr = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("%", "/100")
                .replace("√", "sqrt")
            
            // Very basic validation
            if (saneExpr.lastOrNull()?.isDigit() == false && saneExpr.last() != ')') {
                // Drop last operator if it's incomplete
                saneExpr = saneExpr.dropLast(1)
            }

            val tokens = tokenize(saneExpr)
            val result = evaluatePostfix(infixToPostfix(tokens))
            
            return formatResult(result)
        } catch (e: Exception) {
            return "Error"
        }
    }

    private fun formatResult(value: Double): String {
        val df = DecimalFormat("#.########")
        return df.format(value)
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            if (c.isDigit() || c == '.') {
                val sb = StringBuilder()
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString())
            } else if (c.isLetter()) {
                val sb = StringBuilder()
                while (i < expr.length && expr[i].isLetter()) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString()) // e.g., sqrt
            } else {
                if (!c.isWhitespace()) tokens.add(c.toString())
                i++
            }
        }
        return tokens
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<String>()
        
        for (token in tokens) {
            if (token.toDoubleOrNull() != null) {
                output.add(token)
            } else if (token == "(") {
                operators.push(token)
            } else if (token == ")") {
                while (operators.isNotEmpty() && operators.peek() != "(") {
                    output.add(operators.pop())
                }
                if (operators.isNotEmpty()) operators.pop()
            } else if (token == "sqrt") {
                operators.push(token)
            } else {
                // Operators
                while (operators.isNotEmpty() && precedence(operators.peek()) >= precedence(token)) {
                     output.add(operators.pop())
                }
                operators.push(token)
            }
        }
        while (operators.isNotEmpty()) output.add(operators.pop())
        return output
    }

    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = Stack<Double>()
        for (token in postfix) {
            if (token.toDoubleOrNull() != null) {
                stack.push(token.toDouble())
            } else {
                if (token == "sqrt") {
                    val a = stack.pop()
                    stack.push(sqrt(a))
                } else {
                    val b = stack.pop()
                    val a = if (stack.isNotEmpty()) stack.pop() else 0.0 // Handle unary minus implicitly if needed, mostly binary here
                    when (token) {
                        "+" -> stack.push(a + b)
                        "-" -> stack.push(a - b)
                        "*" -> stack.push(a * b)
                        "/" -> stack.push(a / b)
                        "^" -> stack.push(a.pow(b))
                    }
                }
            }
        }
        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "sqrt" -> 4
            "^" -> 3
            "*", "/" -> 2
            "+", "-" -> 1
            else -> 0
        }
    }
}