package com.panosdim.debttrack

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.panosdim.debttrack.utils.TabNames

var user = FirebaseAuth.getInstance().currentUser
val database = FirebaseDatabase.getInstance()
var selectedTab = TabNames.THEY_OWE_ME
const val TAG = "DebtTrack-Tag"