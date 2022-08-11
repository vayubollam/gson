package suncor.com.android.ui.main.rewards.donate

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import suncor.com.android.databinding.FragmentDonatePetroPointsBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.extensions.getSoftInputMode
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.Timber
import java.util.*
import javax.inject.Inject


class DonatePetroPointsFragment : MainActivityFragment() {

    private var isFrench: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: DonatePetroPointsViewModel
    private lateinit var binding: FragmentDonatePetroPointsBinding
    private var originalMode: Int? = null
    private var current: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DonatePetroPointsViewModel::class.java)
        originalMode = activity?.window?.getSoftInputMode()
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
        isFrench = Locale.getDefault() == Locale.CANADA_FRENCH
    }

    // TODO: 1. Try exoression for edittext text and then try a custom layout as jugaad
    //  2. make a custom layout for edittext with $ sign
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonatePetroPointsBinding.inflate(inflater, container, false)

        binding.closeButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        binding.imageButtonIncrement.setOnClickListener {
            viewModel.incrementAmount()
            binding.executePendingBindings()
        }

        binding.imageButtonDecrement.setOnClickListener {
            viewModel.decrementAmount()
        }

        binding.inputField.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(arg: Editable) {
                try {
                    if (arg.isEmpty()) {
                        viewModel.donateAmount.set(0)
                    } else {
                        viewModel.donateAmount.set(Integer.valueOf(arg.toString()))
                    }
                    viewModel.updateData(true)
                } catch (e: Exception) {
                    Timber.d("Wrong Input")
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.toString() != current) {
//                    binding.inputField.removeTextChangedListener(this)
//                    val cleanString: String = s.replace("""[$,.]""".toRegex(), "")
//
//                    var amount = 0
//                    if (s.isNotEmpty()) amount = cleanString.trim().toInt()
//
//                    viewModel.donateAmount.set(amount)
//                    viewModel.updateData()
//                    val formatted = viewModel.formatter.format((amount))
//                    current = formatted
//                    if (isFrench)
//                        binding.inputField.setSelection(formatted.length-1)
//                    else
//                        binding.inputField.setSelection(formatted.length)
//
//                    binding.inputField.addTextChangedListener(this)
                }
            }
        })
        binding.vm = viewModel

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        originalMode?.let { activity?.window?.setSoftInputMode(it) }
    }
}