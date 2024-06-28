package net.ezra.ui.booking

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import net.ezra.navigation.ROUTE_HOME

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookingScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("") }
    var bookingDate by remember { mutableStateOf("") }

    var bookingStatus by remember { mutableStateOf("") }

    val firestore = remember { FirebaseFirestore.getInstance() }

    fun addBooking(booking: Booking, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("bookings").add(booking)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("StarCast Agency")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Magenta,
                    titleContentColor = Color.White
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Book a Service", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = service,
                onValueChange = { service = it },
                label = { Text("Service") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = bookingDate,
                onValueChange = { bookingDate = it },
                label = { Text("Booking Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val booking = Booking(
                        name = name,
                        email = email,
                        phone = phone,
                        service = service,
                        bookingDate = bookingDate
                    )
                    addBooking(
                        booking,
                        onSuccess = {
                            bookingStatus = "Booking Successful"
                            navController.navigate(ROUTE_HOME) {
                                popUpTo(ROUTE_HOME) { inclusive = true }
                            }
                        },
                        onFailure = {
                            bookingStatus = "Booking Failed: ${it.message}"
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Booking")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bookingStatus,
                color = if (bookingStatus.contains("Successful")) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}

// Model class for Booking

