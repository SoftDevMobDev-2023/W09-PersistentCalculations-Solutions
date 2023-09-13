package au.edu.swin.sdmd.w03_calculations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    var opResult: Int = 0
    var operator = "plus"
    private val viewModel: NumbersViewModel by viewModels()
    private val scope = CoroutineScope(Job() + Dispatchers.Main)


    override fun onStart() {
        super.onStart()
        Log.i("LIFECYCLE", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i("LIFECYCLE", "onResume")
    }

    override fun onPause() {
        super.onPause()

        val store = KeyStore(baseContext)
        scope.launch {
            store.saveNumbers(viewModel.num1.value, viewModel.num2.value)
        }

        Log.i("LIFECYCLE", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i("LIFECYCLE", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("LIFECYCLE", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("LIFECYCLE", "onRestart")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val number1 = findViewById<EditText>(R.id.number1)
        val number2 = findViewById<EditText>(R.id.number2)
        val answer = findViewById<TextView>(R.id.answer)

        val store = KeyStore(baseContext)
        scope.launch {
            store.getLastNumber1.collect { number ->
                viewModel.num1.postValue(number)
            }
        }
        scope.launch {
            store.getLastNumber2.collect { number ->
                viewModel.num2.postValue(number)
            }
        }

        val n1Observer = Observer<Int> { state ->
            number1.setText(state.toString())
        }
        viewModel.num1.observe(this, n1Observer)

        val n2Observer = Observer<Int> { state ->
            number2.setText(state.toString())
        }
        viewModel.num2.observe(this, n2Observer)


        savedInstanceState?.let {
            opResult = savedInstanceState.getInt("ANSWER")
            answer.text = opResult.toString()
        }

        val equals = findViewById<Button>(R.id.equals)
        equals.setOnClickListener {
            opResult = when(operator) {
                "plus" -> add(number1.text.toString(), number2.text.toString())
                "mult" -> mult(number1.text.toString(), number2.text.toString())
                else -> add(number1.text.toString(), number2.text.toString())
            }
            viewModel.num1.value = number1.text.toString().toInt()
            viewModel.num2.value = number2.text.toString().toInt()

            // TODO: show result on the screen
            answer.text = opResult.toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("ANSWER", opResult)
        Log.i("LIFECYCLE", "saveInstanceState $opResult")
    }

    // adds two numbers together
    private fun add(number1: String, number2: String) = number1.toInt() + number2.toInt()
    private fun mult(number1: String, number2: String) = number1.toInt() * number2.toInt()

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioPlus ->
                    if (checked) {
                        operator = "plus"
                    }
                R.id.radioMult ->
                    if (checked) {
                        operator = "mult"
                    }
            }
        }
    }
}

class NumbersViewModel: ViewModel() {
    val num1: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    val num2: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

}