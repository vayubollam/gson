package suncor.com.android.ui.main.carwash

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import suncor.com.android.R
import suncor.com.android.databinding.FragmentVacuumBarcodeBinding
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment

class VacuumBarcodeFragment : MainActivityFragment(), OnBackPressedListener {
    private lateinit var binding: FragmentVacuumBarcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVacuumBarcodeBinding.inflate(inflater, container, false)
        binding.buttonClose.setOnClickListener(closeListener)
        binding.appBar.setNavigationOnClickListener { v -> goBack(false) }
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        binding.barcodeNumberText.text="56353653"
        binding.barCodeImage.setImageBitmap(
            generateBarcode(
                size.x,
                56353653.toString(),
                BarcodeFormat.UPC_A
            )
        )
        return binding.root
    }


    private fun generateBarcode(
        screenSize: Int,
        encryptedCarWashCode: String,
        barcodeFormat: BarcodeFormat
    ): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        val r = resources
        val width = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, screenSize.toFloat(), r.displayMetrics
            )
        )
        val height = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                r.getDimension(R.dimen.carwash_barcode_image_height),
                r.displayMetrics
            )
        )
        try {
            val bitMatrix =
                multiFormatWriter.encode(encryptedCarWashCode, barcodeFormat, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (i in 0 until width) {
                for (j in 0 until height) {
                    bitmap.setPixel(i, j, if (bitMatrix[i, j]) Color.BLACK else Color.WHITE)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    private val closeListener =
        View.OnClickListener { view: View? ->
            Navigation.findNavController(
                requireView()
            ).popBackStack(R.id.cardsDetailsFragment, false)
        }

    override fun onBackPressed() {
        goBack(false)
    }

    private fun goBack(reEnter: Boolean) {
        Navigation.findNavController(requireView()).popBackStack()
    }

}