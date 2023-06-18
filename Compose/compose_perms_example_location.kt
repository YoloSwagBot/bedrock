


// watching the permission's access
val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)


// more easilly manage UI state
val permissionAskState: PermissionAskState? = when {
    locationPermissionState.status.isGranted -> PermissionAskState.HAS_PERMS
    !locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale -> PermissionAskState.ASKED_TWICE
    !locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale -> PermissionAskState.ASKED_ONCE
    !locationPermissionState.status.isGranted -> PermissionAskState.NEVER_ASKED
    else -> null
}




// ie: Text
    Text(
        text = when (permissionAskState) {
            PermissionAskState.NEVER_ASKED -> "Please give the app FINE-Location permissions in Settings."
            PermissionAskState.ASKED_ONCE -> "This app DEFINITELY requires GPS/Location permissions."
            PermissionAskState.ASKED_TWICE, null -> "This app DEFINITELY requires GPS/Location permissions."
            PermissionAskState.HAS_PERMS -> "CORRECT permissions, detected."
        },
        color = Color.Red,
        fontSize = 24.sp
    )

// ie: Button asking for perms in Intro/Setup/Concierge flows
    Button(
        enabled = permissionAskState != PermissionAskState.HAS_PERMS,
        onClick = {
            when (permissionAskState) {
                PermissionAskState.NEVER_ASKED, PermissionAskState.ASKED_ONCE -> locationPermissionState.launchPermissionRequest()
                PermissionAskState.ASKED_TWICE, null -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    openSettingsLauncher.launch(intent)
                }
                PermissionAskState.HAS_PERMS -> { appLevelNav.navigate(MainActivity.AppScreens.Home.name) }
            }
        }
    ) {
        Text(
            text = when (permissionAskState){
                PermissionAskState.HAS_PERMS -> "n0ice"
                PermissionAskState.ASKED_TWICE -> "App Settings"
                else -> "enable FINE-location"
            },
            color = Color.White
        )
        Log.d("CarsonDebug", "ConciergeScreen --- perAskState: ${permissionAskState?.name}")
    }


// ie: Continuation button on permission's confirmation screen
    Button(
        enabled = permissionAskState == PermissionAskState.HAS_PERMS,
        onClick = { appLevelNav.navigate(MainActivity.AppScreens.Home.name) }
    ) {
        Text(
            text = "Done",
            color = Color.White
        )
    }



