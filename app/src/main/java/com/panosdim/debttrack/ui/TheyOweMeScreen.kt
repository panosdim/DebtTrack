package com.panosdim.debttrack.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panosdim.debttrack.TAG
import com.panosdim.debttrack.viewmodels.TheyOweMe
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheyOweMeScreen() {
    val viewModel: TheyOweMe = viewModel()
    val debtItems =
        viewModel.debts.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch { bottomSheetState.show() }
                },
                content = {
                    Icon(Icons.Filled.Add, null)
                })
        }

    ) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentPadding = it
        ) {
            items(debtItems.value) { item ->
                Log.d(TAG, item.toString())
                Box(contentAlignment = Alignment.TopEnd) {
                    DebtCard(item)
                }
            }
        }
        DebtSheet(debtItem = null, bottomSheetState = bottomSheetState)
    }
}