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
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.slider.RangeSlider
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
            Toast.makeText(this, "NFC not available", Toast.LENGTH_LONG).show()
            finish()
        }

        onSetupFields()

    }
    override fun onStop() {
        super.onStop()

        changeStateFAB()

        val mySPR = getSharedPreferences("AnycubicNFC",0)
        val editor = mySPR.edit()

        editor.putFloat("1_HotMinSpeed",spool.firstIntervalHotendTemp.speedMin)
        editor.putFloat("1_HotMaxSpeed",spool.firstIntervalHotendTemp.speedMax)
        editor.putFloat("2_HotMinSpeed",spool.secondIntervalHotendTemp.speedMin)
        editor.putFloat("2_HotMaxSpeed",spool.secondIntervalHotendTemp.speedMax)
        editor.putFloat("3_HotMinSpeed",spool.thirdIntervalHotendTemp.speedMin)
        editor.putFloat("3_HotMaxSpeed",spool.thirdIntervalHotendTemp.speedMax)

        editor.putFloat("1_HotMinTemp",spool.firstIntervalHotendTemp.temperatureMin)
        editor.putFloat("1_HotMaxTemp",spool.firstIntervalHotendTemp.temperatureMax)
        editor.putFloat("2_HotMinTemp",spool.secondIntervalHotendTemp.temperatureMin)
        editor.putFloat("2_HotMaxTemp",spool.secondIntervalHotendTemp.temperatureMax)
        editor.putFloat("3_HotMinTemp",spool.thirdIntervalHotendTemp.temperatureMin)
        editor.putFloat("3_HotMaxTemp",spool.thirdIntervalHotendTemp.temperatureMax)

        editor.putBoolean("switchSpeedMed",spool.secondIntervalHotendTemp.enabled)
        editor.putBoolean("switchSpeedMax",spool.thirdIntervalHotendTemp.enabled)

        editor.putString("Material",spool.material)

        editor.putFloat("BedMinTemp",spool.bedTemp.temperatureMin)
        editor.putFloat("BedMaxTemp",spool.bedTemp.temperatureMax)

        //val aInt = binding.hexColor.color
        editor.putString("a_Color","%02x".format(spool.hexColor.a).uppercase())
        editor.putString("g_Color","%02x".format(spool.hexColor.g).uppercase())
        editor.putString("b_Color","%02x".format(spool.hexColor.b).uppercase())
        editor.putString("r_Color","%02x".format(spool.hexColor.r).uppercase())

        editor.apply()

    }

    override fun onStart() {
        super.onStart()
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

            slideSpeedMed.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {}
                override fun onStopTrackingTouch(slider: RangeSlider) {
                    val values = slider.values
                    if(slideSpeedMax.values[0] < values[1]) {
                        slideSpeedMax.values = listOf(values[1], slideSpeedMax.values[1])
                    }
                    slideSpeedMax.valueFrom = values[1]
                    slideSpeedMin.valueTo = values[0]
                }
            })

            //val mySlider = findViewById<RangeSlider>(R.id.slide_speed_med)

//            slideSpeedMed.values = listOf(123f, 456f)

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

            switchSpeedMed.setOnCheckedChangeListener { buttonView, isChecked ->
                slideSpeedMed.isVisible = isChecked
                tvSpeedMed.isVisible = isChecked
                slideTempMed.isVisible = isChecked
                tvTempMed.isVisible = isChecked
                changeStateFAB()
            }

            switchSpeedMax.setOnCheckedChangeListener { buttonView, isChecked ->
                slideSpeedMax.isVisible = isChecked
                tvSpeedMax.isVisible = isChecked
                slideTempMax.isVisible = isChecked
                tvTempMax.isVisible = isChecked
                changeStateFAB()
            }

            switchSpeedMed.isChecked = true
            switchSpeedMax.isChecked = true

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

            // gespeicherte Werte wieder herstellen
            val mySPR = getSharedPreferences("AnycubicNFC",0)

            var sl_min = mySPR.getFloat("1_HotMinSpeed",slideSpeedMin.valueFrom)
            var sl_max = mySPR.getFloat("1_HotMaxSpeed", slideSpeedMin.valueTo)

            slideSpeedMin.valueTo = sl_max
            slideSpeedMed.valueFrom = sl_max
            slideSpeedMin.setValues(sl_min,sl_max)

            sl_min = mySPR.getFloat("2_HotMinSpeed",slideSpeedMed.valueFrom)
            sl_max = mySPR.getFloat("2_HotMaxSpeed", slideSpeedMed.valueTo)

            slideSpeedMin.valueTo = sl_min // Bereich vergrößern
            slideSpeedMed.valueTo = sl_max
            slideSpeedMax.valueFrom = sl_max
            slideSpeedMed.setValues(sl_min,sl_max)

            sl_min = mySPR.getFloat("3_HotMinSpeed",slideSpeedMax.valueFrom)
            sl_max = mySPR.getFloat("3_HotMaxSpeed", slideSpeedMax.valueTo)

            slideSpeedMed.valueTo = sl_min
            slideSpeedMax.setValues(sl_min,sl_max)


            slideTempMin.setValues(mySPR.getFloat("1_HotMinTemp",slideTempMin.valueFrom),
                                   mySPR.getFloat("1_HotMaxTemp", slideTempMin.valueTo))
            slideTempMed.setValues(mySPR.getFloat("2_HotMinTemp",slideTempMed.valueFrom),
                                   mySPR.getFloat("2_HotMaxTemp", slideTempMed.valueTo))
            slideTempMax.setValues(mySPR.getFloat("3_HotMinTemp",slideTempMax.valueFrom),
                                   mySPR.getFloat("3_HotMaxTemp", slideTempMax.valueTo))

            slideBedTemp.setValues(mySPR.getFloat("BedMinTemp",slideBedTemp.valueFrom),mySPR.getFloat("BedMaxTemp",slideBedTemp.valueTo))

            tvMaterialName.setText(mySPR.getString("Material",""))

            switchSpeedMed.isChecked = mySPR.getBoolean("switchSpeedMed",true)
            switchSpeedMax.isChecked = mySPR.getBoolean("switchSpeedMax",true)

            val a = mySPR.getString("a_Color","00")
            val g = mySPR.getString("g_Color","00")
            val r = mySPR.getString("r_Color","00")
            val b = mySPR.getString("b_Color","00")

            etColor.setText("#"+a+r+g+b)

            // Nur so aktualisieren sich die Slider korrekt :-(
            slideSpeedMin.setValues(slideSpeedMin.values[0]+1,slideSpeedMin.values[1])
            slideSpeedMin.setValues(slideSpeedMin.values[0]-1,slideSpeedMin.values[1])
            slideSpeedMed.setValues(slideSpeedMed.values[0]+1,slideSpeedMed.values[1])
            slideSpeedMed.setValues(slideSpeedMed.values[0]-1,slideSpeedMed.values[1])
            slideSpeedMax.setValues(slideSpeedMax.values[0]+1,slideSpeedMax.values[1])
            slideSpeedMax.setValues(slideSpeedMax.values[0]-1,slideSpeedMax.values[1])

            changeStateFAB()

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
            Toast.makeText(this, "Fill in the registration fields first", Toast.LENGTH_LONG).show()
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
                    enabled = true,
                    speedMin = binding.slideSpeedMin.values[0],
                    speedMax = binding.slideSpeedMin.values[1],
                    temperatureMin = binding.slideTempMin.values[0],
                    temperatureMax = binding.slideTempMin.values[1]
                ),
                secondIntervalHotendTemp = HotEndTemperature(
                    enabled = binding.switchSpeedMed.isChecked,
                    speedMin = binding.slideSpeedMed.values[0],
                    speedMax = binding.slideSpeedMed.values[1],
                    temperatureMin = binding.slideTempMed.values[0],
                    temperatureMax = binding.slideTempMed.values[1]
                ),
                thirdIntervalHotendTemp = HotEndTemperature(
                    enabled =  binding.switchSpeedMax.isChecked,
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