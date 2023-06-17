// I put this outside of the @Composables at top of file
    // The main entry point for interacting with the Fused Location Provider
    private lateinit var fusedLocationClient: FusedLocationProviderClient
// I put this inside the first Main Composable at the top of the file
    val context = LocalContext.current
    // initiate geo-location provider object/api/interface thingy majigy
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
// I put this right after the above statements at the top of the Main Composable
    // Location permission state
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    // If user does not have location permission, send them back to Concierge flow.
    if (!locationPermissionState.status.isGranted){
        Toast.makeText(context, "Enable Permissions: FINE LOCATION", Toast.LENGTH_LONG).show()
        appLevelNav.navigate(MainActivity.AppScreens.Concierge.name)
    }







        // is user locked onto currentLocation or are they panning around the map(ie: creating a fence/etc)
        val mapViewPortMode = rememberSaveable { mutableStateOf(MapViewPortMode.CURRENT_LOCATION) }
        // does the app of the permissions required? If they are ever removed the user should be shot back to Concierge
        val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
        // last known location
        val lastKnownLocation by remember { mutableStateOf(LatLngZoom(0.0, 0.0, 0.0f)) }
        // the object representing where the camera/viewport for google map is currently located, lat/long + zoom level
        val cameraPosState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(lastKnownLocation.lat, lastKnownLocation.long), 14f)
        }
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locResult: LocationResult) {
                Log.d("CarsonDebug", "HomeScreen --- locationCallback --- onLocationResult(...) -> ${locResult.locations.joinToString(", ")}")
                locResult.lastLocation?.let { lastLoc ->
                    cameraPosState.position = CameraPosition.fromLatLngZoom(LatLng(lastLoc.latitude, lastLoc.longitude), cameraPosState.position.zoom)
                }

            }
        }

        // -- Pass new known locations
        // --- onNewLocation -> if mapViewPortMode == CURRENT_LOCATION -> move GoogleMapsCenter to currentLocation
        LaunchedEffect(permissionState){
            when (permissionState.status){
                is PermissionStatus.Granted -> {
                    // create request object
                    val locationRequest: LocationRequest =
                        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
                            setMinUpdateDistanceMeters(1f)
                            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                            setWaitForAccurateLocation(true)
                        }.build()
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                }
                is PermissionStatus.Denied -> {
                    // Send user to Concierge to request permissions
                    Toast.makeText(context, "Enable Permissions: FINE LOCATION", Toast.LENGTH_LONG).show()
                    appLevelNav.navigate(MainActivity.AppScreens.Concierge.name)
                }
            }

            // cleapup
            onDispose {
                // remove callbacks
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
