package suncor.com.android.ui.main.rewards.donate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import suncor.com.android.databinding.FragmentDonatePetroPointsBinding
import suncor.com.android.ui.main.common.MainActivityFragment


class DonatePetroPointsFragment : MainActivityFragment() {

    private lateinit var binding : FragmentDonatePetroPointsBinding

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