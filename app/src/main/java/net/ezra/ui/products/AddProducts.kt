package net.ezra.ui.booking

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import net.ezra.navigation.ROUTE_ADD_PRODUCT
import net.ezra.navigation.ROUTE_HOME
import net.ezra.navigation.ROUTE_VIEW_PROD

import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(navController: NavController, onVehicleAdded: () -> Unit) {
    var vehicleName by remember { mutableStateOf("") }
    var vehicleDescription by remember { mutableStateOf("") }
    var vehiclePrice by remember { mutableStateOf("") }
    var registrationNumber by remember { mutableStateOf("") }
    var vehicleImageUri by remember { mutableStateOf<Uri?>(null) }

    var vehicleNameError by remember { mutableStateOf(false) }
    var vehicleDescriptionError by remember { mutableStateOf(false) }
    var vehiclePriceError by remember { mutableStateOf(false) }
    var registrationNumberError by remember { mutableStateOf(false) }
    var vehicleImageError by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            vehicleImageUri = it
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Add Vehicle", fontSize = 30.sp, color = Color.White)
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff0FB06A),
                    titleContentColor = Color.White,
                )
            )
        }
    ) {
        if (isLoading) {
            LoadingDialog()
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xffE8F5E9))
                .padding(16.dp)
        ) {
            item {
                if (vehicleImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(vehicleImageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.White)
                            .padding(8.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color(0xffC8E6C9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image Selected", modifier = Modifier.padding(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff4CAF50))
                ) {
                    Text("Select Image", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = vehicleName,
                    onValueChange = { vehicleName = it },
                    label = { Text("Vehicle Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = vehicleNameError
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = vehicleDescription,
                    onValueChange = { vehicleDescription = it },
                    label = { Text("Vehicle Description") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = vehicleDescriptionError
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = vehiclePrice,
                    onValueChange = { vehiclePrice = it },
                    label = { Text("Vehicle Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { /* Handle Done action */ }),
                    modifier = Modifier.fillMaxWidth(),
                    isError = vehiclePriceError
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = registrationNumber,
                    onValueChange = { registrationNumber = it },
                    label = { Text("Registration Number") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = registrationNumberError
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (vehicleNameError) {
                    Text(
                        "Vehicle Name is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }
                if (vehicleDescriptionError) {
                    Text(
                        "Vehicle Description is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }
                if (vehiclePriceError) {
                    Text(
                        "Vehicle Price is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }
                if (registrationNumberError) {
                    Text(
                        "Registration Number is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }
                if (vehicleImageError) {
                    Text(
                        "Vehicle Image is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.caption
                    )
                }

                Button(
                    onClick = {
                        vehicleNameError = vehicleName.isBlank()
                        vehicleDescriptionError = vehicleDescription.isBlank()
                        vehiclePriceError = vehiclePrice.isBlank()
                        registrationNumberError = registrationNumber.isBlank()
                        vehicleImageError = vehicleImageUri == null

                        if (!vehicleNameError && !vehicleDescriptionError && !vehiclePriceError && !registrationNumberError && !vehicleImageError) {
                            isLoading = true
                            addVehicleToFirestore(
                                navController,
                                onVehicleAdded,
                                vehicleName,
                                vehicleDescription,
                                vehiclePrice.toDouble(),
                                registrationNumber,
                                vehicleImageUri,
                                { isLoading = false }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff4CAF50))
                ) {
                    Text("Add Vehicle", color = Color.White)
                }
            }
        }
    }
}


@Composable
private fun LoadingDialog() {
    Dialog(
        onDismissRequest = { /* Handle dialog dismiss if needed */ }
    ) {
        Box(
            modifier = Modifier
                .width(280.dp)
                .padding(16.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Adding Your Vehicle Details...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

private fun addVehicleToFirestore(
    navController: NavController,
    onVehicleAdded: () -> Unit,
    vehicleName: String,
    vehicleDescription: String,
    vehiclePrice: Double,
    registrationNumber: String,
    vehicleImageUri: Uri?,
    onLoadingComplete: () -> Unit
) {
    if (vehicleName.isEmpty() || vehicleDescription.isEmpty() || vehiclePrice.isNaN() || registrationNumber.isEmpty() || vehicleImageUri == null) {
        onLoadingComplete()
        return
    }

    val vehicleId = UUID.randomUUID().toString()
    val firestore = Firebase.firestore
    val vehicleData = hashMapOf(
        "name" to vehicleName,
        "description" to vehicleDescription,
        "price" to vehiclePrice,
        "registrationNumber" to registrationNumber,
        "imageUrl" to ""
    )

    firestore.collection("vehicles").document(vehicleId)
        .set(vehicleData)
        .addOnSuccessListener {
            uploadImageToStorage(vehicleId, vehicleImageUri) { imageUrl ->
                firestore.collection("vehicles").document(vehicleId)
                    .update("imageUrl", imageUrl)
                    .addOnSuccessListener {
                        Toast.makeText(
                            navController.context,
                            "Vehicle added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        navController.navigate(ROUTE_VIEW_PROD)
                        onVehicleAdded()
                        onLoadingComplete()
                    }
                    .addOnFailureListener { e ->
                        onLoadingComplete()
                    }
            }
        }
        .addOnFailureListener { e ->
            onLoadingComplete()
        }
}

private fun uploadImageToStorage(vehicleId: String, imageUri: Uri?, onSuccess: (String) -> Unit) {
    if (imageUri == null) {
        onSuccess("")
        return
    }

    val storageRef = Firebase.storage.reference
    val imagesRef = storageRef.child("vehicles/$vehicleId.jpg")

    imagesRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            imagesRef.downloadUrl
                .addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
                .addOnFailureListener {
                    // Handle failure to get download URL
                }
        }
        .addOnFailureListener {
            // Handle failure to upload image
        }
}
