package com.panosdim.debttrack.repositories

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.panosdim.debttrack.database
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.model.DebtDetails
import com.panosdim.debttrack.model.PersonDebts
import com.panosdim.debttrack.selectedTab
import com.panosdim.debttrack.user
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class Repository {
    fun getDebts(): Flow<List<PersonDebts>> {
        return callbackFlow {
            val debtRef =
                user?.let { database.getReference(it.uid).child(selectedTab.getFirebasePath()) }

            val listener =
                debtRef?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val items = mutableStateListOf<PersonDebts>()
                        snapshot.children.forEach { dataSnapshot ->
                            val item = PersonDebts()
                            dataSnapshot.key?.let { key ->
                                item.name = key

                                dataSnapshot.children.forEach {
                                    val details = it.getValue(DebtDetails::class.java)
                                    if (details != null) {
                                        details.id = it.key
                                        item.debts = item.debts.plus(details)
                                    }
                                }
                            }
                            items.add(item)
                        }
                        // Emit the user data to the flow
                        trySend(items)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cancel()
                    }

                })

            awaitClose {
                if (listener != null) {
                    debtRef.removeEventListener(listener)
                }
            }
        }
    }

    fun deleteItem(item: Debt) {
        val debtRef = user?.let {
            item.debt.id?.let { id ->
                database.getReference(it.uid).child(selectedTab.getFirebasePath()).child(item.name)
                    .child(
                        id
                    )
            }
        }
        debtRef?.removeValue()
    }

    fun addNewItem(item: Debt) {
        val debtRef = user?.let {
            database.getReference(it.uid).child(selectedTab.getFirebasePath()).child(item.name)
        }

        debtRef?.push()?.setValue(item.debt)
    }

    fun updateItem(item: Debt) {
        val debtRef = user?.let {
            item.debt.id?.let { id ->
                database.getReference(it.uid).child(selectedTab.getFirebasePath()).child(item.name)
                    .child(
                        id
                    )
            }
        }

        debtRef?.setValue(item.debt)
        debtRef?.child("id")?.removeValue()
    }
}