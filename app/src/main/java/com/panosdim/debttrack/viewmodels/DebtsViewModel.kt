package com.panosdim.debttrack.viewmodels

import androidx.lifecycle.ViewModel
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.repositories.Repository

class DebtsViewModel : ViewModel() {
    private val repository = Repository()
    val debts = repository.getDebts()

    fun addDebt(debt: Debt) {
        repository.addNewItem(debt)
    }

    fun updateDebt(debt: Debt) {
        repository.updateItem(debt)
    }

    fun removeDebt(debt: Debt) {
        repository.deleteItem(debt)
    }
}