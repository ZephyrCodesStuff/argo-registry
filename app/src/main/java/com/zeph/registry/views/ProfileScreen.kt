package com.zeph.registry.views

import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zeph.registry.StudentViewModel

@ExperimentalMaterial3Api
@Composable
fun ProfileScreen(viewModel: StudentViewModel, navController: NavController, sharedPreferences: SharedPreferences) {
    val overview = viewModel.overviewModel
    val student = overview?.student
    val scrollState = ScrollState(0)
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            icon = { Icon(Icons.Filled.Warning, contentDescription = "Avvertimento: uscita") },
            title = {
                Text(text = "Sei sicuro?")
            },
            text = {
                Text(
                    "Dovrai effettuare nuovamente l'accesso per poter accedere al tuo profilo. " +
                            "Sei sicuro di voler uscire?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        sharedPreferences.edit().clear().apply()
                        navController.navigate("login") { navController.popBackStack() }
                    }
                ) {
                    Text("Continua")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text("Annulla")
                }
            }
        )
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            Modifier.fillMaxWidth(0.9F),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.tertiary)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Rounded.HealthAndSafety,
                        contentDescription = "Student info",
                        Modifier.size(24.dp)
                    )
                    Text("Dati anagrafici", style = MaterialTheme.typography.titleLarge)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Info("Cognome", student?.lastName.toString())
                    Info("Nome", student?.firstName.toString())
                    Info("Sesso", if (student?.sex.toString() == "M") "Maschio" else if (student?.sex.toString() == "F") "Femmina" else "Non-binario")
                    Info("Codice fiscale", student?.idCode.toString())
                    Info("Cellulare", student?.phone.toString())
                    Info("Cittadinanza", student?.citizenship.toString())
                    Info("Data di nascita", student?.dateOfBirth.toString())
                    Info("Indirizzo di casa", student?.address.toString())
                }
            }
        }
        Card(
            Modifier.fillMaxWidth(0.9F),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.tertiary)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Rounded.School,
                        contentDescription = "School info",
                        Modifier.size(24.dp)
                    )
                    Text("Informazioni scuola", style = MaterialTheme.typography.titleLarge)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Info("Sede", overview?.hq.toString())
                    Info("Scuola", overview?.school.toString())
                    Info("Classe", overview?.year.toString())
                    Info("Sezione", overview?.section.toString())
                }
            }
        }
        Button(onClick = {
            openDialog = true
        }, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("Esci dall'account")
        }
    }
}

@Composable
fun Info(topText: String, bottomText: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            topText,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(bottomText, style = MaterialTheme.typography.bodyMedium)
    }
}