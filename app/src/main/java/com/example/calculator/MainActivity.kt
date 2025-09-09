package com.example.calculator

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.text.isEmpty

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputValsTV = findViewById<TextView>(R.id.inputValsTV)
        inputValsTV.text = ""
        resultTV = findViewById<TextView>(R.id.resultTV)
        resultTV.text = "0"
    }

    // Views
    private lateinit var inputValsTV: TextView
    private lateinit var resultTV: TextView

    // Variables
    var commaIsPlaced: Boolean = false
    var equalsIsPressed: Boolean = false
    var lastResult: String = ""
    val operatorsList: Array<String> = arrayOf("÷","×","-","+","=")


    // Functions
    fun clearAllAction(view: View) {
        inputValsTV.text = ""
        resultTV.text = "0"
        commaIsPlaced = false
        equalsIsPressed = false
        lastResult = ""
        resetStyles()
    }

    fun backSpaceAction(view: View) {
        if(view is Button){
            if(equalsIsPressed) return
            if(inputValsTV.text.length > 0){
                inputValsTV.text = inputValsTV.text.subSequence(0,inputValsTV.text.length-1)
                if(inputValsTV.text.length == 0){
                    inputValsTV.text = ""
                    resultTV.text = "0"
                    resetStyles()
                    return
                }
                val lastChar = inputValsTV.text.get(inputValsTV.text.length -1).toString()
                if(lastChar in operatorsList || lastChar == ","){
                    calculateResult(trimmed = true)
                } else {
                    calculateResult()
                }
            }
        }
    }

    fun typeNumberAction(view: View) {
        if (view is Button){
            if(inputValsTV.text.isEmpty() || inputValsTV.text.toString() == "0"){
                inputValsTV.text = view.text.toString()
            } else{
                if(equalsIsPressed){
                    inputValsTV.text = view.text.toString()
                    equalsIsPressed = false
                    lastResult = ""
                } else{
                    inputValsTV.append(view.text)
                }
            }
            commaIsPlaced = false
            calculateResult()
            resetStyles()
        }
    }
    fun typeFunctionalSignAction(view: View){
        if(!commaIsPlaced){
            if (view is Button) {
                if(inputValsTV.text.isEmpty()){
                    inputValsTV.append("0"+view.text)
                    resetStyles()
                    return
                }
                if(equalsIsPressed){
                    inputValsTV.text = lastResult
                    equalsIsPressed = false
                    lastResult = ""
                }
                val lastChar = inputValsTV.text.last().toString()
                if(lastChar in operatorsList){
                    val currentText = inputValsTV.text.toString()
                    val newText = currentText.subSequence(0, inputValsTV.text.length-1).toString() + view.text.toString()
                    inputValsTV.text = newText
                } else {
                    inputValsTV.append(view.text)
                    commaIsPlaced = false
                }
                resetStyles()
            }
        }
    }

    fun percentAction(view: View){
        if(!commaIsPlaced){
            if(view is Button){
                if(inputValsTV.text.isEmpty()) return
                if(equalsIsPressed){
                    inputValsTV.text = lastResult
                }
                if(inputValsTV.text.last().toString() in operatorsList) return
                val expr = inputValsTV.text.toString()
                val lastOperatorIndex = expr.lastIndexOfAny(charArrayOf('+','-','÷','×'))
                val lastNum = if (lastOperatorIndex == -1) expr else expr.substring(lastOperatorIndex + 1)

                try {
                    val number = lastNum.replace(",",".").toDouble()
                    val percentValue = number / 100

                    val prefix = if(lastOperatorIndex == -1) "" else expr.subSequence(0, lastOperatorIndex + 1).toString()
                    val newExpr = prefix + formatResult(percentValue)

                    inputValsTV.text = newExpr
                    calculateResult()
                    resetStyles()

                } catch (e: Exception) {
                    resultTV.text = "Error"
                }
            }
        }
    }

    fun commaAction(view: View) {
        if(!commaIsPlaced){
            if(view is Button) {
                if(inputValsTV.text.isEmpty()){
                    inputValsTV.append("0"+view.text)
                    commaIsPlaced = true
                    return
                }
                val lastChar = inputValsTV.text.get(inputValsTV.text.length - 1).toString()
                if(lastChar !in operatorsList){
                    var isComaPlacedInAlready = false
                    for(i in inputValsTV.text.length downTo 1){
                        val lastChar = inputValsTV.text.get(i-1).toString()
                        if(lastChar in operatorsList){
                            break
                        }
                        if(lastChar == view.text){
                            isComaPlacedInAlready = true
                            break
                        }
                    }
                    if(!isComaPlacedInAlready){
                        inputValsTV.append(view.text)
                        commaIsPlaced = true
                    }
                }
            }
        }
    }

    fun equalsAction(view: View) {
        if(inputValsTV.text.last().toString() in operatorsList){
            calculateResult(true)
        }else{
            calculateResult()
        }
        inputValsTV.setTextColor(ContextCompat.getColor(this, R.color.white))
        resultTV.setTextColor(ContextCompat.getColor(this, R.color.white))
        inputValsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,25f)
        resultTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)

        equalsIsPressed = true
        lastResult = resultTV.text.toString().replace("=","").trim()
    }

    fun resetStyles() {
        if(inputValsTV.text.isEmpty()){
            resultTV.setTextColor(ContextCompat.getColor(this, R.color.white))
            resultTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
        } else{
            inputValsTV.setTextColor(ContextCompat.getColor(this, R.color.white))
            resultTV.setTextColor(ContextCompat.getColor(this, R.color.gray))
            inputValsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
            resultTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        }
    }

    fun showResult(result: String) {
        resultTV.text = "= "+ result
    }

    fun formatResult(value: Double, scale: Int = 6): String {
        val bd = BigDecimal(value).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros()
        return bd.toPlainString().replace(".",",")
    }

    fun calculateResult(trimmed: Boolean = false){
        var expression = inputValsTV.text.toString().replace(",",".")

        if(expression == ""){
            showResult("0")
            return
        }

        if(trimmed){
            expression = expression.subSequence(0, expression.length-1).toString()
        }

        try {

            val tokens = expression.split(Regex("(?<=[÷×+-])|(?=[÷×+-])"))
                .map{ it.trim() }
                .filter{ it.isNotEmpty()}

            val numbers = mutableListOf<Double>()
            val operators = mutableListOf<String>()

            if(tokens.first() == "-"){
                val firstNum = -tokens[1].toDouble()
                numbers.add(firstNum)
                var i = 2
                while (i<tokens.size){
                    val token = tokens[i]
                    if(token in operatorsList){
                        operators.add(token)
                    } else {
                        numbers.add(token.toDouble())
                    }
                    i++
                }
            } else {
                for (token in tokens) {
                    if(token in operatorsList){
                        operators.add(token)
                    } else {
                        numbers.add(token.toDouble())
                    }
                }
            }


            var i = 0
            while (i < operators.size){
                val op = operators[i]
                if(op == "×" || op == "÷"){
                    val num1 = numbers[i]
                    val num2 = numbers[i+1]

                    if(num2 == 0.0 && op == "÷"){
                        resultTV.text = "Сan't divide by zero"
                        return
                    }
                    val result = when(op){
                        "×" -> num1 * num2
                        "÷" -> num1 / num2
                        else -> 0.0
                    }
                    numbers.removeAt(i+1)
                    numbers[i] = result
                    operators.removeAt(i)
                } else {
                    i++
                }
            }

            var finalResult = numbers[0]
            i = 0
            while (i < operators.size){
                val op = operators[i]
                val num2 = numbers[i+1]
                when(op){
                    "+" -> finalResult += num2
                    "-" -> finalResult -= num2
                }
                i++
            }

            showResult(formatResult(finalResult))

        } catch (e: Exception){
            resultTV.text = "Error"
        }
    }
}