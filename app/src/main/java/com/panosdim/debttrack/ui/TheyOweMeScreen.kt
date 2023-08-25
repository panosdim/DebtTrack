package com.panosdim.debttrack.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panosdim.debttrack.R
import com.panosdim.debttrack.model.PersonDebts
import com.panosdim.debttrack.model.Response
import com.panosdim.debttrack.viewmodels.DebtsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheyOweMeScreen() {
    val context = LocalContext.current
    val viewModel: DebtsViewModel = viewModel()
    var debtItems by remember { mutableStateOf(emptyList<PersonDebts>()) }
    val debtItemsResponse =
        viewModel.theyOweMeDebts.collectAsStateWithLifecycle(initialValue = Response.Loading)
    val scope = rememberCoroutineScope()
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    when (debtItemsResponse.value) {
        is Response.Success -> {
            isLoading = false

            debtItems =
                (debtItemsResponse.value as Response.Success<List<PersonDebts>>).data
        }

        is Response.Error -> {
            Toast.makeText(
                context,
                (debtItemsResponse.value as Response.Error).errorMessage,
                Toast.LENGTH_SHORT
            )
                .show()

            isLoading = false
        }

        is Response.Loading -> {
            isLoading = true
        }
    }

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { scope.launch { bottomSheetState.show() } },
                    expanded = expandedFab,
                    icon = { Icon(Icons.Filled.Add, stringResource(id = R.string.add_debt)) },
                    text = { Text(text = stringResource(id = R.string.add_debt)) },
                )
            }

        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentPadding = it,
                state = listState
            ) {
                if (debtItems.isNotEmpty()) {
                    items(debtItems) { item ->
                        Box(contentAlignment = Alignment.TopEnd) {
                            DebtCard(item)
                        }
                    }
                    item { Spacer(modifier = Modifier.padding(bottom = 64.dp)) }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = stringResource(id = R.string.no_one_owes_you),
                                modifier = Modifier

                            )
                            Text(
                                text = stringResource(id = R.string.no_one_owes_you)
                            )
                        }
                    }
                }
            }
            AddDebtSheet(bottomSheetState = bottomSheetState)
        }
    }
}