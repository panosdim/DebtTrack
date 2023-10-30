package com.panosdim.debttrack

import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.panosdim.debttrack.utils.TabNames

val paddingLarge = 8.dp
val paddingExtraLarge = 16.dp

var user = FirebaseAuth.getInstance().currentUser
val database = FirebaseDatabase.getInstance()
var selectedTab = TabNames.THEY_OWE_ME
const val TAG = "DebtTrack-Tag"