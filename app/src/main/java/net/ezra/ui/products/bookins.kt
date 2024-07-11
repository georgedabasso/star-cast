package net.ezra.ui.products

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import net.ezra.navigation.ROUTE_HOME


// Composable function for Shopping Cart screen
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ShoppingCartScreen(navController: NavController) {
    val cartItems by remember { derivedStateOf { CartState.cartItems } }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bookings",
                        fontSize = 24.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_HOME)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Optional: Add other actions if needed
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xff6200EE),
                    titleContentColor = Color.White
                )
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("you have no bookings", style = MaterialTheme.typography.h6)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(cartItems) { item ->
                            CartItemRow(item, onRemoveItem = { removedItem ->
                                CartState.cartItems.remove(removedItem)
                            })
                            Divider()
                        }
                    }
                }
            }
        }
    )
}


// Function to store order in Firestore (if needed)
private suspend fun storeOrderInFirestore(cartItems: List<CartItem>) {
    val db = FirebaseFirestore.getInstance()
    val ordersCollection = db.collection("orders")

    val batch = db.batch()
    for (cartItem in cartItems) {
        val orderData = mapOf(
            "productId" to cartItem.product.id,
            "productName" to cartItem.product.name,
            "quantity" to 1, // For simplicity, assuming quantity is 1
            "timestamp" to System.currentTimeMillis()
        )
        batch.set(ordersCollection.document(), orderData)
    }

    // Commit the batch
    batch.commit().await()
}

// Function to perform M-Pesa checkout
@Composable
fun CartItemRow(cartItem: CartItem, onRemoveItem: (CartItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
    ) {
        Image(
            painter = rememberImagePainter(cartItem.product.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = cartItem.product.name,
                style = MaterialTheme.typography.h6,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Price: $${cartItem.product.price}",
                style = MaterialTheme.typography.body1,
                color = Color.Black
            )
        }
        IconButton(
            onClick = { onRemoveItem(cartItem) },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color.Red
            )
        }
    }
}