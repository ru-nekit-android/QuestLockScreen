package ru.nekit.android.qls.setupWizard.view

import android.graphics.Bitmap
import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import ru.nekit.android.qls.R
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETTINGS
import java.util.*

class BindParentFragment : QuestSetupWizardFragment() {

    override val addToBackStack: Boolean = true

    private lateinit var qrCodeView: ImageView
    private lateinit var qrCodeInput: String
    private var qrCodeOutput: Bitmap? = null
    private var qrCodeHandler: Handler? = null
    private lateinit var generateQrCodeView: TextView
    private var generationQrCodeRunnable: Runnable? = Runnable {
        try {
            val hints = HashMap<EncodeHintType, Any>()
            hints.put(EncodeHintType.CHARACTER_SET, "windows-1251")
            qrCodeOutput = encodeAsBitmap(qrCodeInput, QR_SIZE, hints)
            qrCodeView.setImageBitmap(qrCodeOutput)
            generateQrCodeView.visibility = View.GONE
        } catch (exp: WriterException) {
            exp.printStackTrace()
        }
    }

    private fun generateQrCode() {
        if (qrCodeHandler == null) {
            qrCodeHandler = Handler()
            qrCodeHandler!!.post(generationQrCodeRunnable)
        }
    }

    override fun onDestroy() {
        if (qrCodeHandler != null) {
            qrCodeHandler!!.removeCallbacks(generationQrCodeRunnable)
            qrCodeHandler = null
            qrCodeOutput!!.recycle()
        }
        super.onDestroy()
    }

    override fun onSetupStart(view: View) {
        qrCodeView = view.findViewById(R.id.image_qr_code)
        generateQrCodeView = view.findViewById(R.id.tv_generate_qr_code)

        autoDispose {
            setupWizard.createBindCode().subscribe { it ->
                qrCodeInput = it
            }
        }

        val qrSize = context!!.resources.getDimensionPixelSize(R.dimen.qr_code_size)
        val qrLayoutParams = qrCodeView.layoutParams as RelativeLayout.LayoutParams
        qrLayoutParams.width = qrSize
        qrLayoutParams.height = qrSize
        qrCodeView.requestLayout()
        generateQrCode()
        setNextButtonVisibility(false)
        setAltButtonText(R.string.cancel)
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(value: String, size: Int, hints: Map<EncodeHintType, *>?): Bitmap? {
        val result: BitMatrix
        val bitmap: Bitmap
        try {
            result = MultiFormatWriter().encode(value,
                    BarcodeFormat.QR_CODE, size, size, hints)
            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] = ContextCompat.getColor(context!!, if (result.get(x, y)) R.color.green else R.color.white)
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, QR_SIZE, 0, 0, w, h)
        } catch (iae: Exception) {
            iae.printStackTrace()
            return null
        }

        return bitmap
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_bind_parent
    }

    override fun altAction() {
        showSetupWizardStep(SETTINGS)
    }

    companion object {

        val QR_SIZE = 200

        val instance: BindParentFragment
            get() = BindParentFragment()
    }
}