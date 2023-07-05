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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.panosdim.debttrack.utils.CurrencyVisualTransformation
import com.panosdim.debttrack.utils.toEpochMilli
import com.panosdim.debttrack.viewmodels.TheyOweMe
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtSheet(
    debtItem: Debt?,
    bottomSheetState: SheetState
) {
    val context = LocalContext.current
    val viewModel: TheyOweMe = viewModel()
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var debtName by rememberSaveable {
        debtItem?.let {
            return@rememberSaveable mutableStateOf(it.name)
        } ?: run {
            return@rememberSaveable mutableStateOf("")
        }
    }
    var debtComment by rememberSaveable {
        debtItem?.let {
            return@rememberSaveable mutableStateOf(it.comment)
        } ?: run {
            return@rememberSaveable mutableStateOf("")
        }
    }
    var debtAmount by rememberSaveable {
        debtItem?.let {
            return@rememberSaveable mutableStateOf(it.amount.toString())
        } ?: run {
            return@rememberSaveable mutableStateOf("")
        }
    }
    val debtDate by rememberSaveable {
        debtItem?.let {
            return@rememberSaveable mutableStateOf(LocalDate.parse(it.date))
        } ?: run {
            return@rememberSaveable mutableStateOf(LocalDate.now())
        }
    }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = debtDate.toEpochMilli())

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
                        if (debtItem != null) {
                            viewModel.removeDebt(debtItem)
                        }
                        Toast.makeText(
                            context, R.string.delete_toast,
                            Toast.LENGTH_LONG
                        ).show()
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

    fun isFormValid(): Boolean {
        return debtName.isNotBlank() && debtAmount.isNotBlank()
    }

    // Sheet content
    if (bottomSheetState.isVisible) {
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
                    onValueChange = {
                        debtAmount = if (it.startsWith("0")) {
                            ""
                        } else {
                            it
                        }
                    },
                    visualTransformation = CurrencyVisualTransformation(
                        fixedCursorAtTheEnd = true
                    ),
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

                if (debtItem != null) {
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
//                                maintenanceItem.name = itemName
//                                maintenanceItem.periodicity = itemPeriodicity.toInt()
//                                datePickerState.selectedDateMillis?.toLocalDate()?.let {
//                                    maintenanceItem.date = it.toString()
//                                }
//
//                                maintenanceItem.eventID?.let { eventId ->
//                                    datePickerState.selectedDateMillis?.toLocalDate()?.let { date ->
//                                        val eventDate = date.plusMonths(itemPeriodicity.toLong())
//                                        updateEvent(context, eventId, eventDate, itemName)
//                                    }
//                                } ?: kotlin.run {
//                                    datePickerState.selectedDateMillis?.toLocalDate()?.let { date ->
//                                        val eventDate = date.plusMonths(itemPeriodicity.toLong())
//                                        maintenanceItem.eventID =
//                                            insertEvent(context, eventDate, itemName)
//                                    }
//                                }

                                viewModel.updateDebt(debtItem)

                                Toast.makeText(
                                    context, R.string.update_toast,
                                    Toast.LENGTH_LONG
                                ).show()
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
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Button(
                            enabled = isFormValid(),
                            onClick = {
//                                val newItem =
//                                    datePickerState.selectedDateMillis?.toLocalDate()?.let {
//                                        val eventDate = it.plusMonths(itemPeriodicity.toLong())
//                                        Item(
//                                            name = itemName,
//                                            periodicity = itemPeriodicity.toInt(),
//                                            date = it.toString(),
//                                            eventID = insertEvent(context, eventDate, itemName)
//                                        )
//                                    }
//
//                                if (newItem != null) {
//                                    viewModel.addNewItem(newItem)
//                                    Toast.makeText(
//                                        context, "Item Saved Successfully.",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                } else {
//                                    Toast.makeText(
//                                        context, "Failed to save new item.",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                }
//
//                                activity?.finish()
                            },
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(stringResource(id = R.string.create))
                        }
                    }
                }
            }
        }
    }
}