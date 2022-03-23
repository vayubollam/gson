package suncor.com.android.ui.main.vacuum

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
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
import suncor.com.android.ui.main.carwash.CarWashBarCodeFragmentArgs
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.Timber

class VacuumBarcodeFragment : MainActivityFragment(), OnBackPressedListener {

    private lateinit var binding: FragmentVacuumBarcodeBinding
    private lateinit var cardNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVacuumBarcodeBinding.inflate(inflater, container, false)
        binding.buttonClose.setOnClickListener(closeListener)
        cardNumber = VacuumBarcodeFragmentArgs.fromBundle(requireArguments()).cardNumber

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val updateHandler = Handler()
        val runnable = Runnable {
            showBarcode() // some action(s)
        }
        updateHandler.postDelayed(runnable, 500)
    }

    private fun showBarcode(){
        val display:Display
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
             display = activity?.display!!
        } else {
            @Suppress("DEPRECATION")
             display = activity?.windowManager?.defaultDisplay!!
        }
        val size = Point()
        display?.getRealSize(size)
        binding.barcodeNumberText.text = cardNumber
        var barcodeValue = cardNumber.replace(" ", "").substring(6, 17);
        binding.barCodeImage.setImageBitmap(generateBarcode( size.x,  barcodeValue, BarcodeFormat.UPC_A))
    }


    private fun generateBarcode(screenSize: Int, encryptedCarWashCode: String, barcodeFormat: BarcodeFormat
    ): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        val r = resources
        val width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenSize.toFloat(), r.displayMetrics ))
        val height = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                r.getDimension(R.dimen.carwash_barcode_image_height),
                r.displayMetrics ))
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
            Timber.e("Error on generating barcode, Error ", e.message)
        }
        return null
    }

    private val closeListener =
        View.OnClickListener { view: View? ->
            Navigation.findNavController(requireView()).popBackStack()
        }

    override fun onBackPressed() {
        goBack(false)
    }

    private fun goBack(reEnter: Boolean) {
        Navigation.findNavController(requireView()).popBackStack()
    }

}