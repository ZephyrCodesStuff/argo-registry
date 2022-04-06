package com.zeph.registry.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeph.registry.StudentViewModel
import com.zeph.registry.Utils
import com.zeph.registry.capitalizeWords
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalMaterial3Api
@Composable
fun RemindersScreen(viewModel: StudentViewModel) {
    val reminders = viewModel.remindersModel?.data

    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(reminders!!) { _, reminder ->
            Card(
                Modifier
                    .fillMaxWidth(0.9F),
                shape = RoundedCornerShape(16.dp),
                containerColor = if (LocalDate.parse(reminder.date, DateTimeFormatter.ISO_LOCAL_DATE) > LocalDate.now()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            reminder.teacher!!.lowercase().replace("(prof. ", "")
                                .replace(")", "").capitalizeWords,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(Utils.formatDate(reminder.date!!), style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        reminder.description!!.lowercase().replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}