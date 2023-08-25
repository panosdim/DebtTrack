package com.panosdim.debttrack.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.panosdim.debttrack.R
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.model.PersonDebts
import com.panosdim.debttrack.utils.moneyFormat
import com.panosdim.debttrack.utils.toFormattedString
import com.panosdim.debttrack.utils.toLocalDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtCard(personDebts: PersonDebts) {
    val context = LocalContext.current
    val resources = context.resources
    val scope = rememberCoroutineScope()
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    var debt: Debt? by rememberSaveable { mutableStateOf(null) }

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
                    ListItem(
                        headlineContent = {
                            Text(
                                text = debtDetails.date.toLocalDate().toFormattedString(),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        },
                        supportingContent = {
                            if (debtDetails.comment.isNotBlank()) {
                                Text(debtDetails.comment)
                            }
                        },
                        trailingContent = {
                            Text(
                                text = moneyFormat(
                                    debtDetails.amount.toFloat()
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        },
                        modifier = Modifier.clickable {
                            debt = Debt(name = personDebts.name, debt = debtDetails)
                            scope.launch { bottomSheetState.show() }
                        }
                    )
                    Divider()
                }

                Text(
                    text = resources.getString(
                        R.string.total,
                        moneyFormat(personDebts.debts.fold(0f) { acc, debtDetails -> acc + debtDetails.amount.toFloat() })
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }

    debt?.let { EditDebtSheet(debtItem = it, bottomSheetState = bottomSheetState) }
}