package com.zeph.registry.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zeph.registry.*
import java.time.LocalDate

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(viewModel: StudentViewModel, navController: NavHostController) {
    val gradesScrollState = ScrollState(0)
    val remindersScrollState = ScrollState(0)
    val verticalScrollState = ScrollState(0)

    val today = viewModel.todayModel
    val grades = viewModel.gradesModel
    val reminders = viewModel.remindersModel
    val topics = today?.data?.filter { it.type == "ARG" }
    val absences = viewModel.absencesModel
    val unjustifiedEvents = absences?.data?.filter { it.justificationNeeded!! && it.justificationDate == null }
    val homework = viewModel.homeworkModel
    val dueHomework = homework?.data?.filter { LocalDate.parse(it.dueDate) > LocalDate.now() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState, true),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Absences warning
            if (unjustifiedEvents?.isNotEmpty() == true) {
                Card(
                    Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .clickable { navController.navigate("absences") },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WarningAmber, "Assenze non giustificate!")
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val textList = mutableListOf("")
                            if (unjustifiedEvents.any { it.code == "A" }) {
                                unjustifiedEvents.filter { it.code == "A" }.also {
                                    textList.add("${it.size} assenz${if (it.size == 1) "a" else "e"}")
                                }
                            }
                            if (unjustifiedEvents.any { it.code == "I" }) {
                                unjustifiedEvents.filter { it.code == "I" }.also {
                                    textList.add("${it.size} ingress${if (it.size == 1) "o" else "i"}")
                                }
                            }
                            if (unjustifiedEvents.any { it.code == "U" }) {
                                unjustifiedEvents.filter { it.code == "U" }.also {
                                    textList.add("${it.size} uscit${if (it.size == 1) "a" else "e"}")
                                }
                            }
                            textList.removeFirst()
                            Text("Hai ${unjustifiedEvents.size} eventi non giustificati!", style = MaterialTheme.typography.titleMedium)
                            Text(textList.joinToString(", "), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            // Homework
            if (dueHomework?.isNotEmpty() == true) {
                Card(
                    Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .clickable { navController.navigate("homework") },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, "Compiti da svolgere")
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Ci sono ${dueHomework.size} compiti da svolgere", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
        if (unjustifiedEvents?.isNotEmpty() == true || dueHomework?.isNotEmpty() == true) {
            Divider(Modifier.padding(start = 16.dp, end = 16.dp), MaterialTheme.colorScheme.onBackground, 1.dp)
        }
        // Grades
        if (grades?.data != null) {
            Column {
                Text(
                    text = "Ultimi voti",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 16.dp)
                )
                Row(
                    Modifier.horizontalScroll(gradesScrollState, true),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    Spacer(Modifier.size(8.dp, 0.dp))
                    for (grade: GradeModel in grades.data!!.subList(0, if (grades.data!!.size >= 5) 4 else grades.data!!.size)) {
                        Card(
                            Modifier
                                .size(width = 275.dp, height = 175.dp)
                                .clickable {
                                    navController.navigate("subject/${grade.id}")
                                },
                            shape = RoundedCornerShape(16.dp),
                            containerColor = if (grade.value!! >= 6) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        ) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        grade.subject!!.lowercase()
                                            .replaceFirstChar { it.uppercaseChar() },
                                        style = MaterialTheme.typography.titleLarge,
                                        overflow = TextOverflow.Ellipsis, maxLines = 1
                                    )
                                    Text(
                                        grade.teacher!!.lowercase().replace("(prof. ", "")
                                            .replace(")", "").capitalizeWords,
                                        style = MaterialTheme.typography.bodyMedium,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                    Text(Utils.formatDate(grade.date!!), style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(
                                    if (grade.description!! != "") grade.description!!.lowercase()
                                        .replaceFirstChar { it.uppercaseChar() } else
                                        "Nessuna descrizione",
                                    style = MaterialTheme.typography.titleSmall,
                                    overflow = TextOverflow.Ellipsis, maxLines = 1,
                                    fontStyle = if (grade.description!! != "") FontStyle.Normal else FontStyle.Italic)
                                Text(
                                    grade.value!!.toString(),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                    Spacer(Modifier.size(8.dp, 0.dp))
                }
            }
        }
        // Reminders
        if (reminders?.data != null) {
            Column {
                Text(
                    text = "Ultimi promemoria",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    Modifier.horizontalScroll(remindersScrollState, true),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    Spacer(Modifier.size(8.dp, 0.dp))
                    for (reminder: ReminderModel in reminders.data!!.subList(0, if (reminders.data!!.size >= 5) 4 else reminders.data!!.size)) {
                        Card(
                            Modifier.size(width = 275.dp, height = 160.dp),
                            shape = RoundedCornerShape(16.dp),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                                    Text(
                                        Utils.formatDate(reminder.date!!),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    reminder.description!!.lowercase()
                                        .replaceFirstChar { it.uppercaseChar() },
                                    style = MaterialTheme.typography.titleSmall,
                                    overflow = TextOverflow.Ellipsis, maxLines = 4,
                                    fontStyle = FontStyle.Normal
                                )
                            }
                        }
                    }
                    Spacer(Modifier.size(8.dp, 0.dp))
                }
            }
        }
        // Topics
        if (topics != null && topics.isNotEmpty()) {
            Column {
                Text(
                    text = "Argomenti di oggi",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
                {
                    Spacer(Modifier.size(8.dp, 0.dp))
                    for (reminder: EventModel in topics.subList(0, if (topics.size >= 5) 4 else topics.size)) {
                        Card(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        ) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    reminder.data?.teacher!!.lowercase().replace("(prof. ", "")
                                        .replace(")", "").capitalizeWords,
                                    style = MaterialTheme.typography.titleMedium,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Text(
                                    reminder.data?.topic!!.lowercase()
                                        .replaceFirstChar { it.uppercaseChar() },
                                    style = MaterialTheme.typography.bodyMedium,
                                    overflow = TextOverflow.Ellipsis, maxLines = 4,
                                    fontStyle = FontStyle.Normal
                                )
                            }
                        }
                    }
                    Spacer(Modifier.size(8.dp, 0.dp))
                }
            }
        }
        Spacer(Modifier.size(16.dp))
    }
}