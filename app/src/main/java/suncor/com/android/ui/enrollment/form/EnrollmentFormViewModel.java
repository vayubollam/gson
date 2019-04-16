package suncor.com.android.ui.enrollment.form;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.data.repository.account.EnrollmentsApi;
import suncor.com.android.data.repository.suggestions.CanadaPostAutocompleteProvider;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.model.account.NewEnrollment;
import suncor.com.android.model.account.Province;
import suncor.com.android.model.account.SecurityQuestion;
import suncor.com.android.model.canadapost.CanadaPostDetails;
import suncor.com.android.model.canadapost.CanadaPostSuggestion;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.EmailInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PasswordInputField;
import suncor.com.android.ui.common.input.PostalCodeField;
import suncor.com.android.ui.common.input.StreetAddressInputField;
import suncor.com.android.utilities.Timber;

public class EnrollmentFormViewModel extends ViewModel {

    public LiveData<Resource<EnrollmentsApi.EmailState>> emailCheckLiveData;

    public LiveData<Resource<Boolean>> joinLiveData;
    private MutableLiveData<Event<Boolean>> join = new MutableLiveData<>();

    //autocomplete fields
    public MutableLiveData<Boolean> showAutocompleteLayout = new MutableLiveData<>();
    private MediatorLiveData<Resource<CanadaPostSuggestion[]>> autocompleteResults;
    private LiveData<Resource<CanadaPostDetails>> placeDetailsApiCall;
    private MutableLiveData<CanadaPostSuggestion> findMoreSuggestions = new MutableLiveData<>();
    private MutableLiveData<CanadaPostSuggestion> retrieveSuggestionDetails = new MutableLiveData<>();
    private ArrayList<Province> provincesList;

    //Input fields
    private InputField firstNameField = new InputField(R.string.enrollment_first_name_error);
    private InputField lastNameField = new InputField(R.string.enrollment_last_name_error);
    private EmailInputField emailInputField = new EmailInputField(R.string.enrollment_email_empty_error, R.string.enrollment_email_format_error);
    private PasswordInputField passwordField = new PasswordInputField(R.string.enrollment_password_empty_error);
    private InputField securityQuestionField = new InputField();
    private InputField securityAnswerField = new InputField();
    private StreetAddressInputField streetAddressField = new StreetAddressInputField(R.string.enrollment_street_address_error);
    private InputField cityField = new InputField(R.string.enrollment_city_error);
    private InputField provinceField = new InputField(R.string.enrollment_province_error);
    private PostalCodeField postalCodeField = new PostalCodeField(R.string.enrollment_postalcode_error, R.string.enrollment_postalcode_format_error, R.string.enrollment_postalcode_matching_province_error);
    private InputField phoneField = new InputField();
    private ObservableBoolean newsAndOffersField = new ObservableBoolean();

    private SecurityQuestion selectedQuestion;
    private Province selectedProvince;

    private ArrayList<InputField> requiredFields = new ArrayList<>();
    private CardStatus cardStatus;

    @Inject
    public EnrollmentFormViewModel(EnrollmentsApi enrollmentsApi, SessionManager sessionManager, CanadaPostAutocompleteProvider canadaPostAutocompleteProvider) {
        requiredFields.add(firstNameField);
        requiredFields.add(lastNameField);
        requiredFields.add(emailInputField);
        requiredFields.add(passwordField);
        requiredFields.add(streetAddressField);
        requiredFields.add(cityField);
        requiredFields.add(provinceField);
        requiredFields.add(postalCodeField);

        emailCheckLiveData = Transformations.switchMap(emailInputField.getHasFocusObservable(), (event) -> {
            Boolean hasFocus = event.getContentIfNotHandled();
            //If it's focused, or has already been checked, or email is invalid, return empty livedata
            if (hasFocus == null || hasFocus
                    || emailInputField.getVerificationState() != EmailInputField.VerificationState.UNCHECKED
                    || !emailInputField.isValid()) {
                MutableLiveData<Resource<EnrollmentsApi.EmailState>> temp = new MutableLiveData<>();
                temp.setValue(Resource.success(EnrollmentsApi.EmailState.UNCHECKED));
                return temp;
            } else {
                return Transformations.map(enrollmentsApi.checkEmail(emailInputField.getText()), (r) -> {
                    //to avoid further checks, save the state to the email field
                    if (r.status != Resource.Status.LOADING) {
                        emailInputField.setVerificationState(EmailInputField.VerificationState.CHECKED);
                    }
                    return r;
                });
            }
        });

        LiveData<Resource<Boolean>> joinApiData = Transformations.switchMap(join, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                Timber.d("Start sign up process");
                NewEnrollment account = new NewEnrollment(
                        NewEnrollment.EnrollmentType.NEW,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        emailInputField.getText(),
                        passwordField.getText(),
                        streetAddressField.getText(),
                        cityField.getText(),
                        selectedProvince.getId(),
                        postalCodeField.getText().replace(" ", ""), //Replace the space characters
                        phoneField.getText().replaceAll("[^\\d]", ""), //Replace all characters except digits
                        newsAndOffersField.get(),
                        selectedQuestion.getId(),
                        securityAnswerField.getText()
                );

                return enrollmentsApi.registerAccount(account);
            }
            return new MutableLiveData<>();
        });

        joinLiveData = Transformations.switchMap(joinApiData, (result) -> {
            if (result.status == Resource.Status.SUCCESS) {
                //login the user
                Timber.d("Success sign up, start user auto login");
                return Transformations.map(sessionManager.login(emailInputField.getText(), passwordField.getText()), (r) -> {
                    switch (r.status) {
                        case SUCCESS:
                            if (r.data.getStatus() == SigninResponse.Status.SUCCESS) {
                                Timber.d("Login succeeded");
                                sessionManager.setAccountState(SessionManager.AccountState.JUST_ENROLLED);
                                return Resource.success(true);
                            } else {
                                Timber.d("Login failed, status: " + r.data.getStatus());
                                return Resource.error(r.data.getStatus().toString());
                            }
                        case ERROR:
                            Timber.d("Login failed");
                            return Resource.error(r.message);
                        default:
                            return Resource.loading();
                    }
                });
            } else {
                MutableLiveData<Resource<Boolean>> intermediateLivedata = new MutableLiveData<>();
                intermediateLivedata.setValue(result);
                return intermediateLivedata;
            }
        });

        initAutoComplete(canadaPostAutocompleteProvider);
    }

    private void initAutoComplete(CanadaPostAutocompleteProvider provider) {
        autocompleteResults = new MediatorLiveData<>();
        LiveData<Resource<CanadaPostSuggestion[]>> suggestionsOnTextChange = Transformations.switchMap(streetAddressField.getTextLiveData(), text -> {
            if (text.length() >= 3) {
                return provider.findSuggestions(text, null);
            } else {
                showAutocompleteLayout.postValue(false);
                return new MutableLiveData<>();
            }
        });


        LiveData<Resource<CanadaPostSuggestion[]>> suggestionsOnFindPlaceClicked = Transformations.switchMap(findMoreSuggestions, place -> {
            return provider.findSuggestions(streetAddressField.getText(), place.getId());
        });

        placeDetailsApiCall = Transformations.switchMap(retrieveSuggestionDetails, place -> {
            return provider.getPlaceDetails(place.getId());
        });

        autocompleteResults.addSource(suggestionsOnTextChange, (results) -> {
            if (results.status == Resource.Status.SUCCESS && streetAddressField.hasFocus()) {
                showAutocompleteLayout.postValue(true);
            }
            autocompleteResults.setValue(results);
        });

        autocompleteResults.addSource(suggestionsOnFindPlaceClicked, (results) -> {
            autocompleteResults.setValue(results);
        });

        placeDetailsApiCall.observeForever((result) -> {
            if (result.status == Resource.Status.LOADING) {
                autocompleteResults.setValue(Resource.loading());
            } else if (result.status == Resource.Status.SUCCESS) {
                CanadaPostDetails placeDetails = result.data;
                streetAddressField.setTextSilent(placeDetails.getLine1());
                streetAddressField.notifyPropertyChanged(BR.text);
                postalCodeField.setText(placeDetails.getPostalCode());
                postalCodeField.notifyPropertyChanged(BR.text);
                cityField.setText(placeDetails.getCity());
                cityField.notifyPropertyChanged(BR.text);
                Province province = Province.findProvince(provincesList, placeDetails.getProvinceCode());
                province.setName(placeDetails.getProvinceName());
                setSelectedProvince(province);
                provinceField.notifyPropertyChanged(BR.text);
                showAutocompleteLayout.setValue(false);
            } else {
                //TODO handle error
                showAutocompleteLayout.setValue(false);
            }
        });
    }

    public LiveData<Resource<CanadaPostSuggestion[]>> getAutocompleteResults() {
        return autocompleteResults;
    }

    public LiveData<Resource<CanadaPostDetails>> getAutocompleteRetrievalStatus() {
        return placeDetailsApiCall;
    }

    public ObservableBoolean getNewsAndOffersField() {
        return newsAndOffersField;
    }

    public boolean oneItemFilled() {
        for (InputField input : requiredFields) {
            if (!input.isEmpty()) {
                return true;
            }
        }
        return !phoneField.isEmpty()
                || !securityQuestionField.isEmpty()
                || !securityAnswerField.isEmpty();
    }

    public void hideAutoCompleteLayout() {
        showAutocompleteLayout.setValue(false);
    }

    /**
     * retrun index of first invalid item to focus on, -1 if all items valid
     */
    public int validateAndJoin() {
        boolean firstItemFocused = false;
        int firstItemWithError = -1;
        for (int i = 0; i < requiredFields.size(); i++) {
            InputField field = requiredFields.get(i);
            if (!field.isValid()) {
                field.setShowError(true);
                if (!firstItemFocused) {
                    firstItemWithError = i;
                    firstItemFocused = true;
                }
            }
        }
        if (firstItemWithError == -1) {
            //proceed to join
            join.postValue(Event.newEvent(true));
        }
        return firstItemWithError;
    }

    public void addressSuggestionClicked(CanadaPostSuggestion suggestion) {
        if (suggestion.getNext() == CanadaPostSuggestion.Next.FIND) {
            findMoreSuggestions.postValue(suggestion);
        } else {
            retrieveSuggestionDetails.postValue(suggestion);
        }
    }

    public InputField getFirstNameField() {

        return firstNameField;
    }

    public InputField getLastNameField() {

        return lastNameField;
    }

    public EmailInputField getEmailInputField() {

        return emailInputField;
    }

    public PasswordInputField getPasswordField() {

        return passwordField;
    }

    public InputField getSecurityQuestionField() {

        return securityQuestionField;
    }

    public InputField getSecurityAnswerField() {
        return securityAnswerField;
    }

    public StreetAddressInputField getStreetAddressField() {
        return streetAddressField;
    }

    public InputField getCityField() {
        return cityField;
    }

    public InputField getProvinceField() {
        return provinceField;
    }

    public PostalCodeField getPostalCodeField() {
        return postalCodeField;
    }

    public InputField getPhoneField() {
        return phoneField;
    }

    ArrayList<InputField> getRequiredFields() {
        return requiredFields;
    }

    public void setSelectedQuestion(SecurityQuestion selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
        if (selectedQuestion != null) {
            securityQuestionField.setText(selectedQuestion.getLocalizedQuestion());
        }
    }

    public Province getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(Province selectedProvince) {
        this.selectedProvince = selectedProvince;
        if (selectedProvince != null) {
            provinceField.setText(selectedProvince.getName());
            postalCodeField.setFirstCharacterValidation(selectedProvince.getFirstCharacter());
            //to trigger postal code validation
            postalCodeField.setHasFocus(false);
        }
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        if (cardStatus == null)
            return;
        this.cardStatus = cardStatus;
        if (cardStatus.getCardType() == NewEnrollment.EnrollmentType.EXISTING) {
            firstNameField.setText(cardStatus.getUserInfo().getFirstName());
            lastNameField.setText(cardStatus.getUserInfo().getLastName());
            if (cardStatus.getUserInfo().getEmail() != null) {
                emailInputField.setText(cardStatus.getUserInfo().getEmail());
            }
            streetAddressField.setText(cardStatus.getAddress().getStreetAddress());
            cityField.setText(cardStatus.getAddress().getCity());
            provinceField.setText(cardStatus.getAddress().getProvince());
            postalCodeField.setText(cardStatus.getAddress().getPostalCode());
            if (cardStatus.getAddress().getPhone() != null) {
                phoneField.setText(cardStatus.getAddress().getPhone());
            }
        } else {
            lastNameField.setText(cardStatus.getUserInfo().getLastName());
            postalCodeField.setText(cardStatus.getAddress().getPostalCode());
        }
    }

    public void setProvincesList(ArrayList<Province> provinces) {
        this.provincesList = provinces;
    }
}
