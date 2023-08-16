package com.yungert.treinplanner.presentation.ui

import Data.Provider.LocationProvider
import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.presentation.ui.Navigation.Screen

@Composable
fun showGpsPermisson(navController: NavController) {
    val locationProvider : LocationProvider = LocationProvider()
    if(locationProvider.isGPSEnabled()) {
        navController.navigate(Screen.StationVanKiezen.route)
        return
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            navController.navigate(Screen.StationVanKiezen.route)
        } else {

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "We hebben GPS nodig om het dichtbijzijnde station te kunnen vinden. " +
                    "In de instellingen kun je dit aanzetten als je dat wilt",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }) {
            Text("GPS toestaan")
        }
    }
}


fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
