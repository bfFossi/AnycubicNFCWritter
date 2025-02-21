package samuelnunes.com.anycubicnfcwritter

import android.R
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.skydoves.colorpickerview.flag.BubbleFlag
import samuelnunes.com.anycubicnfcwritter.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private var nfcAdapter: NfcAdapter? = null
    private lateinit var binding: ActivityMainBinding
    private var argb: ARGB? = null
    private var dialog: MyDialogFragment? = null
    private lateinit var spool: Spool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC não disponível", Toast.LENGTH_LONG).show()
            finish()
        }
        onSetupFields()
    }

    private fun onSetupFields() {
        binding.apply {
            hexColor.editTextMediator(tvColor) { envelope ->
                alphaTileView.setPaintColor(envelope.color)
                argb = ARGB(envelope.argb)
                changeStateFAB()
            }
            hexColor.attachAlphaSlider(alphaSlideBar)
            hexColor.attachBrightnessSlider(brightnessSlideBar)
            hexColor.flagView = BubbleFlag(this@MainActivity)
            val formatter: (value: Float) -> String = { value: Float -> "${value.toInt()} mm/s" }

            slideSpeedMin.setLabelFormatter(formatter)
            slideSpeedMed.setLabelFormatter(formatter)
            slideSpeedMax.setLabelFormatter(formatter)

            slideSpeedMin.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {}
                override fun onStopTrackingTouch(slider: RangeSlider) {
                    val values = slider.values
                    if(slideSpeedMed.values[0] < values[1]) {
                        slideSpeedMed.values = listOf(values[1], slideSpeedMed.values[1])
                    }
                    slideSpeedMed.valueFrom = values[1]
                }
            })

            slideSpeedMax.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {}
                override fun onStopTrackingTouch(slider: RangeSlider) {
                    val values = slider.values
                    if(slideSpeedMed.values[1] > values[0]) {
                        slideSpeedMed.values = listOf(slideSpeedMed.values[0], values[0])
                    }
                    slideSpeedMed.valueTo = values[0]
                }
            })

            slideSpeedMin.addOnChangeListener(updateSpeedLabel(tvSpeedMin))
            slideSpeedMed.addOnChangeListener(updateSpeedLabel(tvSpeedMed))
            slideSpeedMax.addOnChangeListener(updateSpeedLabel(tvSpeedMax))

            slideTempMin.addOnChangeListener(updateTemperatureLabel(tvTempMin))
            slideTempMed.addOnChangeListener(updateTemperatureLabel(tvTempMed))
            slideTempMax.addOnChangeListener(updateTemperatureLabel(tvTempMax))

            slideBedTemp.addOnChangeListener(updateBedTemperatureLabel(tvBedTemp))

            val materialNames = listOf("ABS", "ASA", "HIPS", "PETG", "PLA", "PLA+", "TPU")
            val adapter = ArrayAdapter(this@MainActivity, R.layout.simple_dropdown_item_1line, materialNames)
            tvMaterialName.setAdapter(adapter)
            tvMaterialName.doAfterTextChanged {
                changeStateFAB()
            }
            fab.setOnClickListener {
                dialog = MyDialogFragment().apply {
                    setCurrentSpool(spool)
                    show(supportFragmentManager, "Diag")
                }
            }
        }
    }

    private fun updateSpeedLabel(tvLabel: TextView): RangeSlider.OnChangeListener =
        RangeSlider.OnChangeListener { slider, _, _ ->
            val values = slider.values
            val text = "${values[0].toInt()} mm/s - ${values[1].toInt()} mm/s"
            tvLabel.text = text
        }

    private fun updateTemperatureLabel(tvLabel: TextView): RangeSlider.OnChangeListener =
        RangeSlider.OnChangeListener { slider, _, _ ->
            val values = slider.values
            val text = "${values[0].toInt()} °C - ${values[1].toInt()} °C"
            tvLabel.text = text
        }

    private fun updateBedTemperatureLabel(tvLabel: TextView): RangeSlider.OnChangeListener =
        RangeSlider.OnChangeListener { slider, _, _ ->
            val values = slider.values
            val text = "Bed Temperature: ${values[0].toInt()} °C - ${values[1].toInt()} °C"
            tvLabel.text = text
        }


    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
        val techLists = arrayOf(arrayOf(NfcA::class.java.name))

        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, techLists)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if(binding.fab.isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }?.let { dialog?.writeNFC(it) }
        } else {
            Toast.makeText(this, "Preencha os campos de cadastro primeiro", Toast.LENGTH_LONG).show()
        }
    }

    private fun changeStateFAB() {
        val material = binding.tvMaterialName.text.toString()
        binding.fab.isEnabled = argb != null && material.isNotBlank()
        argb?.let {
            spool = Spool(
                hexColor = it,
                material = material,
                firstIntervalHotendTemp = HotEndTemperature(
                    speedMin = binding.slideSpeedMin.values[0],
                    speedMax = binding.slideSpeedMin.values[1],
                    temperatureMin = binding.slideTempMin.values[0],
                    temperatureMax = binding.slideTempMin.values[1]
                ),
                secondIntervalHotendTemp = HotEndTemperature(
                    speedMin = binding.slideSpeedMed.values[0],
                    speedMax = binding.slideSpeedMed.values[1],
                    temperatureMin = binding.slideTempMed.values[0],
                    temperatureMax = binding.slideTempMed.values[1]
                ),
                thirdIntervalHotendTemp = HotEndTemperature(
                    speedMin = binding.slideSpeedMax.values[0],
                    speedMax = binding.slideSpeedMax.values[1],
                    temperatureMin = binding.slideTempMax.values[0],
                    temperatureMax = binding.slideTempMax.values[1]
                ),
                bedTemp = BedTemperature(
                    temperatureMin = binding.slideBedTemp.values[0],
                    temperatureMax = binding.slideBedTemp.values[1]
                )
            )
        }
    }
}