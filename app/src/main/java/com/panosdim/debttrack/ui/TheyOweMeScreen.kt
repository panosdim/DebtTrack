package com.panosdim.debttrack.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panosdim.debttrack.R
import com.panosdim.debttrack.viewmodels.DebtsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheyOweMeScreen() {
    val viewModel: DebtsViewModel = viewModel()
    val debtItems =
        viewModel.debts.collectAsStateWithLifecycle(initialValue = emptyList())
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
            if (debtItems.value.isNotEmpty()) {
                items(debtItems.value) { item ->
                    Box(contentAlignment = Alignment.TopEnd) {
                        DebtCard(item)
                    }
                }
                item { Spacer(modifier = Modifier.padding(bottom = 64.dp)) }
            } else {
                item {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(id = R.drawable.empty_list),
                        contentDescription = stringResource(id = R.string.empty_list)
                    )
                }
            }
        }
        AddDebtSheet(bottomSheetState = bottomSheetState)
    }
}