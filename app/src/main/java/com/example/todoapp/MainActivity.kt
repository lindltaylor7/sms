package com.example.todoapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.ui.theme.TodoAppTheme

class MainActivity : ComponentActivity() {
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        setContent {
            val permissionGranted = remember { mutableStateOf(false) }

            val context = LocalContext.current

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                granted ->
                permissionGranted.value = granted
                if (granted) {
                    Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
            }

            LaunchedEffect (Unit) {
                permissionLauncher.launch(android.Manifest.permission.SEND_SMS)
            }

            if (permissionGranted.value) {
                TodoAppTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        ToDoListPage(todoViewModel)
                    }
                }
            } else {
                Text(text = "Esperando permiso...")
            }
        }
    }
}

