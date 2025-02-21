package samuelnunes.com.anycubicnfcwritter

import android.graphics.Color
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

fun ColorPickerView.editTextMediator(textInputLayout: TextInputLayout, callback: (ColorEnvelope) -> Unit) {
    val pickerListener =
        ColorEnvelopeListener { envelope, fromUser ->
            try {
                if (fromUser) {
                    val hexColor = "#${envelope.hexCode}"
                    textInputLayout.editText?.setText(hexColor)
                }
                callback(envelope)
            } catch (ex: Exception) {
                Log.e("editTextMediator", "ColorEnvelopeListener", ex)
            }
        }

    textInputLayout.editText?.doAfterTextChanged {
        try {
            val hexColor = it.toString()
            if(isHexCodeValid(hexColor)) {
                setInitialColor(Color.parseColor(it.toString()))
                textInputLayout.error = null
            } else {
                textInputLayout.error = "Formato inválido! Ex: #A1B2C3D4"
            }
        } catch (ex: Exception) {
            Log.e("editTextMediator", "doAfterTextChanged", ex)
        }
    }
    setColorListener(pickerListener)
}

fun isHexCodeValid(input: String): Boolean = Regex("^#([A-F0-9]{8})$").matches(input)
