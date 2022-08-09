package suncor.com.android.ui.main.rewards.donate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import suncor.com.android.databinding.FragmentDonatePetroPointsBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.extensions.getSoftInputMode
import suncor.com.android.ui.main.common.MainActivityFragment
import javax.inject.Inject


class DonatePetroPointsFragment : MainActivityFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: DonatePetroPointsViewModel
    private lateinit var binding: FragmentDonatePetroPointsBinding
    private var originalMode : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,viewModelFactory).get(DonatePetroPointsViewModel::class.java)
        originalMode = activity?.window?.getSoftInputMode()
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
    }

    // TODO: 1. Study DataBinding (Update views from viewmodel)
    //  2. make a custom layout for edittext with $ sign
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonatePetroPointsBinding.inflate(inflater, container, false)

        binding.closeButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        binding.imageButtonIncrement.setOnClickListener{
            viewModel.incrementAmount()
            binding.executePendingBindings()
        }

        binding.imageButtonDecrement.setOnClickListener {
            viewModel.decrementAmount()
        }

        binding.vm = viewModel

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        originalMode?.let { activity?.window?.setSoftInputMode(it) }
    }
}