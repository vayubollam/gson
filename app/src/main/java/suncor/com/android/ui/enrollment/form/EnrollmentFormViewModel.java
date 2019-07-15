package suncor.com.android.ui.enrollment.form;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.data.account.EnrollmentsApi;
import suncor.com.android.data.suggestions.CanadaPostAutocompleteProvider;
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
import suncor.com.android.ui.common.input.CityInputField;
import suncor.com.android.ui.common.input.EmailInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PasswordInputField;
import suncor.com.android.ui.common.input.PhoneInputField;
import suncor.com.android.ui.common.input.PostalCodeField;
import suncor.com.android.ui.common.input.StreetAddressInputField;
import suncor.com.android.utilities.Timber;

public class EnrollmentFormViewModel extends ViewModel {

    final static String LOGIN_FAILED = "login_failed";
    public LiveData<Resource<Boolean>> joinLiveData;
    //autocomplete fields
    public MutableLiveData<Boolean> showAutocompleteLayout = new MutableLiveData<>();
    private MediatorLiveData<Resource<CanadaPostSuggestion[]>> autocompleteResults;
    private LiveData<Resource<CanadaPostDetails>> placeDetailsApiCall;
    private MutableLiveData<CanadaPostSuggestion> findMoreSuggestions = new MutableLiveData<>();
    private MutableLiveData<CanadaPostSuggestion> retrieveSuggestionDetails = new MutableLiveData<>();
    //Input fields
    private ArrayList<InputField> requiredFields = new ArrayList<>();
    private InputField firstNameField = new InputField(R.string.enrollment_first_name_error);
    private InputField lastNameField = new InputField(R.string.enrollment_last_name_error);
    private EmailInputField emailInputField = new EmailInputField(R.string.enrollment_email_empty_error, R.string.enrollment_email_format_error, R.string.enrollment_email_restricted_error);
    private PasswordInputField passwordField = new PasswordInputField(R.string.enrollment_password_empty_error);
    private InputField securityQuestionField = new InputField();
    private InputField securityAnswerField = new InputField();
    private StreetAddressInputField streetAddressField = new StreetAddressInputField(R.string.enrollment_street_address_error, R.string.enrollment_street_format_error);
    private CityInputField cityField = new CityInputField(R.string.enrollment_city_error, R.string.enrollment_city_format_error);
    private InputField provinceField = new InputField(R.string.enrollment_province_error);
    private PostalCodeField postalCodeField = new PostalCodeField(R.string.enrollment_postalcode_error, R.string.enrollment_postalcode_format_error, R.string.enrollment_postalcode_matching_province_error);
    private PhoneInputField phoneField = new PhoneInputField(R.string.enrollment_phone_field_invalid_format);
    private ObservableBoolean emailOffersField = new ObservableBoolean();
    private ObservableBoolean smsOffersField = new ObservableBoolean();
    private MutableLiveData<Event<Boolean>> navigateToLogin = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> showDuplicateEmailEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> join = new MutableLiveData<>();
    private MutableLiveData<Boolean> isValidatingEmail = new MutableLiveData<>();
    private SecurityQuestion selectedQuestion;
    private Province selectedProvince;
    private CardStatus cardStatus;
    private ArrayList<Province> provincesList;

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

        LiveData<Resource<EnrollmentsApi.EmailState>> emailCheckLiveData = Transformations.switchMap(emailInputField.getHasFocusObservable(), (event) -> {
            Boolean hasFocus = event.getContentIfNotHandled();
            //If it's focused, or has already been checked, or email is invalid, return empty livedata
            if (hasFocus == null || hasFocus
                    || emailInputField.getVerificationState() != EmailInputField.VerificationState.UNCHECKED
                    || !emailInputField.isValid()) {
                MutableLiveData<Resource<EnrollmentsApi.EmailState>> temp = new MutableLiveData<>();
                temp.setValue(Resource.success(EnrollmentsApi.EmailState.UNCHECKED));
                return temp;
            } else {
                return enrollmentsApi.checkEmail(emailInputField.getText(), cardStatus != null ? cardStatus.getCardNumber() : null);
            }
        });
        emailCheckLiveData.observeForever(r -> {
            if (r.status == Resource.Status.LOADING) {
                isValidatingEmail.setValue(true);
            } else {
                isValidatingEmail.setValue(false);
                emailInputField.setVerificationState(EmailInputField.VerificationState.CHECKED);
                if (r.status == Resource.Status.SUCCESS) {
                    if (r.data == EnrollmentsApi.EmailState.ALREADY_REGISTERED) {
                        showDuplicateEmailEvent.postValue(Event.newEvent(true));
                    } else if (r.data == EnrollmentsApi.EmailState.RESTRICTED) {
                        emailInputField.setRestricted(true);
                    }
                }
            }
        });

        LiveData<Resource<Integer>> joinApiData = Transformations.switchMap(join, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                Timber.d("Start sign up process");
                NewEnrollment account = new NewEnrollment(
                        cardStatus != null ? cardStatus.getCardType() : NewEnrollment.EnrollmentType.NEW,
                        cardStatus != null ? cardStatus.getCardNumber() : null,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        emailInputField.getText(),
                        passwordField.getText(),
                        streetAddressField.getText(),
                        cityField.getText(),
                        selectedProvince.getId(),
                        postalCodeField.getText().replace(" ", ""), //Replace the space characters
                        phoneField.getText(),
                        emailOffersField.get(),
                        smsOffersField.get(),
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
                                sessionManager.setRewardedPoints(result.data);
                                return Resource.success(true);
                            } else {
                                Timber.d("Login failed, status: " + r.data.getStatus());
                                sessionManager.setRewardedPoints(result.data);
                                navigateToLogin.postValue(Event.newEvent(true));
                                return Resource.error(LOGIN_FAILED);
                            }
                        case ERROR:
                            Timber.d("Login failed");
                            sessionManager.setRewardedPoints(result.data);
                            navigateToLogin.postValue(Event.newEvent(true));
                            return Resource.error(LOGIN_FAILED);
                        default:
                            return Resource.loading();
                    }
                });
            } else {
                MutableLiveData<Resource<Boolean>> intermediateLivedata = new MutableLiveData<>();
                if (result.status == Resource.Status.LOADING) {
                    intermediateLivedata.setValue(Resource.loading());
                } else {
                    intermediateLivedata.setValue(Resource.error(result.message));
                }
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
                String streetAddress = placeDetails.getLine1();
                streetAddressField.setTextSilent(streetAddress);
                streetAddressField.notifyPropertyChanged(BR.text);
                postalCodeField.setText(placeDetails.getPostalCode());
                postalCodeField.notifyPropertyChanged(BR.text);
                String city = placeDetails.getCity();
                cityField.setText(city);
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

    public boolean isOneItemFilled() {
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
            if (!phoneField.isValid()) {
                phoneField.setShowError(true);
                return -1;
            }

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

    public LiveData<Resource<CanadaPostSuggestion[]>> getAutocompleteResults() {
        return autocompleteResults;
    }

    public LiveData<Resource<CanadaPostDetails>> getAutocompleteRetrievalStatus() {
        return placeDetailsApiCall;
    }

    public MutableLiveData<Event<Boolean>> getShowDuplicateEmailEvent() {
        return showDuplicateEmailEvent;
    }

    public MutableLiveData<Boolean> getIsValidatingEmail() {
        return isValidatingEmail;
    }

    public LiveData<Event<Boolean>> getNavigateToLogin() {
        return navigateToLogin;
    }

    public ObservableBoolean getEmailOffersField() {
        return emailOffersField;
    }

    public ObservableBoolean getSmsOffersField() {
        return smsOffersField;
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

    public CityInputField getCityField() {
        return cityField;
    }

    public InputField getProvinceField() {
        return provinceField;
    }

    public PostalCodeField getPostalCodeField() {
        return postalCodeField;
    }

    public PhoneInputField getPhoneField() {
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
            Province province = Province.findProvince(provincesList, cardStatus.getAddress().getProvince());
            setSelectedProvince(province);
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
