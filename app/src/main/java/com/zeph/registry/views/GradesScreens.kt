package com.zeph.registry.views

import androidx.compose.foundation.clickable
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
import androidx.navigation.NavHostController
import com.zeph.registry.StudentViewModel
import com.zeph.registry.Utils

@ExperimentalMaterial3Api
@Composable
fun SubjectsScreen(viewModel: StudentViewModel, navController: NavHostController) {
    val grades = viewModel.gradesModel
    val gradesSplit = grades?.data?.groupBy { it.subject }

    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(gradesSplit!!.toList()) { _, subjects ->
            var mid = 0F
            subjects.second.forEach { mid += it.value!! }
            mid /= subjects.second.size
            Card(
                Modifier
                    .fillMaxWidth(0.9F)
                    .clickable {
                        navController.navigate("subject/${subjects.second.first().id}")
                    },
                shape = RoundedCornerShape(16.dp),
                containerColor = if (mid >= 6) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(16.dp)) {
                    Text(
                        subjects.first!!.lowercase().replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis, maxLines = 1
                    )
                    Text("Media: ${String.format("%.2f", mid)} con ${subjects.second.size} vot${if (subjects.second.size == 1) "o" else "i"}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun GradesScreen(viewModel: StudentViewModel, subject: Int) {
    val grades = viewModel.gradesModel
    val gradesFiltered = grades?.data?.filter { it.id.toString() == subject.toString() }
    
    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(gradesFiltered!!) { _, grade ->
            Card(
                Modifier.fillMaxWidth(0.9F),
                shape = RoundedCornerShape(16.dp),
                containerColor = if (grade.value!! >= 6) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(grade.value!!.toString(), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.padding(end = 16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            if (grade.description != "") grade.description!!.lowercase().replaceFirstChar { it.uppercaseChar() } else "Nessuna descrizione",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(Utils.formatDate(grade.date!!), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}