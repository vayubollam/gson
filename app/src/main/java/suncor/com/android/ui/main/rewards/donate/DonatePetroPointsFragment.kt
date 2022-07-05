package suncor.com.android.ui.main.rewards.donate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import suncor.com.android.databinding.FragmentDonatePetroPointsBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.ui.main.common.MainActivityFragment
import javax.inject.Inject


class DonatePetroPointsFragment : MainActivityFragment() {

//    @Inject
//    var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: DonatePetroPointsViewModel
    private lateinit var binding: FragmentDonatePetroPointsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = ViewModelProvider(this,viewModelFactory).get(DonatePetroPointsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonatePetroPointsBinding.inflate(inflater, container, false)
        binding.closeButton.setOnClickListener { v ->
            Navigation.findNavController(requireView()).navigateUp()
        }

        return binding.root
    }

}