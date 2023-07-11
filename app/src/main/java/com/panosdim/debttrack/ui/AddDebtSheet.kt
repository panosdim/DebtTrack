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
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import com.panosdim.debttrack.model.DebtDetails
import com.panosdim.debttrack.utils.currencyRegex
import com.panosdim.debttrack.utils.toEpochMilli
import com.panosdim.debttrack.utils.toLocalDate
import com.panosdim.debttrack.viewmodels.DebtsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtSheet(
    bottomSheetState: SheetState
) {
    val context = LocalContext.current
    val viewModel: DebtsViewModel = viewModel()
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Sheet content
    if (bottomSheetState.isVisible) {
        var debtName by remember { mutableStateOf("") }
        var debtComment by remember { mutableStateOf("") }
        var debtAmount by remember { mutableStateOf("") }
        val debtDate by remember { mutableStateOf(LocalDate.now()) }
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
                    label = { Text(stringResource(id = R.string.to_whom_do_i_owe)) },
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
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(
                        enabled = isFormValid(),
                        onClick = {

                            datePickerState.selectedDateMillis?.toLocalDate()?.let {
                                val newItem = Debt(
                                    name = debtName,
                                    debt = DebtDetails(
                                        amount = debtAmount,
                                        date = it.toString(),
                                        comment = debtComment
                                    )
                                )

                                viewModel.addDebt(newItem)
                                Toast.makeText(
                                    context, R.string.create_toast,
                                    Toast.LENGTH_LONG
                                ).show()

                                scope.launch { bottomSheetState.hide() }
                            }
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