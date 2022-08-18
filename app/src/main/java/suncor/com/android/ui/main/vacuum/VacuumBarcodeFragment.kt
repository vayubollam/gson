package suncor.com.android.ui.main.vacuum

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Pair
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
import suncor.com.android.model.cards.CardType
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.AnalyticsUtils
import suncor.com.android.utilities.Constants
import suncor.com.android.utilities.Timber
import java.util.concurrent.TimeUnit


class VacuumBarcodeFragment : MainActivityFragment(), OnBackPressedListener {

    private lateinit var binding: FragmentVacuumBarcodeBinding
    private lateinit var cardNumber: String
    private var clickedCardIndex: Int = 0
    var elapsedMillis: Long = 0
    var startTime:Long=0
    var endTime:Long=0
    private lateinit var cardType: String
    private lateinit var analyticsCardType: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVacuumBarcodeBinding.inflate(inflater, container, false)
        binding.buttonClose.setOnClickListener(closeListener)
        cardNumber = VacuumBarcodeFragmentArgs.fromBundle(requireArguments()).cardNumber
        clickedCardIndex = VacuumBarcodeFragmentArgs.fromBundle(requireArguments()).cardIndex
        cardType = VacuumBarcodeFragmentArgs.fromBundle(requireArguments()).cardType

        when (cardType) {
            CardType.WAG.name -> {
                analyticsCardType = Constants.CARDTYPE_WAG
            }
            CardType.SP.name -> {
                analyticsCardType = Constants.CARDTYPE_SP
            }
        }

        Timber.d("vacuumindex : " + clickedCardIndex + cardType)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val updateHandler = Handler()
        val runnable = Runnable {
            showBarcode()
        }
        updateHandler.postDelayed(runnable, 500)
        startTime=System.currentTimeMillis()
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


    private fun generateBarcode(screenSize: Int, encryptedCarWashCode: String, barcodeFormat: BarcodeFormat): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        val r = resources
        val width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenSize.toFloat(), r.displayMetrics ))
        val height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.carwash_barcode_image_height), r.displayMetrics ))
        try {
            val bitMatrix = multiFormatWriter.encode(encryptedCarWashCode, barcodeFormat, width, height)
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
            endTime=System.currentTimeMillis()
            elapsedMillis = endTime-startTime;
            var seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)
            AnalyticsUtils.logEvent(
                context, AnalyticsUtils.Event.ACTIVATEVACUUMSUCCESS,
                Pair(
                    AnalyticsUtils.Param.carWashCardType,
                    analyticsCardType
                ),
                Pair(
                    AnalyticsUtils.Param.SCREENACTIVETIME,
                    seconds.toString()+"s"
                )
            )
            Timber.d("screen-stop-seconds:${seconds}s")
            Navigation.findNavController(requireView()).popBackStack()
        }

    override fun onBackPressed() {
        goBack(false)
    }

    private fun goBack(reEnter: Boolean) {
        Navigation.findNavController(requireView()).previousBackStackEntry
            ?.savedStateHandle?.set<Int>(Constants.CLICKED_CARD_INDEX, clickedCardIndex)
        Navigation.findNavController(requireView()).popBackStack()
    }
}