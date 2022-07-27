package suncor.com.android.model.redeem.response

data class MemberEligibilityResponse(
    val eligible: Boolean,
    val pointsBalance: Int,
    val categories: List<Category>
)

data class Category(
    val categoryID: Long,
    val name: String,
    private val en: CategoryInfo?,
    private val fr: CategoryInfo?,
    val programs: List<Program>,
    val enabled: Boolean
){
    val info: CategoryInfo
    get() =  en ?: fr!!
}

data class CategoryInfo(
    val title: String
)

data class Program(
    val programID: Long,
    val name: String,
    private val en: ProgramInfo?,
    private val fr: ProgramInfo?,
    val enabled: Boolean,
){
    val info: ProgramInfo
    get() =  en ?: fr!!
}

data class ProgramInfo(
    val title: String,
    val message: String,
    val url: String
)