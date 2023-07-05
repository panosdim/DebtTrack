package com.panosdim.debttrack.viewmodels

import androidx.lifecycle.ViewModel
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.repositories.Repository
import com.panosdim.debttrack.utils.TabNames

class TheyOweMe : ViewModel() {
    private val repository = Repository()
    val debts = repository.getDebt(TabNames.THEYOWEME)

    fun addDebt(debt: Debt) {
        repository.addNewItem(TabNames.THEYOWEME, debt)
    }

    fun updateDebt(debt: Debt) {
        repository.updateItem(TabNames.THEYOWEME, debt)
    }

    fun removeDebt(debt: Debt) {
        repository.deleteItem(TabNames.THEYOWEME, debt)
    }
}