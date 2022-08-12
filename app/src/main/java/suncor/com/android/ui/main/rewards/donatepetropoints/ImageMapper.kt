package suncor.com.android.ui.main.rewards.donatepetropoints

import suncor.com.android.R
import suncor.com.android.model.redeem.response.MemberEligibilityResponse
import java.util.*

object ImageMapper {

    fun mapImages(memberEligibilityResponse: MemberEligibilityResponse){
        memberEligibilityResponse.categories.forEach {category->
            category.programs.forEach { program ->
                if(Locale.getDefault().language.equals("fr", ignoreCase = true)){
                    when(program.programId){
                        667.toLong() ->{
                            program.smallImage = R.drawable.caremakers_french_small
                            program.largeImage = R.drawable.caremakers_french_large
                        }

                        332.toLong() ->{
                            program.smallImage = R.drawable.olympic_card_small
                            program.largeImage = R.drawable.olympic_card_large
                        }

                        312.toLong()->{
                            program.smallImage = R.drawable.paralympic_card_small
                            program.largeImage = R.drawable.paralympic_card_large
                        }

                        375.toLong()->{
                            program.smallImage = R.drawable.coaching_association_small
                            program.largeImage = R.drawable.coaching_association_large
                        }
                    }
                }else{
                    when(program.programId){
                        667.toLong() ->{
                            program.smallImage = R.drawable.caremakers_small_english
                            program.largeImage = R.drawable.caremakers_large_english
                        }

                        332.toLong() ->{
                            program.smallImage = R.drawable.olympic_card_small
                            program.largeImage = R.drawable.olympic_card_large
                        }

                        312.toLong()->{
                            program.smallImage = R.drawable.paralympic_card_small
                            program.largeImage = R.drawable.paralympic_card_large
                        }

                        375.toLong()->{
                            program.smallImage = R.drawable.coaching_association_small
                            program.largeImage = R.drawable.coaching_association_large
                        }
                    }
                }
            }
        }
    }
}