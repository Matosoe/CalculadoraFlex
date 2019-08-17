package br.com.matosoe.calculadoraflex.watchers

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils.formatNumber
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.matosoe.calculadoraflex.R
import br.com.matosoe.calculadoraflex.ui.result.ResultActivity
import kotlinx.android.synthetic.main.activity_form.*
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.text.DecimalFormat

class DecimalTextWatcher (editText: EditText, val totalDecimalNumber: Int = 2):
        TextWatcher {
    private val editTextWeakReference: WeakReference<EditText> = WeakReference(editText)

    init {
        formatNumber(editTextWeakReference.get()!!.text)

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {
        formatNumber(editable)
    }

    private fun getTotalDecimalNumber(): String {
        val decimalNumber = StringBuilder()
        for (i in 1..totalDecimalNumber) {
            decimalNumber.append("0")
        }
        return decimalNumber.toString()
    }

    private fun formatNumber(editable: Editable) {
        val editText = editTextWeakReference.get() ?: return
        val cleanString = editable.toString().trim().replace(" ", "")
        editText.removeTextChangedListener(this)
        val number = Math.pow(10.toDouble(), totalDecimalNumber.toDouble())
        val parsed = when (cleanString) {
            null -> BigDecimal(0)
            "" -> BigDecimal(0)
            "null" -> BigDecimal(0)
            else -> BigDecimal(cleanString.replace("\\D+".toRegex(), ""))
                .setScale(totalDecimalNumber, BigDecimal.ROUND_FLOOR)
                .divide(
                    BigDecimal(number.toInt()),
                    BigDecimal.ROUND_FLOOR
                )
        }
        val dfnd = DecimalFormat("#,##0.${getTotalDecimalNumber()}")
        val formatted = dfnd.format(parsed)
        editText.setText(formatted.replace(',', '.'))
        editText.setSelection(formatted.length)
        editText.addTextChangedListener(this)
    }

    class FormActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_form)
            etGasPrice.addTextChangedListener(DecimalTextWatcher(etGasPrice))
            etEthanolPrice.addTextChangedListener(DecimalTextWatcher(etEthanolPrice))
            etGasAverage.addTextChangedListener(DecimalTextWatcher(etGasAverage, 1))
            etEthanolAverage.addTextChangedListener(DecimalTextWatcher(etEthanolAverage, 1))
            btCalculate.setOnClickListener {
                val proximatela = Intent(this@FormActivity, ResultActivity::class.java)
                proximatela.putExtra("GAS_PRICE", etGasPrice.text.toString().toDouble())
                proximatela.putExtra("ETHANOL_PRICE", etEthanolPrice.text.toString().toDouble())
                proximatela.putExtra("GAS_AVERAGE", etGasAverage.text.toString().toDouble())
                proximatela.putExtra("ETHANOL_AVERAGE", etEthanolAverage.text.toString().toDouble())
                startActivity(proximatela)
            }
        }
    }
}








