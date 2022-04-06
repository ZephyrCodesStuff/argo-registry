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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsencesScreen(viewModel: StudentViewModel) {
    val absences = viewModel.absencesModel?.data

    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(absences!!) { _, absence ->
            Card(
                Modifier
                    .fillMaxWidth(0.9F),
                shape = RoundedCornerShape(16.dp),
                containerColor = if (absence.justificationNeeded!! && absence.justificationDate == null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer ,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(16.dp)) {
                    Text(
                        Utils.formatDate(absence.date!!),
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis, maxLines = 1
                    )
                    when (absence.code) {
                        "I" -> Text("Ingresso alle ${absence.hour?.split(" ")?.get(1)}${if (absence.justificationNeeded!! && absence.justificationDate == null) " da giustificare" else ""}", style = MaterialTheme.typography.titleMedium)
                        "U" -> Text("Uscita alle ${absence.hour?.split(" ")?.get(1)}${if (absence.justificationNeeded!! && absence.justificationDate == null) " da giustificare" else ""}", style = MaterialTheme.typography.titleMedium)
                        "A" -> Text("Assenza${if (absence.justificationNeeded!! && absence.justificationDate == null) " da giustificare" else ""}", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        absence.teacher!!.lowercase().replace("(prof. ", "").replace(")", "").capitalizeWords,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}