package suncor.com.android.ui.main.profile.account

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.io.BaseEncoding
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.R
import suncor.com.android.data.profiles.ProfilesApi
import suncor.com.android.model.Resource
import suncor.com.android.model.account.SecurityQuestion
import suncor.com.android.ui.common.Event
import suncor.com.android.ui.main.profile.securityquestion.SecurityQuestionValidationViewModel

@RunWith(MockitoJUnitRunner::class)
class SecurityQuestionValidationViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var securityQuestionValidationViewModel: SecurityQuestionValidationViewModel

    @Mock
    private lateinit var profilesApi: ProfilesApi
    var securityQuestionLiveData: LiveData<Resource<SecurityQuestion>>? = null
    var securityAnswerLiveData: LiveData<Resource<String>>? = null
    private val loadSecurityQuestion = MutableLiveData<Event<Boolean>>()
    private val validateSecurityQuestion = MutableLiveData<Event<Boolean>>()
    private lateinit var question: SecurityQuestion

    @Before
    fun setUp() {
        securityQuestionValidationViewModel = SecurityQuestionValidationViewModel(profilesApi)
        securityQuestionLiveData = MutableLiveData<Resource<SecurityQuestion>>()
        question = Gson().fromJson<SecurityQuestion>(
            responseJson,
            SecurityQuestion::
            class.java
        )
        securityQuestionLiveData?.observeForever { securityQuestionResource: Resource<SecurityQuestion> ->
            //dummy observer
        }
    }

    @Test
    fun testSecutityQuestionFieldEmpty() {
        securityQuestionValidationViewModel.validateAndContinue()
        Assert.assertTrue(securityQuestionValidationViewModel.questionAnswerField.getShowError())
        Assert.assertEquals(
            R.string.profile_security_question_error,
            securityQuestionValidationViewModel.questionAnswerField.getError()
        )
    }

    @Test
    fun testToFetchSecurityQuestion() {
        val result = MutableLiveData<Resource<SecurityQuestion>>()
        val question: SecurityQuestion = Gson().fromJson<SecurityQuestion>(
            responseJson,
            SecurityQuestion::class.java
        )
        result.postValue(Resource.success(question))
        lenient().`when`<LiveData<Resource<SecurityQuestion>>>(
            profilesApi.securityQuestion
        ).thenReturn(result)
        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<SecurityQuestion> -> }
        result.observeForever(dummyObserver)
        Assert.assertEquals(Resource.Status.SUCCESS, result.value?.status)
    }

    @Test
    fun givenQuestion_withAns_ValidateQuestion_Success() {
        val answer = "test"
        val result = MutableLiveData<Resource<String>>()
        val base64Answer = BaseEncoding.base64().encode(answer.toByteArray())
        result.postValue(Resource.success(base64Answer))
        lenient().`when`<LiveData<Resource<String>>>(
            profilesApi.validateSecurityQuestion(base64Answer)
        ).thenReturn(result)
        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<String> -> }
        result.observeForever(dummyObserver)
        Assert.assertEquals(Resource.Status.SUCCESS, result.value?.status)
    }

    @Test
    fun givenQuestion_withAns_ValidateQuestion_Failure() {
        val answer = "test"
        val actualAns = "TestFailed"
        val result = MutableLiveData<Resource<String>>()
        val base64Answer = BaseEncoding.base64().encode(answer.toByteArray())
        val base64ActualAnswer = BaseEncoding.base64().encode(actualAns.toByteArray())
        result.postValue(Resource.error(base64ActualAnswer))
        lenient().`when`<LiveData<Resource<String>>>(
            profilesApi.validateSecurityQuestion(base64Answer)
        ).thenReturn(result)
        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<String> -> }
        result.observeForever(dummyObserver)
        Assert.assertEquals(Resource.Status.ERROR, result.value?.status)
    }


    private val responseJson = "{\n" +
            "        \"questionId\": \"VZF8UkTva3THk06HcyAC+A==\",\n" +
            "        \"question\": \"What was the name of your elementary school?\"\n" +
            "    }"


}