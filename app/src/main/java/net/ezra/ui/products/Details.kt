package net.ezra.ui.vehicles

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import net.ezra.navigation.ROUTE_VIEW_PROD
import net.ezra.ui.products.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(navController: NavController, vehicleId: String) {

    var vehicle by remember { mutableStateOf<Vehicle?>(null) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(vehicleId) {
        fetchVehicle(vehicleId) { fetchedVehicle ->
            vehicle = fetchedVehicle
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = vehicle?.name ?: "Details",
                        fontSize = 30.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_VIEW_PROD)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "backIcon",
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
                vehicle?.let {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(it.imageUrl),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )
                        Text(text = it.name, style = MaterialTheme.typography.h5)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Price per day: \$${it.price}", style = MaterialTheme.typography.subtitle1)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it.description, style = MaterialTheme.typography.body1)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Registration Number: ${it.registrationNumber}", style = MaterialTheme.typography.subtitle1)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Booking confirmed!")
                                    navController.navigate(ROUTE_VIEW_PROD)
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xff0FB06A)
                            )
                        ) {
                            Text(text = "Book Now")
                        }
                    }
                }
            }
        }
    )
}

private suspend fun fetchVehicle(vehicleId: String, onSuccess: (Vehicle?) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("vehicles").document(vehicleId)
    val snapshot = docRef.get().await()
    val vehicle = snapshot.toObject<Vehicle>()
    onSuccess(vehicle)
}
