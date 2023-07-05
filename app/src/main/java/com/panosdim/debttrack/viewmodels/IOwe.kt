package com.panosdim.debttrack.viewmodels

import androidx.lifecycle.ViewModel
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.repositories.Repository
import com.panosdim.debttrack.utils.TabNames

class IOwe : ViewModel() {
    private val repository = Repository()
    val debts = repository.getDebt(TabNames.IOWE)

    fun addDebt(debt: Debt) {
        repository.addNewItem(TabNames.IOWE, debt)
    }

    fun updateDebt(debt: Debt) {
        repository.updateItem(TabNames.IOWE, debt)
    }

    fun removeDebt(debt: Debt) {
        repository.deleteItem(TabNames.IOWE, debt)
    }
}