package net.ezra.ui.bookings

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import net.ezra.navigation.ROUTE_BOOKING_LIST
import net.ezra.navigation.ROUTE_HOME

data class Booking(
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val service: String = "",
    val bookingDate: String = ""
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(navController: NavController, bookingId: String) {

    var booking by remember { mutableStateOf<Booking?>(null) }

    LaunchedEffect(bookingId) {
        fetchBooking(bookingId) { fetchedBooking ->
            booking = fetchedBooking
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = booking?.name ?: "Booking Details",
                        fontSize = 30.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_BOOKING_LIST)
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "backIcon",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Magenta,
                    titleContentColor = Color.White,
                )
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                booking?.let {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = it.name, style = MaterialTheme.typography.h5)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Email: ${it.email}", style = MaterialTheme.typography.subtitle1)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Phone: ${it.phone}", style = MaterialTheme.typography.subtitle1)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Service: ${it.service}", style = MaterialTheme.typography.subtitle1)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Booking Date: ${it.bookingDate}", style = MaterialTheme.typography.body1)
                    }
                }
            }
        }
    )
}

private suspend fun fetchBooking(bookingId: String, onSuccess: (Booking?) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("bookings").document(bookingId)
    val snapshot = docRef.get().await()
    val booking = snapshot.toObject<Booking>()
    onSuccess(booking)
}
