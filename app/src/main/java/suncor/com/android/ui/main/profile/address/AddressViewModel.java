package suncor.com.android.ui.main.profile.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.data.suggestions.CanadaPostAutocompleteProvider;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Province;
import suncor.com.android.model.canadapost.CanadaPostDetails;
import suncor.com.android.model.canadapost.CanadaPostSuggestion;
import suncor.com.android.ui.common.input.CityInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PostalCodeField;
import suncor.com.android.ui.common.input.StreetAddressInputField;

public class AddressViewModel extends ViewModel {


    private MediatorLiveData<Resource<CanadaPostSuggestion[]>> autocompleteResults;
    public MutableLiveData<Boolean> showAutocompleteLayout = new MutableLiveData<>();
    private LiveData<Resource<CanadaPostDetails>> placeDetailsApiCall;
    private ArrayList<Province> provincesList;
    private Province selectedProvince;
    private MutableLiveData<CanadaPostSuggestion> findMoreSuggestions = new MutableLiveData<>();
    private MutableLiveData<CanadaPostSuggestion> retrieveSuggestionDetails = new MutableLiveData<>();
    private StreetAddressInputField streetAddressField = new StreetAddressInputField(R.string.enrollment_street_address_error, R.string.enrollment_street_format_error);
    private CityInputField cityField = new CityInputField(R.string.enrollment_city_error, R.string.enrollment_city_format_error);
    private InputField provinceField = new InputField(R.string.enrollment_province_error);
    private PostalCodeField postalCodeField = new PostalCodeField(R.string.enrollment_postalcode_error, R.string.enrollment_postalcode_format_error, R.string.enrollment_postalcode_matching_province_error);

    @Inject
    public AddressViewModel(CanadaPostAutocompleteProvider autocompleteProvider) {
        initAutoComplete(autocompleteProvider);
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
                //TODO handle error
                showAutocompleteLayout.setValue(false);
            }
        });
    }

    public void setProvincesList(ArrayList<Province> provincesList) {
        this.provincesList = provincesList;
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
}
