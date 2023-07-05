package com.panosdim.debttrack.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.panosdim.debttrack.database
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.user
import com.panosdim.debttrack.utils.TabNames
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class Repository {
    fun getDebt(tab: TabNames): Flow<List<Debt>> {
        return callbackFlow {
            val debtRef = user?.let { database.getReference(tab.getFirebasePath()).child(it.uid) }

            val listener =
                debtRef?.orderByChild("date")?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val items = mutableListOf<Debt>()
                        snapshot.children.forEach {
                            val item = it.getValue(Debt::class.java)
                            if (item != null) {
                                item.id = it.key
                                items.add(item)
                            }
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

    fun deleteItem(tab: TabNames, item: Debt) {
        val debtRef = user?.let {
            item.id?.let { id ->
                database.getReference(tab.getFirebasePath()).child(it.uid).child(
                    id
                )
            }
        }
        debtRef?.removeValue()
    }

    fun addNewItem(tab: TabNames, item: Debt) {
        val debtRef = user?.let { database.getReference(tab.getFirebasePath()).child(it.uid) }

        debtRef?.push()?.setValue(item)
    }

    fun updateItem(tab: TabNames, item: Debt) {
        val debtRef = user?.let {
            item.id?.let { id ->
                database.getReference(tab.getFirebasePath()).child(it.uid).child(
                    id
                )
            }
        }

        debtRef?.setValue(item)
        debtRef?.child("id")?.removeValue()
    }
}