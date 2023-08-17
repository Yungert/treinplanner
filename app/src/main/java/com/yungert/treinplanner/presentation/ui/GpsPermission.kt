package com.yungert.treinplanner.presentation.ui

import Data.Provider.LocationProvider
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.Navigation.Screen

private val locationProvider: LocationProvider = LocationProvider()

@Composable
fun showGpsPermisson(navController: NavController) {
    val context = LocalContext.current
    val hasGpsPermission = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    LaunchedEffect(hasGpsPermission) {
        if (hasGpsPermission) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGpsEnabled) {
                navController.navigate(Screen.StationVanKiezen.route)
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            navController.navigate(Screen.StationVanKiezen.route)
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
            text = stringResource(id = R.string.bericht_reden_gps_toestemming),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }) {
            Text(stringResource(id = R.string.label_button_gps_toestaan))
        }
    }

}


fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
