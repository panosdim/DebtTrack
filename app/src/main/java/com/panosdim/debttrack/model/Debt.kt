package com.panosdim.debttrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PersonDebts(
    var name: String = "",
    var debts: List<DebtDetails> = mutableListOf()
) :
    Parcelable

@Parcelize
data class Debt(
    var name: String = "",
    var debt: DebtDetails = DebtDetails()
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