package samuelnunes.com.anycubicnfcwritter

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import samuelnunes.com.anycubicnfcwritter.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {


    private var nfcAdapter: NfcAdapter? = null
    private lateinit var textView: TextView

    private lateinit var binding: ActivityMainBinding

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
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        tag?.let { writeToTag(it) }
    }

    private fun writeToTag(tag: Tag) {
        val hexStuff: String = nfcMapper(binding.tvHex.text.toString(), binding.tvMaterialName.text.toString())
        val nfca = NfcA.get(tag)
        try {
            nfca.connect()
            val lines = hexStuff.split(",")
            for (line in lines) {
                val bytes = line.trim().split(":").map { it.toInt(16).toByte() }.toByteArray()
                nfca.transceive(bytes) // Envia os dados para a tag
            }
            Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Error writing to NFC tag", Toast.LENGTH_SHORT).show()
        } finally {
            nfca.close()
        }
    }
}