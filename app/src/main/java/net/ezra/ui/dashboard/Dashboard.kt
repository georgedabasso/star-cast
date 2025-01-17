package net.ezra.ui.dashboard

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import net.ezra.navigation.ROUTE_LOGIN
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import net.ezra.navigation.ROUTE_ADD_PRODUCT
import net.ezra.navigation.ROUTE_ADD_STUDENTS
import net.ezra.navigation.ROUTE_BOOKING
import net.ezra.navigation.ROUTE_BOOKING_LIST
import net.ezra.navigation.ROUTE_DASHBOARD
import net.ezra.navigation.ROUTE_HOME

private var progressDialog: ProgressDialog? = null

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DashboardScreen(navController: NavHostController) {
    var school by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()
    var user: User? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }
    var studentCount by remember { mutableIntStateOf(0) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val firestores = Firebase.firestore
    val context = LocalContext.current

    BackHandler {
        navController.popBackStack()
    }

    // Fetch user details from Firestore
    LaunchedEffect(key1 = currentUser?.uid) {
        if (currentUser != null) {
            val userDocRef = firestore.collection("users").document(currentUser.uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        user = document.toObject<User>()
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    isLoading = false
                }
        }
    }

    LaunchedEffect(Unit) {
        firestores.collection("Students")
            .get()
            .addOnSuccessListener { result ->
                studentCount = result.size()
            }
            .addOnFailureListener { exception ->
                // Handle failures
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Dashboard", color = Color.White, fontSize = 30.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff6200EE),
                    titleContentColor = Color.White,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_HOME)
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon", tint = Color.White)
                    }
                },
            )
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val dashboardItems = listOf(
                    DashboardItemData(
                        title = "Add Vehicles",
                        icon = Icons.Default.Add,
                        badgeCount = 3,
                        onClick = {
                            navController.navigate(ROUTE_ADD_PRODUCT)
                        }
                    ),
                    DashboardItemData(
                        title = "My Vehicles",
                        icon = Icons.Default.ShoppingCart,
                        badgeCount = 4,
                        onClick = {
                            navController.navigate(ROUTE_BOOKING_LIST)
                            // Navigate to booking list screen
                        }
                    ),
                    // Add more dashboard items as needed
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(16.dp)
                ) {
                    items(dashboardItems) { item ->
                        DashboardItem(item)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardItem(item: DashboardItemData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(12.dp))
            .clickable { item.onClick() },
        elevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = "Dashboard Icon",
                tint = Color(0xff0FB06A),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.subtitle1.copy(fontSize = 18.sp),
                color = Color.Black
            )
            if (item.badgeCount > 0) {
                Badge(count = item.badgeCount)
            }
        }
    }
}

@Composable
fun Badge(count: Int) {
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.caption.copy(color = Color.White)
        )
    }
}

data class DashboardItemData(
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int,
    val onClick: () -> Unit
)

data class User(
    val userId: String = "",
    val school: String = "",
    val name: String = ""
)

fun saveUserDetails(user: User, param: (Any) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(user.userId)
        .set(user, SetOptions.merge())
        .addOnSuccessListener {
            progressDialog?.dismiss()
            // Success message or navigation
        }
        .addOnFailureListener {
            progressDialog?.dismiss()
            // Handle failure
        }
}
