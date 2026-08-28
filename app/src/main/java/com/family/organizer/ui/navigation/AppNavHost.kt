package com.family.organizer.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.family.organizer.FamilyOrganizerApp
import com.family.organizer.ui.auth.AuthViewModel
import com.family.organizer.ui.auth.AuthViewModelFactory
import com.family.organizer.ui.auth.FamilySetupScreen
import com.family.organizer.ui.auth.LoginScreen
import com.family.organizer.ui.auth.SignUpScreen
import com.family.organizer.ui.calendar.CalendarScreen
import com.family.organizer.ui.calendar.CalendarViewModel
import com.family.organizer.ui.calendar.CalendarViewModelFactory
import com.family.organizer.ui.dashboard.DashboardScreen
import com.family.organizer.ui.dashboard.DashboardViewModel
import com.family.organizer.ui.dashboard.DashboardViewModelFactory
import com.family.organizer.ui.finance.AddTransactionScreen
import com.family.organizer.ui.finance.AddTransactionViewModel
import com.family.organizer.ui.finance.AddTransactionViewModelFactory
import com.family.organizer.ui.finance.CategoriesScreen
import com.family.organizer.ui.finance.CategoriesViewModel
import com.family.organizer.ui.finance.CategoriesViewModelFactory
import com.family.organizer.ui.finance.FinanceScreen
import com.family.organizer.ui.finance.FinanceViewModel
import com.family.organizer.ui.finance.FinanceViewModelFactory
import com.family.organizer.ui.goals.GoalsScreen
import com.family.organizer.ui.goals.GoalsViewModel
import com.family.organizer.ui.goals.GoalsViewModelFactory
import com.family.organizer.ui.more.MoreScreen
import com.family.organizer.ui.more.MoreViewModel
import com.family.organizer.ui.more.MoreViewModelFactory
import com.family.organizer.ui.shopping.ShoppingScreen
import com.family.organizer.ui.shopping.ShoppingViewModel
import com.family.organizer.ui.shopping.ShoppingViewModelFactory
import com.family.organizer.ui.tasks.TasksScreen
import com.family.organizer.ui.tasks.TasksViewModel
import com.family.organizer.ui.tasks.TasksViewModelFactory
import com.family.organizer.ui.wishlist.WishlistScreen
import com.family.organizer.ui.wishlist.WishlistViewModel
import com.family.organizer.ui.wishlist.WishlistViewModelFactory

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    NavItem("dashboard", "Дашборд", Icons.Default.Home),
    NavItem("finance", "Финансы", Icons.Default.List),
    NavItem("tasks", "Задачи", Icons.Default.CheckCircle),
    NavItem("shopping", "Покупки", Icons.Default.ShoppingCart),
    NavItem("more", "Ещё", Icons.Default.MoreVert),
)

/**
 * Корневая точка входа: сначала проверяет состояние аккаунта (Firebase Auth)
 * и членство в семье (Firestore), и только затем показывает основное
 * приложение с нижним меню. См. data/auth и data/family.
 */
@Composable
fun AppNavHost() {
    val app = LocalContext.current.applicationContext as FamilyOrganizerApp
    val authViewModel = viewModel<AuthViewModel>(
        factory = AuthViewModelFactory(app.authRepository, app.familyCloudRepository, app.familySession),
    )
    val authState by authViewModel.uiState.collectAsState()

    when {
        authState.checkingSession -> FullScreenLoading()
        authState.userId == null -> AuthFlow(authViewModel)
        authState.checkingFamily -> FullScreenLoading()
        authState.familyId == null -> FamilySetupScreen(
            userEmail = authState.userEmail,
            isLoading = authState.isLoading,
            errorMessage = authState.errorMessage,
            onCreateFamily = { name -> authViewModel.createFamily(name) },
            onJoinFamily = { code -> authViewModel.joinFamily(code) },
            onSignOut = { authViewModel.signOut() },
        )
        else -> MainAppScaffold(onSignOut = { authViewModel.signOut() })
    }
}

@Composable
private fun AuthFlow(authViewModel: AuthViewModel) {
    var showSignUp by remember { mutableStateOf(false) }
    val authState by authViewModel.uiState.collectAsState()

    if (showSignUp) {
        SignUpScreen(
            isLoading = authState.isLoading,
            errorMessage = authState.errorMessage,
            onSignUp = { name, email, password -> authViewModel.signUp(name, email, password) },
            onGoToLogin = {
                showSignUp = false
                authViewModel.clearError()
            },
        )
    } else {
        LoginScreen(
            isLoading = authState.isLoading,
            errorMessage = authState.errorMessage,
            onLogin = { email, password -> authViewModel.signIn(email, password) },
            onGoToSignUp = {
                showSignUp = true
                authViewModel.clearError()
            },
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MainAppScaffold(onSignOut: () -> Unit) {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("dashboard") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<DashboardViewModel>(
                    factory = DashboardViewModelFactory(
                        app.transactionRepository,
                        app.goalRepository,
                        app.taskRepository,
                        app.shoppingRepository,
                        app.calendarEventRepository,
                    ),
                )
                DashboardScreen(viewModel = viewModel, onNavigate = { route -> navController.navigate(route) })
            }

            composable("finance") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<FinanceViewModel>(
                    factory = FinanceViewModelFactory(
                        app.transactionRepository,
                        app.categoryRepository,
                        app.savingsAccountRepository,
                        app.familyMemberRepository,
                        app.goalRepository,
                    ),
                )
                FinanceScreen(viewModel = viewModel, onAddTransaction = { navController.navigate("finance_add") })
            }

            composable("finance_add") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<AddTransactionViewModel>(
                    factory = AddTransactionViewModelFactory(app.transactionRepository, app.categoryRepository),
                )
                AddTransactionScreen(
                    viewModel = viewModel,
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    onAddCategory = { navController.navigate("finance_categories") },
                )
            }

            composable("finance_categories") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<CategoriesViewModel>(
                    factory = CategoriesViewModelFactory(app.categoryRepository),
                )
                CategoriesScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }

            composable("tasks") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<TasksViewModel>(
                    factory = TasksViewModelFactory(app.taskRepository, app.familyMemberRepository),
                )
                TasksScreen(viewModel = viewModel)
            }

            composable("shopping") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<ShoppingViewModel>(
                    factory = ShoppingViewModelFactory(app.shoppingRepository),
                )
                ShoppingScreen(viewModel = viewModel)
            }

            composable("more") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<MoreViewModel>(
                    factory = MoreViewModelFactory(
                        app.familyMemberRepository,
                        app.calendarEventRepository,
                        app.goalRepository,
                        app.wishlistItemRepository,
                    ),
                )
                MoreScreen(
                    viewModel = viewModel,
                    onOpenCalendar = { navController.navigate("more_calendar") },
                    onOpenGoals = { navController.navigate("more_goals") },
                    onOpenWishlist = { navController.navigate("more_wishlist") },
                    onOpenCategories = { navController.navigate("finance_categories") },
                    onSignOut = onSignOut,
                )
            }

            composable("more_calendar") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<CalendarViewModel>(
                    factory = CalendarViewModelFactory(app.calendarEventRepository),
                )
                CalendarScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }

            composable("more_goals") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<GoalsViewModel>(
                    factory = GoalsViewModelFactory(app.goalRepository),
                )
                GoalsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }

            composable("more_wishlist") {
                val app = LocalContext.current.applicationContext as FamilyOrganizerApp
                val viewModel = viewModel<WishlistViewModel>(
                    factory = WishlistViewModelFactory(app.wishlistItemRepository, app.familyMemberRepository),
                )
                WishlistScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentRoute?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                },
            )
        }
    }
}
