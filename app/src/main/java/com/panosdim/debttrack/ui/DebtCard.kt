package com.panosdim.debttrack.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.panosdim.debttrack.TAG
import com.panosdim.debttrack.model.Debt
import com.panosdim.debttrack.model.PersonDebts

@Composable
fun DebtCard(personDebts: PersonDebts) {
    Card(
        modifier = Modifier
            // The space between each card and the other
            .padding(4.dp)
            .fillMaxWidth()
            .clickable(onClick = {
//                val intent = Intent(context, ItemDetailsActivity::class.java)
//                val bundle = Bundle()
//                bundle.putParcelable(MSG.ITEM.message, debt)
//                intent.putExtra(MSG.ITEM.message, debt)
//                context.startActivity(intent)
            })
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
                        headlineContent = { Text(debtDetails.date) },
                        supportingContent = { Text(debtDetails.comment) },
                        trailingContent = { Text(debtDetails.amount.toString()) },
                        modifier = Modifier.clickable {
                            val debt = Debt(name = personDebts.name, debt = debtDetails)
                            Log.d(TAG, debt.toString())
                        }
                    )
                    Divider()
                }
            }
        }
    }
}