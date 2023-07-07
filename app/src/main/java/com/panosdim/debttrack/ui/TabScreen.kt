package com.panosdim.debttrack.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.panosdim.debttrack.utils.TabNames

@Composable
fun TabScreen() {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = enumValues<TabNames>().map { it.tabName }

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    icon = {
                        when (index) {
                            0 -> Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null
                            )

                            1 -> Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
        when (tabIndex) {
            0 -> TheyOweMeScreen()
            1 -> IOweScreen()
        }
    }
}