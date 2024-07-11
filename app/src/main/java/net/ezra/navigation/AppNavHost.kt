package net.ezra.navigation

//import net.ezra.ui.auth.SignupScreen


//import net.ezra.ui.products.BookingScreen
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.ezra.ui.SplashScreen
import net.ezra.ui.about.AboutScreen
import net.ezra.ui.auth.LoginScreen
import net.ezra.ui.auth.SignUpScreen

import net.ezra.ui.bookings.BookingDetailScreen
import net.ezra.ui.dashboard.DashboardScreen
import net.ezra.ui.home.HomeScreen
import net.ezra.ui.products.ShoppingCartScreen
import net.ezra.ui.products.VehicleDetailScreen
import net.ezra.ui.products.VehicleListScreen
import net.ezra.ui.students.AddStudents
import net.ezra.ui.students.Search
import net.ezra.ui.students.Students
import net.ezra.ui.vehicles.AddvehicleScreen
import net.ezra.ui.vehicles.UserVehiclesScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH


) {


    BackHandler {
        navController.popBackStack()

        }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {


        composable(ROUTE_HOME) {
            HomeScreen(navController)
        }


        composable(ROUTE_ABOUT) {
            AboutScreen(navController)
        }


        composable(ROUTE_ADD_STUDENTS) {
            AddStudents(navController)
        }

        composable(ROUTE_SPLASH) {
            SplashScreen(navController)
        }

        composable(ROUTE_VIEW_STUDENTS) {
           Students(navController = navController, viewModel = viewModel() )
        }

        composable(ROUTE_SEARCH) {
            Search(navController)
        }

        composable(ROUTE_DASHBOARD) {
            DashboardScreen(navController)
        }

        composable(ROUTE_REGISTER) {
           SignUpScreen(navController = navController) {

           }
        }

        composable(ROUTE_LOGIN) {
            LoginScreen(navController = navController){}
        }

        composable(ROUTE_ADD_PRODUCT) {
            AddvehicleScreen(navController = navController){}
        }

        composable(ROUTE_VIEW_PROD) {
            VehicleListScreen(navController = navController, Vehicles = listOf() )
        }



        composable("VehicleDetail/{VehicleId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("VehicleId") ?: ""
            VehicleDetailScreen(navController, productId)
        }

        composable(ROUTE_BOOKING_LIST) {
            UserVehiclesScreen(navController = navController )
        }


        composable(
            "bookingDetail/{bookingId}",
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailScreen(navController, bookingId)
        }
        composable(ROUTE_SHOPPING_CART) {
            ShoppingCartScreen(navController = navController)
        }







































    }
}