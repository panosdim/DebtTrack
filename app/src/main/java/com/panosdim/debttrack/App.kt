package com.panosdim.debttrack

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

var user = FirebaseAuth.getInstance().currentUser
val database = FirebaseDatabase.getInstance()
const val TAG = "DebtTrack-Tag"