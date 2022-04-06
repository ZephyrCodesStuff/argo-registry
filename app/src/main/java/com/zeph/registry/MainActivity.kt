package com.zeph.registry

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zeph.registry.ui.theme.RegistryTheme
import com.zeph.registry.views.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class MainActivity : ComponentActivity() {
    private lateinit var viewModel : StudentViewModel

    sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen_route: String){
        object Home : BottomNavItem("Home", Icons.Default.Home, "home")
        object Grades : BottomNavItem("Voti", Icons.Default.Score, "grades")
        object Reminders : BottomNavItem("Promemoria", Icons.Default.FormatListBulleted, "reminders")
        object Absences : BottomNavItem("Assenze, ritardi e uscite", Icons.Default.AssignmentInd, "absences")
        object Homework: BottomNavItem("Compiti", Icons.Default.Backpack, "homework")
    }

    private val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Grades,
        BottomNavItem.Reminders,
        BottomNavItem.Absences,
        BottomNavItem.Homework
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("com.zeph.registry.PREFERENCE_FILE_KEY", MODE_PRIVATE)
        viewModel = ViewModelProvider(this).get(StudentViewModel::class.java)

        setContent {
            val navController = rememberNavController()

            RegistryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val selectedItem = remember { mutableStateOf(items[0]) }
                    val navBackStackRoute by navController.currentBackStackEntryAsState()
                    val overview = viewModel.overviewModel
                    if (navBackStackRoute?.destination?.route != "login") {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            gesturesEnabled = navBackStackRoute?.destination?.route != "profile",
                            drawerContent = {
                                Column {
                                    Text(viewModel.overviewModel?.student?.firstName?.capitalizeWords ?: "Registro", modifier = Modifier.padding(top = 16.dp, start = 16.dp), style = MaterialTheme.typography.headlineMedium)
                                    Text("${overview?.year}${overview?.section} @ ${overview?.school?.capitalizeWords?.trim()}", modifier = Modifier.padding(top = 4.dp, start = 16.dp))
                                    Text(LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                                        .toString(), Modifier.padding(start = 16.dp, bottom = 16.dp, top = 4.dp), style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    items.forEach { item ->
                                        NavigationDrawerItem(
                                            icon = { Icon(item.icon, contentDescription = null) },
                                            label = { Text(item.title) },
                                            selected = item == selectedItem.value,
                                            onClick = {
                                                navController.navigate(item.screen_route) { navController.popBackStack() }
                                                scope.launch { drawerState.close() }
                                                selectedItem.value = item
                                            },
                                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                        )
                                        if (item.screen_route == "home") {
                                            Divider(thickness = 1.dp, modifier = Modifier.padding(8.dp))
                                        }
                                    }
                                }
                            },
                            content = {
                                Scaffold(
                                    topBar = {
                                        LargeTopAppBar(
                                            title = {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    with (navBackStackRoute?.destination?.route) {
                                                        when {
                                                            equals("home") -> {
                                                                Text("Bentornato")
                                                                DateTitle()
                                                            }
                                                            equals("grades") -> {
                                                                Text("I tuoi voti")
                                                                var mid = 0.00F
                                                                viewModel.gradesModel?.data?.forEach {
                                                                    mid += it.value!!
                                                                }
                                                                mid /= viewModel.gradesModel?.data?.size!!
                                                                Text("La tua media complessiva è di ${String.format("%.2f", mid)}",
                                                                    style = MaterialTheme.typography.titleMedium)
                                                            }
                                                            equals("reminders") -> {
                                                                Text("Promemoria")
                                                                DateTitle()
                                                            }
                                                            equals("profile") -> {
                                                                Text("Profilo")
                                                            }
                                                            equals("absences") -> {
                                                                Text("Assenze, ritardi e uscite")
                                                                val events = viewModel.absencesModel?.data
                                                                if (events.isNullOrEmpty()) {
                                                                    Text("Non ci sono eventi da mostrare", style = MaterialTheme.typography.titleMedium)
                                                                } else {
                                                                    val textList = mutableListOf("")
                                                                    if (events.any { it.code == "A" }) {
                                                                        events.filter { it.code == "A" }.also {
                                                                            textList.add("${it.size} assenz${if (it.size == 1) "a" else "e"}")
                                                                        }
                                                                    }
                                                                    if (events.any { it.code == "I" }) {
                                                                        events.filter { it.code == "I" }.also {
                                                                            textList.add("${it.size} ingress${if (it.size == 1) "o" else "i"}")
                                                                        }
                                                                    }
                                                                    if (events.any { it.code == "U" }) {
                                                                        events.filter { it.code == "U" }.also {
                                                                            textList.add("${it.size} uscit${if (it.size == 1) "a" else "e"}")
                                                                        }
                                                                    }
                                                                    textList.removeFirst()
                                                                    Text(textList.joinToString(", "), style = MaterialTheme.typography.titleMedium)
                                                                }
                                                            }
                                                            equals("homework") -> {
                                                                Text("Compiti")
                                                                val events = viewModel.homeworkModel?.data?.filter {
                                                                    LocalDate.parse(it.dueDate, DateTimeFormatter.ISO_LOCAL_DATE) > LocalDate.now()
                                                                }
                                                                if (events.isNullOrEmpty()) {
                                                                    Text("Non ci sono compiti da svolgere", style = MaterialTheme.typography.titleMedium)
                                                                } else {
                                                                    Text("Ci sono ${events.size} compiti da svolgere", style = MaterialTheme.typography.titleMedium)
                                                                }
                                                            }
                                                            equals("login") -> {
                                                                Text("Benvenuto!")
                                                            }
                                                            this?.startsWith("subject") == true -> Text(
                                                                viewModel.gradesModel?.data!!.first {
                                                                    it.id.toString() == navBackStackRoute?.arguments?.getInt("id").toString()
                                                                }.subject!!.lowercase().replaceFirstChar { it.uppercaseChar() },
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            navigationIcon = {
                                                if (navBackStackRoute?.destination?.route != "profile") {
                                                    IconButton(
                                                        onClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } }
                                                    ) {
                                                        Icon(Icons.Filled.Menu, contentDescription = "Open navigation drawer")
                                                    }
                                                } else {
                                                    IconButton(
                                                        onClick = { navController.navigateUp() }
                                                    ) {
                                                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                                                    }
                                                }
                                            },
                                            actions = {
                                                if (navBackStackRoute?.destination?.route != "profile") {
                                                    IconButton(
                                                        onClick = { navController.navigate("profile") { navController.popBackStack("home", false) } }
                                                    ) {
                                                        Icon(Icons.Filled.Person, contentDescription = "Navigate to profile")
                                                    }
                                                }
                                            }
                                        )
                                    }
                                ) {
                                    NavigationView(navController, viewModel, selectedItem, sharedPrefs)
                                }
                            }
                        )
                    } else {
                        NavigationView(navController, viewModel, selectedItem, sharedPrefs)
                    }
                }
            }
        }
    }

    @Composable
    fun DateTitle() {
        Text(LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)).toString(),
            style = MaterialTheme.typography.titleMedium
        )
    }

    @ExperimentalMaterial3Api
    @Composable
    fun NavigationView(
        navController: NavHostController,
        viewModel: StudentViewModel,
        selectedItem: MutableState<BottomNavItem>,
        sharedPreferences: SharedPreferences,
    ) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginScreen(viewModel, sharedPreferences, navController) }
            composable("home") { HomeScreen(viewModel, navController); selectedItem.value = items[0] }
            composable("grades") { SubjectsScreen(viewModel, navController); selectedItem.value = items[1] }
            composable("reminders") { RemindersScreen(viewModel); selectedItem.value = items[2] }
            composable("profile") { ProfileScreen(viewModel, navController, sharedPreferences) }
            composable("subject/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) {
                GradesScreen(viewModel = viewModel, subject = it.arguments?.getInt("id")!!)
            }
            composable("absences") { AbsencesScreen(viewModel); selectedItem.value = items[3] }
            composable("homework") { HomeworkScreen(viewModel); selectedItem.value = items[4] }
        }
    }
}