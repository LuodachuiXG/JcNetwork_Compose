import androidx.compose.desktop.DesktopTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.repositories.ConfigKey
import data.repositories.ConfigRepo
import java.awt.Dimension
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.JOptionPane

@Composable
fun App() {
    MaterialTheme {
        MainView()
    }
}

@Composable
fun MainView(
    viewModel: MainViewModel = MainViewModel()
) {
    Column (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()

    ) {
        Text(
            text = "当前环境：${System.getProperty("os.name")}"
        )
        MainInput(viewModel)
    }
}

@Composable
fun MainInput(
    viewModel: MainViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("提示")
            },
            text = {
                Text(dialogText)
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text("确定")
                }
            }
        )
    }

    Column (
        modifier = Modifier.padding(top = 10.dp)
    ) {
        OutlinedTextField (
            value = viewModel.userName,
            onValueChange = { viewModel.userName = it },
            label = {
                Text("帐号")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = {
                Text("密码")
            },
            visualTransformation = PasswordVisualTransformation('#'),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            onClick = {
                dialogText = "这是一个测试哦"
                showDialog = true
            }
        ) {
            Text("登录")
        }
    }
}

/**
 * MainViewModel
 */
class MainViewModel {
    var userName by mutableStateOf("")
    var password by mutableStateOf("")

    init {
        // 获取本地是否已经有保存的账号密码
        userName = ConfigRepo.getConfig(ConfigKey.USERNAME)
        password = ConfigRepo.getConfig(ConfigKey.PASSWORD)
    }
}

fun main() = application {
    val state = rememberWindowState()
    val width = ConfigRepo.getConfigInt(ConfigKey.WIDTH)
    val height = ConfigRepo.getConfigInt(ConfigKey.HEIGHT)
    val x = ConfigRepo.getConfigInt(ConfigKey.X)
    val y = ConfigRepo.getConfigInt(ConfigKey.Y)

    state.size = DpSize(
        if (width == 0) 350.dp else width.dp,
        if (height == 0) 280.dp else height.dp
    )

    state.position = if (x == 0 && y == 0) {
        WindowPosition.Aligned(Alignment.Center)
    } else {
        WindowPosition(x.dp, y.dp)
    }


    Window(
        onCloseRequest = ::exitApplication,
        title = "南航金城校园网登录",
        state = state
    ) {
        window.addComponentListener(object : ComponentListener {
            override fun componentResized(e: ComponentEvent?) {
                val w = e?.component?.width.toString()
                val h = e?.component?.height.toString()
                ConfigRepo.setConfig(ConfigKey.WIDTH, w)
                ConfigRepo.setConfig(ConfigKey.HEIGHT, h)
            }

            override fun componentMoved(e: ComponentEvent?) {
                val xx = e?.component?.x.toString()
                val yy = e?.component?.y.toString()
                ConfigRepo.setConfig(ConfigKey.X, xx)
                ConfigRepo.setConfig(ConfigKey.Y, yy)
            }
            override fun componentShown(e: ComponentEvent?) {}
            override fun componentHidden(e: ComponentEvent?) {}
        })
        App()
    }
}
