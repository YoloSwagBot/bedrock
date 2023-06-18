// major commit June 18th, 2023

// GRADLE
    // DataStore Newerish way of saving data that google never told anyone to use????
    implementation"androidx.datastore:datastore-preferences-core:1.0.0"
    implementation 'androidx.datastore:datastore-preferences-rxjava2:1.0.0' // trash dependency
    implementation 'androidx.datastore:datastore-preferences-rxjava3:1.0.0' // trash dependency

// seperate .kt class to use BECAUSE -- we shouldn't called suspend functions from Composables, but from the viewModelScope/etc
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
    class GeoAlarmDataStore(private val context: Context) {
        companion object {
            val ASK_PERM_LOC_ATTEMPT_COUNT = intPreferencesKey(key_db_permissionAskCount_FINE_LOCATION)
        }
        suspend fun saveAskLocationPermissionCount(count: Int){
            context.dataStore.edit {
                it[ASK_PERM_LOC_ATTEMPT_COUNT] = count
            }
        }
        fun getAskLocationPermissionCount() = context.dataStore.data.map {
            it[ASK_PERM_LOC_ATTEMPT_COUNT]
            it.get(ASK_PERM_LOC_ATTEMPT_COUNT)
        }
        suspend fun clearDataStore() = context.dataStore.edit {
            it.clear()
        }
    }


// IN-COMPOSABLE
    // Location permission state
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionAskState: PermissionAskState = when {
        locationPermissionState.status.isGranted -> PermissionAskState.HAS_PERMS
        else -> {
            when (conciergeVM.getPermLocationAskCount().collectAsState(0).value) {
                null, 0 -> PermissionAskState.NEVER_ASKED
                1 -> PermissionAskState.ASKED_ONCE
                else -> PermissionAskState.ASKED_TWICE
            }
        }
    }




// IN-COMPOSABLE
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


// IN-COMPOSABLE
    // ie: Button asking for perms in Intro/Setup/Concierge flows
    Button(
        enabled = permissionAskState != PermissionAskState.HAS_PERMS,
        onClick = {
            when (permissionAskState) {
                PermissionAskState.ASKED_ZERO -> {
                    locationPermissionState.launchPermissionRequest()
                    conciergeVM.saveAskLocationPermissionCount(1)
                }
                PermissionAskState.ASKED_ONCE -> {
                    locationPermissionState.launchPermissionRequest()
                    conciergeVM.saveAskLocationPermissionCount(2)
                }
                PermissionAskState.ASKED_TWICE, null -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    openSettingsLauncher.launch(intent)
                    conciergeVM.saveAskLocationPermissionCount(3)
                }
                PermissionAskState.HAS_PERMS -> {
                    conciergeVM.saveAskLocationPermissionCount(1)
                    appLevelNav.navigate(MainActivity.AppScreens.Home.name)
                }
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



