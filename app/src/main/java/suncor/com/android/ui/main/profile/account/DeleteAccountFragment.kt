package suncor.com.android.ui.main.profile.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import suncor.com.android.databinding.FragmentAccountDetailsBinding
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment

class DeleteAccountFragment : MainActivityFragment(), OnBackPressedListener {

    private lateinit var binding: FragmentAccountDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }
}