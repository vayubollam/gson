package suncor.com.android.ui.main.rewards.donatepetropoints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.gson.Gson
import suncor.com.android.databinding.FragmentDonatePetroPointsBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.model.redeem.response.MemberEligibilityResponse
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment
import javax.inject.Inject

class DonatePetroPointsFragment: MainActivityFragment(), OnBackPressedListener {


    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var gson: Gson

    private var viewModel: DonatePetroPointsViewModel? = null
    private lateinit var  binding: FragmentDonatePetroPointsBinding
    private lateinit var memberEligibilityResponse: MemberEligibilityResponse
    private var categoryString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         viewModel = ViewModelProviders.of(this, factory)[DonatePetroPointsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonatePetroPointsBinding.inflate(inflater, container, false)
        arguments.let {

            if(it != null){
                categoryString = DonatePetroPointsFragmentArgs.fromBundle(it).categoriesList
            }
        }

        categoryString.let {
            memberEligibilityResponse = gson.fromJson(it, MemberEligibilityResponse::class.java)
        }


        binding.vm = viewModel

        binding.appBar.setNavigationOnClickListener { v ->
            Navigation.findNavController(
                requireView()
            ).popBackStack()
        }

        return binding.root

    }

    override fun onResume() {
        super.onResume()

        val adapter = context?.let { DonateCategoryAdapter(it, memberEligibilityResponse.categories) }
        binding.categoriesRecyclerView.adapter = adapter
    }

    override fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }
}