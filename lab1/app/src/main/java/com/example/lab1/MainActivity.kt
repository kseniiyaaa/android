package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PasswordScreen()
                }
            }
        }
    }
}

@Composable
fun PasswordScreen() {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Введіть пароль") },
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = showPassword,
                    onClick = { showPassword = true }
                )
                Text(
                    text = "Показати символи",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = !showPassword,
                    onClick = { showPassword = false }
                )
                Text(
                    text = "Приховати символи",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Button(
            onClick = {
                if (password.isEmpty()) {
                    showDialog = true
                } else {
                    result = "Введений пароль: $password"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("OK")
        }
        if (result.isNotEmpty()) {
            Text(result)
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Попередження") },
                text = { Text("Будь ласка, введіть пароль") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
