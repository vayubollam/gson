package suncor.com.android.model.account

class DeleteAccountRequest (val petroPointsNumber: String,
                            val firstName: String,
                            val lastName: String,
                            val email: String,
                            val streetAddress: String,
                            val city: String,
                            val province: String,
                            val postalCode: String,
                            val phone: String,
                            val locationNotConvenient: Boolean,
                            val differentLoyalty: Boolean,
                            val betterValueElseWhere: Boolean,
                            val differentReason: String
)
