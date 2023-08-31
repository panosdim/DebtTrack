package com.panosdim.debttrack.viewmodels

import androidx.lifecycle.ViewModel
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.model.PersonDebts
import com.panosdim.debttrack.repositories.Repository
import com.panosdim.debttrack.utils.TabNames

class DebtsViewModel : ViewModel() {
    private val repository = Repository()

    val iOweDebts = repository.getDebts(TabNames.I_OWE.getFirebasePath())
    val theyOweMeDebts = repository.getDebts(TabNames.THEY_OWE_ME.getFirebasePath())

    fun addDebt(debt: Debt) {
        repository.addNewItem(debt)
    }

    fun updateDebt(debt: Debt) {
        repository.updateItem(debt)
    }

    fun removeDebt(debt: Debt) {
        repository.deleteItem(debt)
    }

    fun removePersonDebts(personDebts: PersonDebts) {
        repository.deletePersonDebts(personDebts)
    }
}