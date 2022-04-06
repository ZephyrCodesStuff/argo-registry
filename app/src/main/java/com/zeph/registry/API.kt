package com.zeph.registry

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

interface ArgoService {
    @GET("schede")
    suspend fun getOverview(@HeaderMap header: Map<String, String>): Response<List<OverviewModel>>
    @GET("oggi")
    suspend fun getToday(@Query("datGiorno") date: String, @HeaderMap header: Map<String, String>): Response<TodayModel>
    @GET("votigiornalieri")
    suspend fun getGrades(@Query("datGiorno") date: String, @HeaderMap header: Map<String, String>): Response<GradesModel>
    @GET("promemoria")
    suspend fun getReminders(@Query("datGiorno") date: String, @HeaderMap header: Map<String, String>): Response<RemindersModel>
    @GET("assenze")
    suspend fun getAbsences(@Query("datGiorno") date: String, @HeaderMap header: Map<String, String>): Response<AbsencesModel>
    @GET("compiti")
    suspend fun getHomework(@Query("datGiorno") date: String, @HeaderMap header: Map<String, String>): Response<HomeworksModel>
    @GET("login")
    suspend fun getToken(@HeaderMap header: Map<String, String>): Response<LoginModel>
}

data class OverviewModel (
    // Use @SerializedName(" ") for the Gson converter
    // @field:Json(name = " ") for the Moshi converter
    // @SerialName(" ") for the Kotlinx Serialization converter
    // @JsonProperty(" ") for the Jackson converter

    @SerializedName("desSede")
    var hq: String?,
    @SerializedName("desScuola")
    var school: String?,
    @SerializedName("desDenominazione")
    var year: String?,
    @SerializedName("desCorso")
    var section: String?,
    @SerializedName("authToken")
    var authToken: String?,
    @SerializedName("alunno")
    var student: StudentModel,
    @SerializedName("prgAlunno")
    var studentCode: Int,
    @SerializedName("prgScuola")
    var schoolCode: Int,
    @SerializedName("prgScheda")
    var sheetCode: Int,
    @SerializedName("prgClasse")
    var classCode: Int,
)

data class TodayModel (
    @SerializedName("nuoviElementi")
    var newElements: Number?,
    @SerializedName("dati")
    var data: List<EventModel>?,
)

data class GradesModel (
    @SerializedName("dati")
    var data: List<GradeModel>?,
)

data class RemindersModel (
    @SerializedName("dati")
    var data: List<ReminderModel>?,
)

data class ReminderModel (
    @SerializedName("desAnnotazioni")
    var description: String?,
    @SerializedName("desMittente")
    var teacher: String?,
    @SerializedName("datGiorno")
    var date: String?,
)

data class GradeModel (
    @SerializedName("prgMateria")
    var id: Number?,
    @SerializedName("desProva")
    var description: String?,
    @SerializedName("decValore")
    var value: Float?,
    @SerializedName("docente")
    var teacher: String?,
    @SerializedName("desMateria")
    var subject: String?,
    @SerializedName("desCommento")
    var comment: String?,
    @SerializedName("datGiorno")
    var date: String?,
)

data class EventModel (
    @SerializedName("ordine")
    var order: Number?,
    @SerializedName("tipo")
    var type: String?,
    @SerializedName("titolo")
    var title: String?,
    @SerializedName("giorno")
    var date: String?,
    @SerializedName("dati")
    var data: DataModel?,
)

data class DataModel (
    @SerializedName("datCompitiPresente")
    var homeworkPresent: Boolean?,
    @SerializedName("docente")
    var teacher: String?,
    @SerializedName("desCompiti")
    var description: String?,
    @SerializedName("desMateria")
    var subject: String?,
    @SerializedName("datCompiti")
    var date: String?,
    @SerializedName("desArgomento")
    var topic: String?,
    @SerializedName("desMittente")
    var sender: String?
)

data class StudentModel (
    @SerializedName("flgSesso")
    var sex: String?,
    @SerializedName("desCognome")
    var lastName: String?,
    @SerializedName("desNome")
    var firstName: String?,
    @SerializedName("desCittadinanza")
    var citizenship: String?,
    @SerializedName("desCf")
    var idCode: String?,
    @SerializedName("desCellulare")
    var phone: String?,
    @SerializedName("datNascita")
    var dateOfBirth: String?,
    @SerializedName("desVia")
    var address: String?,
)

data class AbsencesModel (
    @SerializedName("dati")
    var data: List<AbsenceModel>?
)

data class AbsenceModel (
    @SerializedName("numOra")
    var hourIndex: Number?,
    @SerializedName("datAssenza")
    var date: String?,
    @SerializedName("oraAssenza")
    var hour: String?,
    @SerializedName("registrataDa")
    var teacher: String?,
    @SerializedName("flgDaGiustificare")
    var justificationNeeded: Boolean?,
    @SerializedName("desAssenza")
    var description: String?,
    @SerializedName("codEvento")
    var code: String?,
    @SerializedName("datGiustificazione")
    var justificationDate: String?,
)

data class HomeworksModel (
    @SerializedName("dati")
    var data: List<HomeworkModel>?
)

data class HomeworkModel (
    @SerializedName("datGiorno")
    var assignedDate: String?,
    @SerializedName("datCompiti")
    var dueDate: String?,
    @SerializedName("desMateria")
    var subject: String?,
    @SerializedName("desCompiti")
    var description: String?,
    @SerializedName("docente")
    var teacher: String?
)

data class LoginModel (
    @SerializedName("token")
    var token: String?,
    @SerializedName("tipoUtente")
    var userType: String?,
)

class StudentViewModel : ViewModel() {
    var overviewModel: OverviewModel? by mutableStateOf(null)
    var todayModel: TodayModel? by mutableStateOf(null)
    var gradesModel: GradesModel? by mutableStateOf(null)
    var remindersModel: RemindersModel? by mutableStateOf(null)
    var absencesModel: AbsencesModel? by mutableStateOf(null)
    var homeworkModel: HomeworksModel? by mutableStateOf(null)
    var loginModel: LoginModel? by mutableStateOf(null)

    // Do an asynchronous operation to fetch users.
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.portaleargo.it/famiglia/api/rest/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ArgoService = retrofit.create(ArgoService::class.java)

    fun loadUser(date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                 sharedPreferences: SharedPreferences,
                 navController: NavController) {
        val token: String? = sharedPreferences.getString("token", "")
        val student: Int = sharedPreferences.getInt("student", 0)
        val sheet: Int = sharedPreferences.getInt("sheet", 0)
        val school: Int = sharedPreferences.getInt("school", 0)
        val headers = HashMap<String, String>()
        headers["x-key-app"] = "ax6542sdru3217t4eesd9"
        headers["x-version"] = "2.1.0"
        headers["x-produttore-software"] = "ARGO Software s.r.l. - Ragusa"
        headers["x-app-code"] = "APF"
        headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"
        headers["x-cod-min"] = "sg25565"
        headers["x-auth-token"] = token!!
        headers["x-prg-alunno"] = student.toString()
        headers["x-prg-scheda"] = sheet.toString()
        headers["x-prg-scuola"] = school.toString()

        CoroutineScope(Dispatchers.Default).launch {
            val overview = service.getOverview(headers)
            val today = service.getToday(date, headers)
            val grades = service.getGrades(date, headers)
            val reminders = service.getReminders(date, headers)
            val absences = service.getAbsences(date, headers)
            val homework = service.getHomework(date, headers)

            withContext(Dispatchers.Main) {
                if (overview.isSuccessful) overviewModel = overview.body()!![0]
                if (today.isSuccessful) todayModel = today.body()
                if (grades.isSuccessful) gradesModel = grades.body()
                if (reminders.isSuccessful) remindersModel = reminders.body()
                if (absences.isSuccessful) absencesModel = absences.body()
                if (homework.isSuccessful) homeworkModel = homework.body()

                // If all are successful
                if (overviewModel != null && todayModel != null && gradesModel != null && remindersModel != null && absencesModel != null && homeworkModel != null) {
                    navController.navigate("home") { navController.popBackStack() }
                }
            }
        }
    }

    fun login(
        code: String,
        user: String,
        password: String,
        sharedPreferences: SharedPreferences,
        navController: NavController
    ) {
        val headers = HashMap<String, String>()
        headers["x-key-app"] = "ax6542sdru3217t4eesd9"
        headers["x-version"] = "2.1.0"
        headers["x-produttore-software"] = "ARGO Software s.r.l. - Ragusa"
        headers["x-app-code"] = "APF"
        headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"
        headers["x-cod-min"] = code
        headers["x-user-id"] = user
        headers["x-pwd"] = password

        CoroutineScope(Dispatchers.Default).launch {
            val token = service.getToken(headers)

            withContext(Dispatchers.Main) { if (token.isSuccessful)  {
                if (token.body()?.userType != "A") {
                    Toast.makeText(navController.context, "L'applicazione è stata sviluppata per gli studenti.", Toast.LENGTH_SHORT).show()
                    return@withContext
                }
                loginModel = token.body()
                headers["x-auth-token"] = token.body()!!.token!!
                overviewModel = service.getOverview(headers).body()?.get(0)
                sharedPreferences.edit()
                    .putString("token", loginModel?.token)
                    .putInt("student", overviewModel?.studentCode!!)
                    .putInt("sheet", overviewModel?.sheetCode!!)
                    .putInt("school", overviewModel?.schoolCode!!)
                .apply()

                loadUser(sharedPreferences = sharedPreferences, navController = navController)
            } else {
                if (token.code() == 401) {
                    Toast.makeText(navController.context, "Credenziali non corrette.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(navController.context, "Login fallito. Le credenziali non sono il problema.", Toast.LENGTH_SHORT).show()
                }
            }}
        }
    }
}
