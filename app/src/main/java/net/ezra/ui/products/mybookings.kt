package net.ezra.ui.booking

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

import net.ezra.navigation.ROUTE_HOME

data class Booking(
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val service: String = "",
    val bookingDate: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookingListScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    var bookingList by remember { mutableStateOf(emptyList<Booking>()) }
    var displayedBookingCount by remember { mutableStateOf(1) }
    var progress by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        fetchBookings { fetchedBookings ->
            bookingList = fetchedBookings
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Bookings", fontSize = 30.sp, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_HOME)
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
                    .padding( top = 60.dp)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(progress = progress / 100f)
                        Text(text = "Loading... $progress%", fontSize = 20.sp)
                    }
                } else {
                    if (bookingList.isEmpty()) {
                        Text(text = "No bookings found", modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                            items(bookingList.take(displayedBookingCount)) { booking ->
                                BookingListItem(booking) {
                                    navController.navigate("bookingDetail/${booking.id}")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (displayedBookingCount < bookingList.size) {
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0FB06A)),
                                onClick = { displayedBookingCount += 8 },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Load More")
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun BookingListItem(booking: Booking, onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(booking.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Text(text = booking.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Service: ${booking.service}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Date: ${booking.bookingDate}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

private suspend fun fetchBookings(onSuccess: (List<Booking>) -> Unit) {
    val firestore = Firebase.firestore
    val snapshot = firestore.collection("bookings").get().await()
    val bookingList = snapshot.documents.mapNotNull { doc ->
        val booking = doc.toObject<Booking>()
        booking?.id = doc.id
        booking
    }
    onSuccess(bookingList)
}
