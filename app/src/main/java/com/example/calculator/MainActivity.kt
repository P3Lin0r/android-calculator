package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        inputValsTV.text = "0"
        resultTV = findViewById<TextView>(R.id.resultTV)
        resultTV.text = "0"
    }

    // Views
    private lateinit var inputValsTV: TextView
    private lateinit var resultTV: TextView

    // Variables
    var commaIsPlaced: Boolean = false

    val operatorsList: Array<String> = arrayOf("÷","×","-","+","=")


    // Functions
    fun clearAllAction(view: View) {
        inputValsTV.text = "0"
        resultTV.text = "0"
        commaIsPlaced = false
    }

    fun backSpaceAction(view: View) {
        if(inputValsTV.text.length > 0){
            inputValsTV.text = inputValsTV.text.subSequence(0,inputValsTV.text.length-1)
            if(inputValsTV.text.length == 0){
                inputValsTV.text = "0"
            }
        }
        calculateResult()
    }

    fun typeNumberAction(view: View) {
        if (view is Button){
            val lastChar  = inputValsTV.text.get(inputValsTV.text.length - 1).toString()
            if(inputValsTV.text.toString() == "0"){
                inputValsTV.text = view.text.toString()
            } else{
                inputValsTV.append(view.text)
            }
            commaIsPlaced = false
            calculateResult()
        }
    }
    fun typeFunctionalSignAction(view: View){
        if(!commaIsPlaced){
            if (view is Button) {
                val lastChar  = inputValsTV.text.get(inputValsTV.text.length - 1).toString()
                if(lastChar in operatorsList){
                    val currentText = inputValsTV.text.toString()
                    val newText = currentText.subSequence(0, inputValsTV.text.length-1).toString() + view.text.toString()
                    inputValsTV.text = newText
                } else {
                    inputValsTV.append(view.text)
                    commaIsPlaced = false
                }
            }
        }
    }

    fun percentAction(view: View){
        if(!commaIsPlaced){
            val expr = inputValsTV.text.toString()
            val lastOperatorIndex = expr.lastIndexOfAny(charArrayOf('+','-','÷','×'))
            val lastNum = if (lastOperatorIndex == -1) expr else expr.substring(lastOperatorIndex + 1)

            try {
                val number = lastNum.replace(",",".").toDouble()
                val percentValue = number / 100

                var newExpr = expr.replace(lastNum, percentValue.toString().replace(".",","))

                inputValsTV.text = newExpr
                calculateResult()

            } catch (e: Exception) {
                resultTV.text = "Error"
            }

        }
    }

    fun commaAction(view: View) {
        if(!commaIsPlaced){
            if(view is Button) {
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
        calculateResult()
    }

    fun calculateResult(){
        val expression = inputValsTV.text.toString().replace(",",".")
        if(expression == "0"){
            resultTV.text = "0"
            return
        }

        try {
            val tokens = expression.split(Regex("(?<=[÷×+-])|(?=[÷×+-])"))
            val numbers = mutableListOf<Double>()
            val operators = mutableListOf<String>()


            for (token in tokens) {
                if(token in operatorsList){
                    operators.add(token)
                } else {
                    numbers.add(token.toDouble())
                }
            }


            var i = 0
            while (i < operators.size){
                val op = operators[i]
                if(op == "×" || op == "÷"){
                    val num1 = numbers[i]
                    val num2 = numbers[i+1]

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

            val resultParts = finalResult.toString().split(".")
            if(resultParts[1] == "0"){
                resultTV.text = resultParts[0]
            } else {
               resultTV.text = finalResult.toString().replace(".", ",")
            }

        } catch (e: Exception){
            resultTV.text = "Error"
        }
    }
}