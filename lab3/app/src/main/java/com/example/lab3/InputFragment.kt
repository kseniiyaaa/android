package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class InputFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    InputScreen()
                }
            }
        }
    }

    @Composable
    fun InputScreen() {
        var password by remember { mutableStateOf("") }
        var showPassword by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }
        var showSaveSuccessDialog by remember { mutableStateOf(false) }

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
                Row {
                    RadioButton(
                        selected = showPassword,
                        onClick = { showPassword = true }
                    )
                    Text("Показати символи", modifier = Modifier.padding(start = 8.dp))
                }
                Row {
                    RadioButton(
                        selected = !showPassword,
                        onClick = { showPassword = false }
                    )
                    Text("Приховати символи", modifier = Modifier.padding(start = 8.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (password.isEmpty()) {
                            showDialog = true
                        } else {
                            val success = (requireActivity() as MainActivity).savePassword(password)
                            if (success) {
                                showSaveSuccessDialog = true
                            }
                            (requireActivity() as MainActivity).showResult("Введений пароль: $password")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("OK")
                }

                Button(
                    onClick = {
                        (requireActivity() as MainActivity).openStoredData()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Відкрити")
                }
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

            if (showSaveSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveSuccessDialog = false },
                    title = { Text("Успіх") },
                    text = { Text("Дані успішно збережено") },
                    confirmButton = {
                        TextButton(onClick = { showSaveSuccessDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }

    fun clearInput() {
        parentFragmentManager.commit {
            replace(R.id.inputFragmentContainer, InputFragment())
        }
    }
}