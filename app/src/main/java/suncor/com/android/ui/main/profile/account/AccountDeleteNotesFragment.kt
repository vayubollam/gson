package suncor.com.android.ui.main.profile.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import suncor.com.android.databinding.FragmentAccountDeleteNotesBinding
import suncor.com.android.databinding.FragmentAccountDeleteSubmitBinding
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.ui.main.profile.ProfileSharedViewModel

class AccountDeleteNotesFragment : MainActivityFragment(), OnBackPressedListener {

    private lateinit var binding: FragmentAccountDeleteNotesBinding
    private lateinit var profileSharedViewModel: ProfileSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileSharedViewModel =
                ViewModelProviders.of(requireActivity())[ProfileSharedViewModel::class.java]
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDeleteNotesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     // handle text view and clicks

    }
    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        profileSharedViewModel.ecryptedSecurityAnswer = null
        Navigation.findNavController(requireView()).popBackStack()
    }


}