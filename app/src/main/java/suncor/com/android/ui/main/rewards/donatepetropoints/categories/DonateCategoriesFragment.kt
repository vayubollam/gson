package suncor.com.android.ui.main.rewards.donatepetropoints.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.gson.Gson
import suncor.com.android.R
import suncor.com.android.databinding.FragmentDonateCategoriesBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.model.redeem.response.MemberEligibilityResponse
import suncor.com.android.model.redeem.response.Program
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.ui.main.rewards.donatepetropoints.ImageMapper
import javax.inject.Inject

class DonateCategoriesFragment : MainActivityFragment(), OnBackPressedListener {


    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var gson: Gson

    private var viewModel: DonateCategoriesViewModel? = null
    private lateinit var binding: FragmentDonateCategoriesBinding
    private lateinit var memberEligibilityResponse: MemberEligibilityResponse
    private var categoryString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory)[DonateCategoriesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonateCategoriesBinding.inflate(inflater, container, false)
        arguments.let {
            if (it != null) {
                categoryString = DonateCategoriesFragmentArgs.fromBundle(
                    it
                ).categoriesList
            }
        }

        categoryString.let {
            memberEligibilityResponse = gson.fromJson(it, MemberEligibilityResponse::class.java)
        }

        ImageMapper.mapImages(memberEligibilityResponse)

        binding.vm = viewModel

        binding.appBar.setNavigationOnClickListener {
            Navigation.findNavController(
                requireView()
            ).popBackStack()
        }

        val adapter = context?.let {
            DonateCategoryAdapter(
                it,
                memberEligibilityResponse.categories,
                this::handleClick
            )
        }

        binding.apply {
            categoriesRecyclerView.adapter = adapter
        }

        return binding.root
    }

    private fun handleClick(program: Program) {
        val programString = gson.toJson(program)
        val action =
            DonateCategoriesFragmentDirections.actionDonateCategoriesToDonatePetroPointsFragment()
        action.program = programString
        val navDestination = Navigation.findNavController(requireView()).currentDestination
        if (navDestination != null && navDestination.id == R.id.donateCategoriesFragment) {
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    override fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }
}