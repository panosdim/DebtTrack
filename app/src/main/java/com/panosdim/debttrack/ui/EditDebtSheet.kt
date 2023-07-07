package com.panosdim.debttrack.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panosdim.debttrack.R
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.utils.toEpochMilli
import com.panosdim.debttrack.utils.toLocalDate
import com.panosdim.debttrack.viewmodels.TheyOweMe
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDebtSheet(
    debtItem: Debt,
    bottomSheetState: SheetState
) {
    val context = LocalContext.current
    val viewModel: TheyOweMe = viewModel()
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currencyRegex =
        "([1-9][0-9]*(\\.[0-9]{0,2})?|0(\\.[0-9]{0,2})?|(\\.[0-9]{1,2})?)"

    val openDeleteDialog = remember { mutableStateOf(false) }

    if (openDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDeleteDialog.value = false
            },
            title = {
                Text(text = stringResource(id = R.string.delete_debt_dialog_title))
            },
            text = {
                Text(
                    stringResource(id = R.string.delete_debt_dialog_description)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDeleteDialog.value = false
                        viewModel.removeDebt(debtItem)
                        Toast.makeText(
                            context, R.string.delete_toast,
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

    // Sheet content
    if (bottomSheetState.isVisible) {
        var debtName by remember { mutableStateOf(debtItem.name) }
        var debtComment by remember { mutableStateOf(debtItem.debt.comment) }
        var debtAmount by remember { mutableStateOf(debtItem.debt.amount) }
        val debtDate by remember { mutableStateOf(LocalDate.parse(debtItem.debt.date)) }
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = debtDate.toEpochMilli())

        fun isFormValid(): Boolean {
            return debtName.isNotBlank() && debtAmount.isNotBlank()
        }

        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets

        ModalBottomSheet(
            onDismissRequest = { scope.launch { bottomSheetState.hide() } },
            sheetState = bottomSheetState,
            windowInsets = windowInsets
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                OutlinedTextField(
                    value = debtName,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization =
                        KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    isError = !isFormValid(),
                    onValueChange = { debtName = it },
                    label = { Text(stringResource(id = R.string.who_owes_me)) },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = debtAmount,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.EuroSymbol,
                            contentDescription = "Euro Icon"
                        )
                    },
                    isError = !isFormValid(),
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex(currencyRegex))) {
                            debtAmount = newValue
                        }
                    },
                    label = { Text(stringResource(id = R.string.amount)) },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = debtComment,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization =
                        KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    onValueChange = { debtComment = it },
                    label = { Text(stringResource(id = R.string.comment)) },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )

                OutlinedDatePicker(
                    state = datePickerState,
                    label = stringResource(id = R.string.date)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    OutlinedButton(
                        onClick = { openDeleteDialog.value = true },
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(id = R.string.delete))
                    }
                    Button(
                        enabled = isFormValid(),
                        onClick = {
                            debtItem.name = debtName
                            debtItem.debt.amount = debtAmount
                            debtItem.debt.comment = debtComment
                            datePickerState.selectedDateMillis?.toLocalDate()?.let {
                                debtItem.debt.date = it.toString()
                            }

                            viewModel.updateDebt(debtItem)

                            Toast.makeText(
                                context, R.string.update_toast,
                                Toast.LENGTH_LONG
                            ).show()

                            scope.launch { bottomSheetState.hide() }
                        },
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(id = R.string.update))
                    }
                }
            }
        }
    }
}