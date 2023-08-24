package com.panosdim.debttrack.viewmodels

import androidx.lifecycle.ViewModel
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.model.PersonDebts
import com.panosdim.debttrack.model.Response
import com.panosdim.debttrack.repositories.Repository
import kotlinx.coroutines.flow.Flow

class DebtsViewModel : ViewModel() {
    private val repository = Repository()

    fun getDebts(): Flow<Response<List<PersonDebts>>> {
        return repository.getDebts()
    }

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