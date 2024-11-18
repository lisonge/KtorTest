package ktor.test

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.Coil
import com.hjq.toast.Toaster
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ktor.test.ui.theme.KtorTestTheme
import kotlin.getValue


class MainActivity : ComponentActivity() {
    val runtimeFalse by lazy { packageName.length < 3 }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (runtimeFalse) {
                Coil.imageLoader(this)
                Toaster.init(this.application)
                val navController = rememberNavController()
                DestinationsNavHost(
                    navController = navController,
                    navGraph = NavGraphs.root
                )
            }
            KtorTestTheme {
                AppPage()
            }
        }
    }
}

val appScope = MainScope()

@Destination<RootGraph>(start = true)
@Composable
fun HomePage() {
    Text("Hello World")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPage() {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = {
                    Text(text = "Ktor Test")
                }
            )
        }
    ) { contentPadding ->
        val server = serverFlow.collectAsState().value
        val expectation = expectationFlow.collectAsState().value
        val checked = server != null
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ktor Server", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        appScope.launch(Dispatchers.IO) {
                            try {
                                serverFlow.value?.stop(1000, 1000)
                                expectationFlow.value = null
                                if (checked) {
                                    serverFlow.value = null
                                } else {
                                    val newServer = createServer()
                                    serverFlow.value = newServer
                                    newServer.start()
                                }
                            } catch (e: Exception) {
                                serverFlow.value = null
                                expectationFlow.value = e
                            }
                        }
                    },
                )
            }
            if (expectation != null) {
                val errorText = remember(expectation) { expectation.stackTraceToString() }
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = errorText, color = MaterialTheme.colorScheme.error
                )
            }
            if (checked) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(text = "Server is running")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier.clickable {
                            context.openUri(SERVER_HTTP_URL)
                        },
                        text = SERVER_HTTP_URL,
                        color = MaterialTheme.colorScheme.secondary,
                        style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline)
                    )
                }
            } else {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Server is stopped"
                )
            }
        }
    }
}

val expectationFlow = MutableStateFlow<Exception?>(null)
val serverFlow =
    MutableStateFlow<EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>?>(null)

const val SERVER_PORT = 8888
const val SERVER_HTTP_URL = "http://127.0.0.1:$SERVER_PORT"
fun createServer() = appScope.embeddedServer(CIO, SERVER_PORT) {
    routing {
        get("/") { call.respondText(ContentType.Text.Plain) { "hello world" } }
    }
}

fun Context.openUri(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val json233 by lazy {
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }
}

val client by lazy {
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json233, ContentType.Any)
        }
    }
}
