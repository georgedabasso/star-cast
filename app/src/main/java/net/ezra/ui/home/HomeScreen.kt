package net.ezra.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import net.ezra.R
import net.ezra.navigation.ROUTE_ABOUT
import net.ezra.navigation.ROUTE_BOOKING
import net.ezra.navigation.ROUTE_BOOKING_LIST
import net.ezra.navigation.ROUTE_DASHBOARD
import net.ezra.navigation.ROUTE_HOME
import net.ezra.navigation.ROUTE_LOGIN
import net.ezra.navigation.ROUTE_SEARCH
import net.ezra.navigation.ROUTE_SHOPPING_CART
import net.ezra.navigation.ROUTE_VIEW_PROD

data class Screen(val title: String, val icon: Int)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceAsColor")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    var isDrawerOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val isLoggedIn: Boolean = sharedPreferences.getBoolean("isLoggedIn", false)

    val callLauncher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { _ ->

        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "StarCast Booking Agency",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = @Composable {
                    if (!isDrawerOpen) {
                        IconButton(onClick = { isDrawerOpen = true }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    }
                },

                actions = {
                    IconButton(onClick = {
                        if (isLoggedIn) {
                            navController.navigate(ROUTE_DASHBOARD) { popUpTo("home") { inclusive = true } }
                        } else {
                            navController.navigate(ROUTE_LOGIN) { popUpTo(ROUTE_HOME) { inclusive = true } }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
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

        content = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        if (isDrawerOpen) {
                            isDrawerOpen = false
                        }
                    }
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome to StarCast Booking Agency",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Button(
                        onClick = {
                            navController.navigate(ROUTE_VIEW_PROD)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xff6200EE))
                    ) {
                        Text("View Vehicles", color = Color.White)
                    }
                }
            }
        },

        bottomBar = { BottomBar(navController = navController) }
    )

    AnimatedDrawer(
        isOpen = isDrawerOpen,
        onClose = { isDrawerOpen = false }
    )
}

@Composable
fun AnimatedDrawer(isOpen: Boolean, onClose: () -> Unit) {
    val drawerWidth = remember { Animatable(if (isOpen) 250f else 0f) }

    LaunchedEffect(isOpen) {
        drawerWidth.animateTo(if (isOpen) 250f else 0f, animationSpec = tween(durationMillis = 300))
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawerWidth.value.dp),
        color = Color(0xFFE0E0E0),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Drawer Item 1", modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Drawer Item 2", modifier = Modifier.padding(bottom = 8.dp))

        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val isLoggedIn: Boolean = sharedPreferences.getBoolean("isLoggedIn", false)
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(
        elevation = 10.dp,
        backgroundColor = Color(0xff6200EE),
    ) {
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home, "", tint = Color.White)
        },
            label = { Text(text = "Home", color = Color.White) },
            selected = (selectedIndex.value == 0), onClick = {
                selectedIndex.value = 0
                if (isLoggedIn) {
                    navController.navigate(ROUTE_DASHBOARD) { popUpTo("home") { inclusive = true } }
                } else {
                    navController.navigate(ROUTE_LOGIN) { popUpTo(ROUTE_HOME) { inclusive = true } }
                }
            })
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.PlayArrow, "", tint = Color.White)
        },
            label = { Text(text = "Your Bookings", color = Color.White) },
            selected = (selectedIndex.value == 2), onClick = {
                selectedIndex.value = 3
                navController.navigate(ROUTE_SHOPPING_CART)
                selectedIndex.value = 2
            })
    }
}
