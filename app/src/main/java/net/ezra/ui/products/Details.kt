package net.ezra.ui.products

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(navController: NavController,  vehicleId: String) {
    var vehicle by remember { mutableStateOf<Vehicle?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(vehicleId) {
        fetchVehicle(vehicleId) { fetchedVehicle ->
            vehicle = fetchedVehicle as? Vehicle
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text =  vehicle?.name ?: "Details",
                        fontSize = 24.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "backIcon",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xff6200EE),
                    titleContentColor = Color.White,
                )
            )
        },
        content = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                vehicle?.let { product ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.White, Color.LightGray)))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(50.dp))

                        Image(
                            painter = rememberImagePainter(product.imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.White, shape = MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Price: $${product.price}",
                            style = MaterialTheme.typography.subtitle1.copy(color = Color.Green),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.body1,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Buy Button
                        Button(
                            onClick = {
                                addToCartAndNavigate(navController, product)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .shadow(2.dp, RoundedCornerShape(15.dp))
                        ) {
                            Text("Buy", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    )
}

private fun addToCartAndNavigate(navController: NavController,product: Vehicle) {
    CartState.addToCart(CartItem(product))
    navController.navigateUp()
}

suspend fun fetchVehicle(vehicleId: String, onSuccess: (Vehicle?) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("vehicles").document(vehicleId)
    val snapshot = docRef.get().await()
    val vehicle = snapshot.toObject<Vehicle>()
    onSuccess(vehicle)
}
data class CartItem(val product:  Vehicle)