package net.ezra.ui.products

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import net.ezra.navigation.ROUTE_HOME

data class Vehicle(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    var imageUrl: String = "",
    var registrationNumber: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun VehicleListScreen(navController: NavController, Vehicles: List<Vehicle>) {
    var isLoading by remember { mutableStateOf(true) }
    var vehicleList by remember { mutableStateOf(emptyList<Vehicle>()) }
    var displayedVehicleCount by remember { mutableStateOf(10) }
    var progress by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        fetchVehicles { fetchedVehicles ->
            vehicleList = fetchedVehicles
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Vehicles for Rent", fontSize = 30.sp, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_HOME)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "backIcon",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff6200EE),
                    titleContentColor = Color.White,
                )
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(progress = progress / 100f)
                        Text(text = "Fetching vehicles...", fontSize = 20.sp)
                    }
                } else {
                    if (vehicleList.isEmpty()) {
                        Text(text = "No vehicles found", modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyVerticalGrid(columns = GridCells.Fixed(1)) {
                            items(vehicleList) { vehicle ->
                                VehicleListItem(vehicle) {
                                    navController.navigate("vehicleDetail/${vehicle.id}")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (displayedVehicleCount < vehicleList.size) {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff0FB06A)),
                                onClick = { displayedVehicleCount += 8 },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "More Vehicles")
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun VehicleListItem(vehicle: Vehicle, onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(vehicle.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(vehicle.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = vehicle.name, style = MaterialTheme.typography.h6)
                Text(text = "Price per day: \$${vehicle.price}", style = MaterialTheme.typography.body2)
            }
        }
    }
}

private suspend fun fetchVehicles(onSuccess: (List<Vehicle>) -> Unit) {
    val firestore = Firebase.firestore
    val snapshot = firestore.collection("vehicles").get().await()
    val vehicleList = snapshot.documents.mapNotNull { doc ->
        val vehicle = doc.toObject<Vehicle>()
        vehicle?.id = doc.id
        vehicle
    }
    onSuccess(vehicleList)
}
