package net.ezra.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import net.ezra.navigation.ROUTE_LOGIN
import net.ezra.navigation.ROUTE_REGISTER

@Composable
fun SignUpScreen(navController: NavController, onSignUpSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Sign Up", style = MaterialTheme.typography.h4.copy(color = Color.Gray))
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
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Magenta
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
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
                when {
                    email.isBlank() -> error = "Email is required"
                    password.isBlank() -> error = "Password is required"
                    confirmPassword.isBlank() -> error = "Password Confirmation required"
                    password != confirmPassword -> error = "Passwords do not match"
                    else -> {
                        isLoading = true
                        signUp(email, password, {
                            isLoading = false
                            Toast.makeText(context, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                            onSignUpSuccess()
                        }) { errorMessage ->
                            isLoading = false
                            error = errorMessage
                        }
                    }
                }
            },
            modifier = Modifier.width(100.dp)
        ) {
            Text("Sign Up", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.clickable {
                navController.navigate(ROUTE_LOGIN) {
                    popUpTo(ROUTE_REGISTER) { inclusive = true }
                }
            },
            text = "Already have an account? Login",
            textAlign = TextAlign.Center,
            color = Color.Magenta
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

private fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                if (signInMethods.isNotEmpty()) {
                    onError("Email is already registered")
                } else {
                    // Email is not registered, proceed with sign-up
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { signUpTask ->
                            if (signUpTask.isSuccessful) {
                                onSuccess()
                            } else {
                                onError(signUpTask.exception?.message ?: "Sign-up failed")
                            }
                        }
                }
            } else {
                onError(task.exception?.message ?: "Failed to check email availability")
            }
        }
}
