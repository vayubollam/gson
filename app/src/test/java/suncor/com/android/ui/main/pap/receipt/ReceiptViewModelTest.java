package suncor.com.android.ui.main.pap.receipt;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import suncor.com.android.SuncorApplication;
import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.transaction.Transaction;
import suncor.com.android.utilities.UserLocalSettings;

public class ReceiptViewModelTest  {

        private ReceiptViewModel viewModel;
        private PapRepository papRepository = Mockito.mock(PapRepository.class);
        private SessionManager sessionManager = Mockito.mock(SessionManager.class);
        private SuncorApplication suncorApplication = Mockito.mock(SuncorApplication.class);

        @Rule
        public TestRule rule = new InstantTaskExecutorRule();


        @Before
        public void init() {
            UserLocalSettings settings = new UserLocalSettings(suncorApplication);
            settings.setLong(UserLocalSettings.LAST_SUCCESSFUL_PAP_DATE, 1608468891000L );
            Mockito.when(sessionManager.getUserLocalSettings()).thenReturn(settings);
            viewModel = new ReceiptViewModel(papRepository, sessionManager);
        }

    @Test
    public void test_get_transactional_details_success() {
        LiveData<Resource<Transaction>> response = viewModel.getTransactionDetails("98435fa7-3c36-4758-a9a9-bbff044cc176", false);
        Assert.assertEquals(Boolean.TRUE, response.getValue().status == Resource.Status.SUCCESS);
    }

    @Test
    public void test_get_transactional_details_error() {
        LiveData<Resource<Transaction>> response = viewModel.getTransactionDetails("98435fa7-3c36-4758-a9a9-bbff044cc176", false);
        Assert.assertEquals(Boolean.TRUE, response.getValue().status == Resource.Status.ERROR);
    }

    @Test
    public void test_is_first_transaction_Of_month() {
        boolean response = viewModel.isFirstTransactionOfMonth();
        Assert.assertEquals(Boolean.TRUE, response);
    }
    @Test
    public void test_is_not_first_transaction_Of_month() {
        boolean response = viewModel.isFirstTransactionOfMonth();
        Assert.assertEquals(Boolean.FALSE, response);
    }


}
