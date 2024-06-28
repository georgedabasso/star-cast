package net.ezra.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import net.ezra.navigation.ROUTE_DASHBOARD
import net.ezra.navigation.ROUTE_LOGIN
import net.ezra.navigation.ROUTE_REGISTER

@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    BackHandler {
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Login", style = MaterialTheme.typography.h4.copy(color = Color.Magenta))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.width(350.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Magenta
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.width(350.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Magenta
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "Please fill in all fields"
                } else {
                    isLoading = true
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                navController.navigate(ROUTE_DASHBOARD)
                            } else {
                                error = task.exception?.message ?: "Login failed"
                            }
                        }
                }
            },
            modifier = Modifier
                .width(100.dp)
        ) {
            Text("Login", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.clickable {
                navController.navigate(ROUTE_REGISTER) {
                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                }
            },
            text = "Don't have an account? Register",
            textAlign = TextAlign.Center,
            color = Color.Magenta,
            style = MaterialTheme.typography.body2
        )

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}
