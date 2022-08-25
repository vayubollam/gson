package suncor.com.android.ui.main.rewards.donate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.account.Profile
import suncor.com.android.ui.main.rewards.donatepetropoints.donate.DonatePetroPointsViewModel

@RunWith(MockitoJUnitRunner::class)
class DonatePetroPointsViewModelTest {
    /*
    * What is covered?
    * Increment and decrement operations
    * Cases: 1. Normal
    *        2. Incrementing reaching maxLimit
    *        3. Decrement reaching minLimit
    *        4. Increment disabled on reaching maxLimit
    *        5. Decrement and donation disabled on reaching minLimit
    *        6. donation and decrement are disabled when donateAmount 0 but gets enabled on increment. 6.1
    *
    * */

    lateinit var viewModel: DonatePetroPointsViewModel
    var profile: Profile = Profile()

    @Mock
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        profile.petroPointsNumber = "706934700054132011"
        profile.pointsBalance = 2500
        Mockito.`when`(sessionManager.profile).thenReturn(profile)

        viewModel = DonatePetroPointsViewModel(sessionManager)
    }

    // Initial state of screen too
    @Test
    fun donation_decrement_disabled_amount_0_enabled_on_increment() {
        viewModel.donateAmount.set(0)
        assertEquals(false, viewModel.enableDonation.get())
        assertEquals(false, viewModel.enableDeduction.get())

        viewModel.incrementAmount()

        assertEquals(true, viewModel.enableDonation.get())
        assertEquals(true, viewModel.enableDeduction.get())
    }

    // Initial state of screen if maxLimit is 0
    @Test
    fun donation_decrement_disabled_amount_0_disabled_on_increment_if_maxLimit_0() {
        viewModel.donateAmount.set(0)
        assertEquals(false, viewModel.enableDonation.get())
        assertEquals(false, viewModel.enableDeduction.get())

        profile.pointsBalance = 900
        viewModel.incrementAmount()

        assertEquals(false, viewModel.enableDonation.get())
        assertEquals(false, viewModel.enableDeduction.get())
    }

    @Test
    fun incrementAmount_Normal() {
        viewModel.donateAmount.set(0)
        viewModel.incrementAmount()
        assertEquals(viewModel.donateAmount.get(), 1)
    }

    @Test
    fun incrementAmount_Reach_Max_Limit_does_not_Increment() {
        viewModel.donateAmount.set(1)
        viewModel.incrementAmount()
        assertEquals(2, viewModel.donateAmount.get())

        viewModel.incrementAmount()
        assertNotEquals(3, viewModel.donateAmount.get())
    }

    @Test
    fun incrementAmount_Reach_Max_Limit_disables_Increment() {
        viewModel.donateAmount.set(1)
        viewModel.incrementAmount()
        assertEquals(false, viewModel.enableIncrement.get())
    }


    @Test
    fun decrementAmount_Normal() {
        viewModel.donateAmount.set(1)
        viewModel.decrementAmount()
        assertEquals(viewModel.donateAmount.get(), 0)
    }

    @Test
    fun decrementAmount_reach_0_does_not_decrement() {
        viewModel.donateAmount.set(1)
        viewModel.decrementAmount()
        assertEquals(viewModel.donateAmount.get(), 0)

        viewModel.decrementAmount()
        assertNotEquals(-1, viewModel.donateAmount.get())
    }


    @Test
    fun decrementAmount_Reach_0_disables_donation_and_decrement() {
        viewModel.donateAmount.set(1)
        viewModel.decrementAmount()
        assertEquals(false, viewModel.enableDeduction.get())
        assertEquals(false, viewModel.enableDonation.get())
    }

}