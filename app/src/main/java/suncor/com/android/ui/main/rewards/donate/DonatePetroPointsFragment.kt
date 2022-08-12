package suncor.com.android.ui.main.rewards.donate

import android.R
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.crashlytics.internal.common.CommonUtils.hideKeyboard
import suncor.com.android.databinding.FragmentDonatePetroPointsBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.extensions.getSoftInputMode
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.Timber
import java.util.*
import javax.inject.Inject


class DonatePetroPointsFragment : MainActivityFragment(), OnKeyboardVisibilityListener {

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

    // TODO: 1. Set Max digits logic
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
            }
        })

        binding.inputField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                hideKeyboard(context, v)
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })

        binding.vm = viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.setWindowInsetsAnimationCallback(object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsets,
                    runningAnimations: MutableList<WindowInsetsAnimation>
                ): WindowInsets {
                    return insets
                }
                override fun onEnd(animation: WindowInsetsAnimation) {
                    super.onEnd(animation)
                    val showingKeyboard = view.rootWindowInsets.isVisible(WindowInsets.Type.ime())
                    onVisibilityChanged(showingKeyboard)
                }
            })
        } else {
            setKeyboardVisibilityListener(this);
        }
    }


    override fun onVisibilityChanged(visible: Boolean) {
        if(!visible){
            viewModel.enableDonation.set(true)
            viewModel.rectifyValuesOnKeyboardGone()
            binding.inputField.setText(viewModel.formattedDonationAmount.get())
            binding.inputField.setSelection(
                viewModel.formattedDonationAmount.get()?.length ?: 0
            )
        }else{
            viewModel.enableDonation.set(false)
        }

    }

    private fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (binding.root.findViewById(R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            private var alreadyOpen = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
            private val rect: Rect = Rect()
            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                ).toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff: Int = parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                if (isShown == alreadyOpen) {
                    Timber.i("Keyboard state", "Ignoring global layout change...")
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onVisibilityChanged(isShown)
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        originalMode?.let { activity?.window?.setSoftInputMode(it) }
    }
}

interface OnKeyboardVisibilityListener {
    fun onVisibilityChanged(visible: Boolean)
}