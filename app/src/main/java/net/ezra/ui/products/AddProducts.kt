package net.ezra.ui.vehicles

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.ezra.navigation.ROUTE_BOOKING_LIST
import java.util.UUID

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddvehicleScreen(navController: NavController, onvehicleAdded: () -> Unit) {
    var vehicleName by remember { mutableStateOf("") }
    var vehicleDescription by remember { mutableStateOf("") }
    var vehiclePrice by remember { mutableStateOf("") }
    var registrationNumber by remember { mutableStateOf("") }
    var vehicleImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Track if fields are empty
    var vehicleNameError by remember { mutableStateOf(false) }
    var vehicleDescriptionError by remember { mutableStateOf(false) }
    var vehiclePriceError by remember { mutableStateOf(false) }
    var vehicleImageError by remember { mutableStateOf(false) }
    var registrationNumberError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            vehicleImageUri = it
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Add vehicles", fontSize = 24.sp, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_BOOKING_LIST)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "backIcon",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                )
            )
        },
        content = {
            if (isLoading) {
                LoadingDialog()
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp) ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Gray)
                            .padding(16.dp)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (vehicleImageUri != null) {
                            Image(
                                painter = rememberImagePainter(vehicleImageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Tap to select an image", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = vehicleName,
                        onValueChange = { vehicleName = it },
                        label = { Text("vehicle Name") },
                        isError = vehicleNameError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff0FB06A),
                            unfocusedBorderColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            cursorColor = Color(0xff0FB06A),
                            textColor = Color.Black
                        )

                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vehicleDescription,
                        onValueChange = { vehicleDescription = it },
                        label = { Text("vehicle Description") },
                        isError = vehicleDescriptionError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff0FB06A),
                            unfocusedBorderColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            cursorColor = Color(0xff0FB06A),
                            textColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vehiclePrice,
                        onValueChange = { vehiclePrice = it },
                        label = { Text("vehicle Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(onDone = { /* Handle Done action */ }),
                        isError = vehiclePriceError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff0FB06A),
                            unfocusedBorderColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            cursorColor = Color(0xff0FB06A),
                            textColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = registrationNumber,
                        onValueChange = { registrationNumber = it },
                        label = { Text("Registration Number") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = registrationNumberError,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff0FB06A),
                            unfocusedBorderColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            cursorColor = Color(0xff0FB06A),
                            textColor = Color.Black
                        )

                    )


                    if (vehicleNameError) {
                        Text("vehicle Name is required", color = Color.Red)
                    }
                    if (vehicleDescriptionError) {
                        Text("vehicle Description is required", color = Color.Red)
                    }
                    if (vehiclePriceError) {
                        Text("vehicle Price is required", color = Color.Red)
                    }
                    if (vehicleImageError) {
                        Text("vehicle Image is required", color = Color.Red)
                    }
                    if (registrationNumberError) {
                        Text("Registration Number is required", color = Color.Red)
                    }

                    Button(
                        onClick = {
                            // Reset error flags
                            vehicleNameError = vehicleName.isBlank()
                            vehicleDescriptionError = vehicleDescription.isBlank()
                            vehiclePriceError = vehiclePrice.isBlank()
                            vehicleImageError = vehicleImageUri == null

                            // Add vehicle if all fields are filled
                            if (!vehicleNameError && !vehicleDescriptionError && !vehiclePriceError && !vehicleImageError) {
                                isLoading = true
                                addvehicleToFirestore(
                                    navController,
                                    onvehicleAdded,
                                    vehicleName,
                                    vehicleDescription,
                                    vehiclePrice.toDouble(),
                                    registrationNumber,
                                    vehicleImageUri,
                                    onLoadingChange = { isLoading = it }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xff0FB06A)),
                        modifier = Modifier
                            .clickable(indication = rememberRipple(bounded = true), interactionSource = remember { MutableInteractionSource() }) { /* Handle click */ }
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.small

                    ) {
                        Text("Add vehicle", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    )
}

@Composable
fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(text = "Loading")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Please wait while we add your vehicle")
            }
        },
        buttons = {}
    )
}

private fun addvehicleToFirestore(
    navController: NavController,
    onvehicleAdded: () -> Unit,
    vehicleName: String,
    vehicleDescription: String,
    vehiclePrice: Double,
    registrationNumber: String,
    vehicleImageUri: Uri?,
    onLoadingChange: (Boolean) -> Unit
) {
    if (vehicleName.isEmpty() || vehicleDescription.isEmpty() || vehiclePrice.isNaN() || vehicleImageUri == null || registrationNumber.isEmpty()) {
        // Validate input fields
        return
    }

    val vehicleId = UUID.randomUUID().toString()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser == null) {
        // Handle user not logged in
        onLoadingChange(false)
        return
    }

    val firestore = Firebase.firestore
    val vehicleData = hashMapOf(
        "name" to vehicleName,
        "description" to vehicleDescription,
        "price" to vehiclePrice,
        "registrationNumber" to registrationNumber,
        "imageUrl" to "",
        "userId" to currentUser.uid  // Associate vehicle with the current user
    )

    firestore.collection("vehicles").document(vehicleId)
        .set(vehicleData)
        .addOnSuccessListener {
            uploadImageToStorage(vehicleId, vehicleImageUri) { imageUrl ->
                firestore.collection("vehicles").document(vehicleId)
                    .update("imageUrl", imageUrl)
                    .addOnSuccessListener {
                        // Display toast message
                        Toast.makeText(
                            navController.context,
                            "vehicle added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to the user's vehicles screen
                        navController.navigate(ROUTE_BOOKING_LIST)

                        // Invoke the onvehicleAdded callback
                        onvehicleAdded()

                        // Hide the loading dialog
                        onLoadingChange(false)
                    }
                    .addOnFailureListener { e ->
                        // Handle error updating vehicle document
                        // Hide the loading dialog
                        onLoadingChange(false)
                    }
            }
        }
        .addOnFailureListener { e ->
            // Handle error adding vehicle to Firestore
            // Hide the loading dialog
            onLoadingChange(false)
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