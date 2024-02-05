package com.mealmentor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.mealmentor.logic.database.sign_in.GoogleAuthClient
import com.mealmentor.logic.database.sign_in.SignInViewModel
import com.mealmentor.ui.screens.LoginPage
import com.mealmentor.ui.screens.ProfilePage
import com.mealmentor.ui.screens.SignUpPage
import com.mealmentor.ui.screens.SplashScreen
import com.mealmentor.ui.theme.MealMentorKotlinTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealMentorKotlinTheme {
                Navigation()
            }
        }
    }

    @Composable
    fun Navigation() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            NavHost(
                navController = navController, startDestination = "splash_screen"
            ) {

                // Splash screen
                navigation(
                    startDestination = "splash", route = "splash_screen"
                ) {
                    composable("splash") {
                        SplashScreen {
                            if (googleAuthClient.getSignedInUser() != null) {
                                navController.popBackStack()
                                navController.navigate("profile_page")
                            } else {
                                navController.popBackStack()
                                navController.navigate("auth")
                            }
                        }
                    }
                }

                // Авторизація
                navigation(
                    startDestination = "log_in", route = "auth"
                ) {
                    // Вікно авторизації
                    composable("log_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsState()

                        val launcher =
                            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) lifecycleScope.launch {
                                        val signInResult = googleAuthClient.signInWithIntent(
                                            result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)
                                    }

                                })

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext, "Sign in successful", Toast.LENGTH_LONG
                                ).show()
                                navController.popBackStack()
                                navController.navigate("profile_page")
                                viewModel.resetState()
                            }
                        }

                        LoginPage(state = state, onGoogleSignInClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }, navigateToForgotPasswordPage = {
                            Toast.makeText(
                                applicationContext, "Forgot password", Toast.LENGTH_LONG
                            ).show()
                            // navController.navigate("forgot_password")
                        }, navigateToSignUpPage = {
                            navController.navigate("sign_up")
                        })
                    }

                    // Вікно реєстрації
                    composable("sign_up") {

                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsState()

                        val launcher =
                            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) lifecycleScope.launch {
                                        val signInResult = googleAuthClient.signInWithIntent(
                                            result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)
                                    }

                                })

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext, "Sign in successful", Toast.LENGTH_LONG
                                ).show()
                                navController.popBackStack()
                                navController.navigate("profile_page")
                                viewModel.resetState()
                            }
                        }

                        SignUpPage(state = state, onGoogleSignInClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }, navigateToLogInPage = {
                            navController.popBackStack()
                            navController.navigate("log_in")
                        })
                    }
                }

                // Профіль
                navigation(
                    startDestination = "profile", route = "profile_page"
                ) {
                    // Вікно профілю
                    composable("profile") {
                        ProfilePage(userData = googleAuthClient.getSignedInUser(), onSignOut = {
                            lifecycleScope.launch {
                                googleAuthClient.signOut()
                                Toast.makeText(
                                    applicationContext, "Sign out successful", Toast.LENGTH_LONG
                                ).show()
                                navController.popBackStack()
                                navController.navigate("auth")
                            }
                        })
                    }
                }

            }
        }
    }
}
