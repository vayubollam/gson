package suncor.com.android.model.redeem.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MemberEligibilityResponse(
    val eligible: Boolean,
    val pointsBalance: Int,
    val categories: List<Category>
): Parcelable

@Parcelize
data class Category(
    val categoryId: Long,
    private val en: CategoryInfo?,
    private val fr: CategoryInfo?,
    val programs: List<Program>,
): Parcelable{
    val info: CategoryInfo
    get() =  en ?: fr!!
}

@Parcelize
data class CategoryInfo(
    val title: String
): Parcelable

@Parcelize
data class Program(
    val programId: Long,
    var smallImage:Int,
    var largeImage: Int,
    private val en: ProgramInfo?,
    private val fr: ProgramInfo?,
): Parcelable{
    val info: ProgramInfo
    get() =  en ?: fr!!
}

@Parcelize
data class ProgramInfo(
    val title: String,
    val message: String,
    val url: String
): Parcelable
