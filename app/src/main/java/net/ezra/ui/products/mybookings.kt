package net.ezra.ui.vehicles

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import net.ezra.navigation.ROUTE_DASHBOARD

data class Vehicle(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserVehiclesScreen(navController: NavHostController) {
    var isLoading by remember { mutableStateOf(true) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userVehicles = remember { mutableStateListOf<Vehicle>() }
    val specialOffers = remember { mutableStateListOf<Vehicle>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            coroutineScope.launch {
                val db = Firebase.firestore

                // Fetch regular vehicles
                db.collection("vehicles")
                    .whereEqualTo("userId", currentUser.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val vehicle = document.toObject(Vehicle::class.java)
                            userVehicles.add(vehicle)
                        }
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading = false
                    }
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Your Vehicles",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(ROUTE_DASHBOARD) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xff6200EE))
            )
        },
        content = { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val combinedVehicles = userVehicles + specialOffers
                if (combinedVehicles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You have no vehicles",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues)  // Apply padding values provided by Scaffold
                    ) {
                        items(combinedVehicles) { vehicle ->
                            VehicleItem(vehicle) {
                                navController.navigate("editvehicle/${vehicle.id}")
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun VehicleItem(vehicle: Vehicle, onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.LightGray)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = vehicle.imageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = vehicle.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = vehicle.description,
            fontSize = 14.sp,
            color = Color.Gray,
            maxLines = 2,
            modifier = Modifier.padding(end = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "\$${vehicle.price}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF43A047)
        )
    }
}
