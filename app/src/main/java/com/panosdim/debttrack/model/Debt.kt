package com.panosdim.debttrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PersonDebts(
    var name: String = "",
    var debts: List<DebtDetails>
) :
    Parcelable

@Parcelize
data class Debt(
    var name: String = "",
    var debt: DebtDetails
) :
    Parcelable

@Parcelize
data class DebtDetails(
    var id: String? = null,
    var amount: String = "",
    var date: String = "",
    var comment: String = ""
) :
    Parcelable