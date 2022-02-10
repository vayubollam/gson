package suncor.com.android.ui.main.profile.address;

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
import suncor.com.android.data.profiles.ProfilesApi;
import suncor.com.android.data.suggestions.CanadaPostAutocompleteProvider;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.model.account.Province;
import suncor.com.android.model.canadapost.CanadaPostDetails;
import suncor.com.android.model.canadapost.CanadaPostSuggestion;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.CityInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PostalCodeField;
import suncor.com.android.ui.common.input.StreetAddressInputField;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;

public class AddressViewModel extends ViewModel {


    private MediatorLiveData<Resource<CanadaPostSuggestion[]>> autocompleteResults;
    public MutableLiveData<Boolean> showAutocompleteLayout = new MutableLiveData<>();
    private LiveData<Resource<CanadaPostDetails>> placeDetailsApiCall;
    private ArrayList<Province> provincesList;
    private ObservableBoolean isEditing = new ObservableBoolean(false);
    private Province selectedProvince;
    private Profile profile;
    private LiveData<Resource<Boolean>> profilesApiCall;
    private MutableLiveData<Event<Boolean>> _navigateToProfile = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToProfile = _navigateToProfile;
    private MutableLiveData<Event> updateProfileEvent = new MutableLiveData<>();
    private MutableLiveData<CanadaPostSuggestion> findMoreSuggestions = new MutableLiveData<>();
    private MutableLiveData<CanadaPostSuggestion> retrieveSuggestionDetails = new MutableLiveData<>();
    private ProfileSharedViewModel sharedViewModel;
    private MutableLiveData<Event<Boolean>> updateEvent = new MutableLiveData<>();
    private MutableLiveData<Event> _showSaveButtonEvent = new MutableLiveData<>();
    public LiveData<Event> showSaveButtonEvent = _showSaveButtonEvent;
    private StreetAddressInputField streetAddressField = new StreetAddressInputField(R.string.enrollment_street_address_error, R.string.enrollment_street_format_error);
    private CityInputField cityField = new CityInputField(R.string.enrollment_city_error, R.string.enrollment_city_format_error);
    private InputField provinceField = new InputField(R.string.enrollment_province_error);
    private PostalCodeField postalCodeField = new PostalCodeField(R.string.enrollment_postalcode_error, R.string.enrollment_postalcode_format_error, R.string.enrollment_postalcode_matching_province_error);

    @Inject
    public AddressViewModel(CanadaPostAutocompleteProvider autocompleteProvider, SessionManager sessionManager, ProfilesApi profilesApi) {
        initAutoComplete(autocompleteProvider);
        profile = sessionManager.getProfile();
        cityField.setText(profile.getCity());
        streetAddressField.setText(profile.getStreetAddress());
        postalCodeField.setText(profile.getPostalCode());

        profilesApiCall = Transformations.switchMap(updateEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                ProfileRequest request = new ProfileRequest();
                request.setSecurityAnswerEncrypted(sharedViewModel.getEcryptedSecurityAnswer());

                if (!streetAddressField.getText().equals(profile.getStreetAddress()) || !cityField.getText().equals(profile.getCity())
                        || !provinceField.getText().equals(getProvinceNameById(profile.getProvince()))
                        || !postalCodeField.getText().replace(" ", "").equals(profile.getPostalCode())) {
                    request.getAddress().setStreetAddress(streetAddressField.getText());
                    request.getAddress().setCity(cityField.getText());
                    request.getAddress().setProvince(selectedProvince.getId());
                    request.getAddress().setPostalCode(postalCodeField.getText());
                }

                return profilesApi.updateProfile(request);
            } else {
                return new MutableLiveData<>();
            }
        });
        profilesApiCall.observeForever(resource -> {
            switch (resource.status) {
                case LOADING:
                    _navigateToProfile.setValue(Event.newEvent(true));
                    break;
                case SUCCESS:
                    sharedViewModel.postToast(R.string.profile_update_toast);
                    profile.setStreetAddress(streetAddressField.getText());
                    profile.setCity(cityField.getText());
                    profile.setProvince(selectedProvince.getId());
                    profile.setPostalCode(postalCodeField.getText());
                    break;
                case ERROR:
                    ProfileSharedViewModel.Alert alert = new ProfileSharedViewModel.Alert();
                    alert.title = R.string.msg_e001_title;
                    alert.message = R.string.msg_e001_message;
                    alert.positiveButton = R.string.ok;
                    sharedViewModel.postAlert(alert);
            }
        });

    }

    private String getProvinceNameById(String province) {

        try {
            if (provincesList.indexOf(new Province(province, null, null)) != -1) {
                return provincesList.get(provincesList.indexOf(new Province(province, null, null))).getName();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
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


        LiveData<Resource<CanadaPostSuggestion[]>> suggestionsOnFindPlaceClicked = Transformations.switchMap(findMoreSuggestions, place -> provider.findSuggestions(streetAddressField.getText(), place.getId()));

        placeDetailsApiCall = Transformations.switchMap(retrieveSuggestionDetails, place -> provider.getPlaceDetails(place.getId()));

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
                streetAddressField.notifyPropertyChanged(suncor.com.android.BR.text);
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
                // handle error
                showAutocompleteLayout.setValue(false);
            }
        });
    }


    public void setProvincesList(ArrayList<Province> provincesList) {
        this.provincesList = provincesList;

        String province = profile.getProvince();

        provinceField.setText(getProvinceNameById(province));

        if (provincesList.indexOf(new Province(province, null, null)) != -1) {
            sharedViewModel.setSelectedProvince(provincesList.get(provincesList.indexOf(new Province(province, null, null))));
        }

    }

    public void setSelectedProvince(Province selectedProvince) {
        this.selectedProvince = selectedProvince;
        if (selectedProvince != null) {
            if (!selectedProvince.getName().equalsIgnoreCase(provinceField.getText())) {
                _showSaveButtonEvent.postValue(Event.newEvent(true));
            }
            provinceField.setText(selectedProvince.getName());
            postalCodeField.setFirstCharacterValidation(selectedProvince.getFirstCharacter());
            //to trigger postal code validation
            postalCodeField.setHasFocus(false);

        }
    }

    public Province getSelectedProvince() {
        return selectedProvince;
    }

    public void addressSuggestionClicked(CanadaPostSuggestion suggestion) {
        if (suggestion.getNext() == CanadaPostSuggestion.Next.FIND) {
            findMoreSuggestions.postValue(suggestion);
        } else {
            retrieveSuggestionDetails.postValue(suggestion);
        }
    }

    public void hideAutoCompleteLayout() {
        showAutocompleteLayout.setValue(false);
    }

    public LiveData<Resource<CanadaPostSuggestion[]>> getAutocompleteResults() {
        return autocompleteResults;
    }

    public LiveData<Resource<CanadaPostDetails>> getAutocompleteRetrievalStatus() {
        return placeDetailsApiCall;
    }

    public ObservableBoolean getIsEditing() {
        return isEditing;
    }

    private void callUpdateProfile() {
        updateProfileEvent.setValue(Event.newEvent(true));
    }

    public void setSharedViewModel(ProfileSharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }

    public void streetAdressChange(String text) {
        if (!text.equals(streetAddressField.getText())) {
            _showSaveButtonEvent.setValue(Event.newEvent(true));
        }
    }


    public void provinceChanged(String text) {
        if (!text.equals(provinceField.getText())) {
            _showSaveButtonEvent.setValue(Event.newEvent(true));
        }
    }

    public void cityChanged(String text) {
        if (!text.equals(cityField.getText())) {
            _showSaveButtonEvent.setValue(Event.newEvent(true));
        }
    }

    public void postalCodeChanged(String text) {
        if (!text.replace(" ", "").equals(postalCodeField.getText().replace(" ", ""))) {
            _showSaveButtonEvent.setValue(Event.newEvent(true));
        }
    }

    public void save(boolean hasConnection) {
        boolean isValid = true;
        if (!streetAddressField.isValid()) {
            streetAddressField.setShowError(true);
            isValid = false;
        }
        if (!cityField.isValid()) {
            cityField.setShowError(true);
            isValid = false;
        }
        if (!provinceField.isValid()) {
            provinceField.setShowError(true);
            isValid = false;
        }
        if (!postalCodeField.isValid()) {
            postalCodeField.setShowError(true);
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        boolean isUpdatingSAF = !streetAddressField.getText().equals(profile.getStreetAddress());
        boolean isUpdatingCF = !cityField.getText().equals(profile.getCity());
        boolean isUpdatingPF = !provinceField.getText().equals(getProvinceNameById(profile.getProvince()));
        boolean isUpdatingPCF = !postalCodeField.getText().equals(profile.getPostalCode().replace(" ", ""));
        boolean shouldUpdateProfile = isUpdatingSAF || isUpdatingCF || isUpdatingPF || isUpdatingPCF;

        if (shouldUpdateProfile) {
            if (!hasConnection) {
                _navigateToProfile.setValue(Event.newEvent(true));
                ProfileSharedViewModel.Alert alert = new ProfileSharedViewModel.Alert();
                alert.title = R.string.msg_e002_title;
                alert.message = R.string.msg_e002_message;
                alert.positiveButton = R.string.ok;
                sharedViewModel.postAlert(alert);
            } else {
                updateEvent.setValue(Event.newEvent(true));
            }
        } else {
            _navigateToProfile.setValue(Event.newEvent(true));
            sharedViewModel.postToast(R.string.profile_update_toast);
        }
    }


}
