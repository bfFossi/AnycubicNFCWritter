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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import samuelnunes.com.anycubicnfcwritter.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private var nfcAdapter: NfcAdapter? = null
    private lateinit var binding: ActivityMainBinding
    private var hexColor: ARGB? = null
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
        binding.hexColor.setColorListener(ColorEnvelopeListener { envelope, fromUser ->
            binding.alphaTileView.setPaintColor(envelope.color)
            hexColor = ARGB(envelope.argb)
            changeStateFAB()
        })
        binding.hexColor.attachAlphaSlider(binding.alphaSlideBar)

        val materialNames = listOf("ABS", "ASA", "HIPS", "PETG", "PLA", "PLA+", "TPU")
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, materialNames)
        binding.tvMaterialName.setAdapter(adapter)
        binding.tvMaterialName.doAfterTextChanged {
            changeStateFAB()
        }
        binding.fab.setOnClickListener {
            dialog = MyDialogFragment().apply {
                setCurrentSpool(spool)
                show(supportFragmentManager, "Diag")
            }
        }
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
        binding.fab.isEnabled = hexColor != null && material.isNotBlank()
        hexColor?.let {
            spool = Spool(
                hexColor = it,
                material = material
            )
        }
    }
}