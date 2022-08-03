package suncor.com.android.model.redeem.response

data class MemberEligibilityResponse(
    val eligible: Boolean,
    val pointsBalance: Int,
    val categories: List<Category>
)

data class Category(
    val categoryId: Long,
    private val en: CategoryInfo?,
    private val fr: CategoryInfo?,
    val programs: List<Program>,
){
    val info: CategoryInfo
    get() =  en ?: fr!!
}

data class CategoryInfo(
    val title: String
)

data class Program(
    val programId: Long,
    private val en: ProgramInfo?,
    private val fr: ProgramInfo?,
){
    val info: ProgramInfo
    get() =  en ?: fr!!
}

data class ProgramInfo(
    val title: String,
    val message: String,
    val url: String
)