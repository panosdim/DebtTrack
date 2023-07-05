package com.panosdim.debttrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Debt(
    var id: String? = null,
    var name: String = "",
    var amount: Float = 0.0F,
    var date: String = "",
    var comment: String = ""
) :
    Parcelable