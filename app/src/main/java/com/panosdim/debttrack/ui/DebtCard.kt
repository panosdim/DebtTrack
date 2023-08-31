package com.panosdim.debttrack.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panosdim.debttrack.R
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.model.PersonDebts
import com.panosdim.debttrack.utils.moneyFormat
import com.panosdim.debttrack.utils.toFormattedString
import com.panosdim.debttrack.utils.toLocalDate
import com.panosdim.debttrack.viewmodels.DebtsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtCard(personDebts: PersonDebts) {
    val context = LocalContext.current
    val resources = context.resources
    val viewModel: DebtsViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    var debt: Debt? by rememberSaveable { mutableStateOf(null) }
    val addExtraDebtBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    val openDeleteDialog = remember { mutableStateOf(false) }

    if (openDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDeleteDialog.value = false
            },
            title = {
                Text(text = stringResource(id = R.string.delete_person_debts_dialog_title))
            },
            text = {
                Text(
                    stringResource(id = R.string.delete_person_debts_dialog_description)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDeleteDialog.value = false
                        viewModel.removePersonDebts(personDebts)
                        Toast.makeText(
                            context, R.string.person_delete_toast,
                            Toast.LENGTH_LONG
                        ).show()
                        scope.launch { bottomSheetState.hide() }
                    }
                ) {
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDeleteDialog.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.dismiss))
                }
            }
        )
    }

    Card(
        modifier = Modifier
            // The space between each card and the other
            .padding(4.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = personDebts.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                personDebts.debts.forEach { debtDetails ->
                    ListItem(headlineContent = {
                        Text(
                            text = debtDetails.date.toLocalDate().toFormattedString(),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }, supportingContent = {
                        if (debtDetails.comment.isNotBlank()) {
                            Text(debtDetails.comment)
                        }
                    }, trailingContent = {
                        Text(
                            text = moneyFormat(
                                debtDetails.amount.toFloat()
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }, modifier = Modifier.clickable {
                        debt = Debt(name = personDebts.name, debt = debtDetails)
                        scope.launch { bottomSheetState.show() }
                    })
                    Divider()
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            scope.launch { addExtraDebtBottomSheetState.show() }
                        },
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(id = R.string.add_person_debt))
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch { openDeleteDialog.value = true }
                        },
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(id = R.string.delete_all_person_debts))
                    }
                }

                Text(
                    text = resources.getString(
                        R.string.total,
                        moneyFormat(personDebts.debts.fold(0f) { acc, debtDetails -> acc + debtDetails.amount.toFloat() })
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }

    debt?.let {
        EditDebtSheet(debtItem = it, bottomSheetState = bottomSheetState)
    }
    AddDebtSheet(bottomSheetState = addExtraDebtBottomSheetState, personName = personDebts.name)
}