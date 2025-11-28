package samuelnunes.com.anycubicnfcwritter

import android.animation.Animator
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import samuelnunes.com.anycubicnfcwritter.databinding.DialogFragmentBinding
import java.io.IOException

class MyDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentBinding? = null
    private val binding get() = _binding!!
    private var spool: Spool? = null

    private val finishAnimation = object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {}
        override fun onAnimationEnd(p0: Animator) {
            this@MyDialogFragment.dismiss()
        }

        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setCurrentSpool(spool: Spool) {
        this.spool = spool

    }

    private fun nfcError() {
        binding.animation.apply {
            setAnimation(R.raw.error)
            addAnimatorListener(finishAnimation)
            setRepeatCount(1)
            playAnimation()
        }
    }

    private fun nfcSuccess() {
        binding.animation.apply {
            setAnimation(R.raw.success)
            addAnimatorListener(finishAnimation)
            setRepeatCount(1)
            playAnimation()
        }
    }

    fun writeNFC(tag: Tag) {
        spool?.let {
            val hexStuff: String = nfcMapper(it)
            val nfca = NfcA.get(tag)
            try {
                nfca.connect()
                val lines = hexStuff.split(",")
                for (line in lines) {
                    val bytes = line.trim().split(":").map { it.toInt(16).toByte() }.toByteArray()
                    nfca.transceive(bytes) // Sendet die Daten an das Tag
                }
                Log.i("NFCWrite", "Data saved successfully!")
                nfcSuccess()
            } catch (e: IOException) {
                Log.e("NFCWrite", "Error writing to NFC tag", e)
                nfcError()
            } finally {
                nfca.close()
            }
        } ?: run {
            Log.e("NFCWrite", "No spool info")
            nfcError()
        }
    }
}
