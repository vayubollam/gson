package suncor.com.android.googlepay.passes


data class LoyalityData(
        var barcode: String? = null,
        var barcodeDisplay: String? = null,
        var barcodeType: String? = "pdf417",
        var nameLabel: String? = null,
        var nameValue: String? = null,
        var emailLabel: String? = null,
        var emailValue: String? = null,
        var detailsLabel: String? = null,
        var detailsValue: String? = null,
        var valuesLabel: String? = null,
        var valuesValue: String? = null,
        var howToUseLabel: String? = null,
        var howToUseValue: String? = null,
        var termConditionLabel: String? = null,
        var termConditionValue: String? = null,
        var currentLat: String? = null,
        var currentLng: String? = null,

)
