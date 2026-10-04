package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.location.GeocodingService
import com.example.data.model.CityLocation
import com.example.data.model.KundliProfile
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProfileDialog(
    initialProfile: KundliProfile? = null,
    onDismiss: () -> Unit,
    onSave: (KundliProfile) -> Unit
) {
    var name by remember { mutableStateOf(initialProfile?.name ?: "") }
    var gender by remember { mutableStateOf(initialProfile?.gender ?: "Male") }

    // Date
    val initialDateParts = (initialProfile?.birthDate ?: "1998-07-15").split("-")
    var birthYear by remember { mutableStateOf(initialDateParts.getOrElse(0) { "1998" }) }
    var birthMonth by remember { mutableStateOf(initialDateParts.getOrElse(1) { "07" }) }
    var birthDay by remember { mutableStateOf(initialDateParts.getOrElse(2) { "15" }) }

    // Time
    val initialTimeParts = (initialProfile?.birthTime ?: "10:30").split(":")
    var birthHour by remember { mutableStateOf(initialTimeParts.getOrElse(0) { "10" }) }
    var birthMinute by remember { mutableStateOf(initialTimeParts.getOrElse(1) { "30" }) }

    // Place & Coordinates
    var placeName by remember { mutableStateOf(initialProfile?.birthPlace ?: "Ramban, Jammu and Kashmir, India") }
    var latitude by remember { mutableStateOf(initialProfile?.latitude?.toString() ?: "33.2427") }
    var longitude by remember { mutableStateOf(initialProfile?.longitude?.toString() ?: "75.2415") }
    var timezoneOffset by remember { mutableStateOf(initialProfile?.timezoneOffset?.toString() ?: "5.5") }

    var showManualCoordinates by remember { mutableStateOf(false) }
    var showCitySearch by remember { mutableStateOf(false) }
    var isFetchingCoordinates by remember { mutableStateOf(false) }
    var coordinateFetchFeedback by remember { mutableStateOf<String?>(null) }
    var cityQuery by remember { mutableStateOf(placeName) }
    var searchResults by remember { mutableStateOf<List<CityLocation>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_edit_profile_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (initialProfile == null) "Nayi Kundli Janam Vivaran" else "Janam Vivaran Edit Karein",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SacredGold
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("dialog_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Name Input
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Pura Naam (Full Name)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_full_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SacredGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = SacredGold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Gender Selector
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(Pair("Male", "Purush"), Pair("Female", "Mahila"), Pair("Other", "Anya")).forEach { (key, label) ->
                            val isSelected = gender == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { gender = key },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SacredGold,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Date of Birth
                item {
                    Text(
                        text = "Janam Tithi (Date of Birth)",
                        style = MaterialTheme.typography.labelMedium,
                        color = SacredGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = birthDay,
                            onValueChange = { if (it.length <= 2) birthDay = it },
                            label = { Text("Din (Day)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_birth_day")
                        )
                        OutlinedTextField(
                            value = birthMonth,
                            onValueChange = { if (it.length <= 2) birthMonth = it },
                            label = { Text("Mahina (Mo)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("input_birth_month")
                        )
                        OutlinedTextField(
                            value = birthYear,
                            onValueChange = { if (it.length <= 4) birthYear = it },
                            label = { Text("Saal (Year)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("input_birth_year")
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Time of Birth (24hr)
                item {
                    Text(
                        text = "Janam Samay (Time of Birth - 24 Ghante Format)",
                        style = MaterialTheme.typography.labelMedium,
                        color = SacredGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = birthHour,
                            onValueChange = { if (it.length <= 2) birthHour = it },
                            label = { Text("Ghanta (0-23)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_birth_hour")
                        )
                        OutlinedTextField(
                            value = birthMinute,
                            onValueChange = { if (it.length <= 2) birthMinute = it },
                            label = { Text("Minute (0-59)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_birth_minute")
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Birth City Manual Search & Coordinates Fetcher
                item {
                    Text(
                        text = "Janam Sthan & Shehar Search (Location)",
                        style = MaterialTheme.typography.labelMedium,
                        color = SacredGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Manual Text Field for typing town/city name directly
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = {
                            placeName = it
                            coordinateFetchFeedback = null
                        },
                        label = { Text("Shehar, Qasba ya Gaon (jaise Ramban, Jammu and Kashmir)") },
                        placeholder = { Text("Shehar ka naam likhein jaise Ramban, Jammu and Kashmir") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = SacredGold
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                cityQuery = placeName
                                showCitySearch = true
                                coroutineScope.launch {
                                    searchResults = GeocodingService.searchCoordinates(placeName)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = SacredGold
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_city_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SacredGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = SacredGold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick "Fetch Coordinates" Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (placeName.isBlank()) return@Button
                                isFetchingCoordinates = true
                                coordinateFetchFeedback = null
                                coroutineScope.launch {
                                    val results = GeocodingService.searchCoordinates(placeName)
                                    val matched = results.firstOrNull()
                                    if (matched != null) {
                                        latitude = matched.latitude.toString()
                                        longitude = matched.longitude.toString()
                                        timezoneOffset = matched.timezone.toString()
                                        placeName = "${matched.name}, ${matched.stateOrCountry}"
                                        coordinateFetchFeedback = "Coordinates resolved: ${matched.latitude}° N, ${matched.longitude}° E"
                                    } else {
                                        coordinateFetchFeedback = "No match found. Please verify spelling or set manually below."
                                    }
                                    isFetchingCoordinates = false
                                }
                            },
                            enabled = !isFetchingCoordinates && placeName.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SacredGoldLight,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("fetch_coordinates_button")
                        ) {
                            if (isFetchingCoordinates) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Khoj rahe hain...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Coordinates Khojein", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        TextButton(
                            onClick = { showManualCoordinates = !showManualCoordinates },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (showManualCoordinates) Icons.Default.ExpandLess else Icons.Default.Tune,
                                contentDescription = null,
                                tint = SacredGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showManualCoordinates) "Coordinates Chhupayein" else "Lat/Lon Badlein",
                                color = SacredGold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (coordinateFetchFeedback != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = coordinateFetchFeedback!!,
                            color = if (coordinateFetchFeedback!!.startsWith("Coordinates") || coordinateFetchFeedback!!.startsWith("Selected")) Color(0xFF69F0AE) else MaterialTheme.colorScheme.error,
                            fontSize = 11.sp
                        )
                    }

                    // Display Current Resolved Coordinates Badge
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CosmicSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lat: $latitude° N • Lon: $longitude° E • TZ: UTC+$timezoneOffset",
                                fontSize = 11.sp,
                                color = SacredGoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Manual Coordinates Editor (Expandable)
                    AnimatedVisibility(visible = showManualCoordinates) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Custom Geographic Coordinates:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SacredGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = latitude,
                                    onValueChange = { latitude = it },
                                    label = { Text("Latitude", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_latitude")
                                )
                                OutlinedTextField(
                                    value = longitude,
                                    onValueChange = { longitude = it },
                                    label = { Text("Longitude", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_longitude")
                                )
                                OutlinedTextField(
                                    value = timezoneOffset,
                                    onValueChange = { timezoneOffset = it },
                                    label = { Text("TZ Offset", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_timezone")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Save Button
                item {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please enter a valid name."
                                return@Button
                            }
                            val d = birthDay.padStart(2, '0')
                            val m = birthMonth.padStart(2, '0')
                            val y = birthYear
                            val h = birthHour.padStart(2, '0')
                            val min = birthMinute.padStart(2, '0')

                            val lat = latitude.toDoubleOrNull() ?: 33.2427
                            val lon = longitude.toDoubleOrNull() ?: 75.2415
                            val tz = timezoneOffset.toDoubleOrNull() ?: 5.5

                            val newProfile = KundliProfile(
                                id = initialProfile?.id ?: 0,
                                name = name.trim(),
                                gender = gender,
                                birthDate = "$y-$m-$d",
                                birthTime = "$h:$min",
                                birthPlace = placeName,
                                latitude = lat,
                                longitude = lon,
                                timezoneOffset = tz,
                                isDefault = initialProfile?.isDefault ?: true
                            )
                            onSave(newProfile)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_kundli_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SacredGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Kundli Banayein (Calculate)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    // City Search & Geocoding Results Dialog
    if (showCitySearch) {
        Dialog(onDismissRequest = { showCitySearch = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Shehar ya Qasba Khojein",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = "Bharat aur vishva ke kisi bhi shehar ya gaon ka naam likhein",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cityQuery,
                        onValueChange = {
                            cityQuery = it
                            coroutineScope.launch {
                                searchResults = GeocodingService.searchCoordinates(it)
                            }
                        },
                        placeholder = { Text("e.g. Ramban, Jammu and Kashmir") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SacredGold) },
                        trailingIcon = {
                            if (cityQuery.isNotBlank()) {
                                IconButton(onClick = { cityQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_city_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(searchResults) { city ->
                            ListItem(
                                headlineContent = {
                                    Text(city.name, fontWeight = FontWeight.Bold, color = Color.White)
                                },
                                supportingContent = {
                                    Column {
                                        Text(city.stateOrCountry, color = Color.LightGray, fontSize = 12.sp)
                                        Text(
                                            "Lat: ${city.latitude}° N, Lon: ${city.longitude}° E (UTC+${city.timezone})",
                                            color = SacredGoldLight,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                leadingContent = {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = SacredGold)
                                },
                                modifier = Modifier
                                    .clickable {
                                        placeName = "${city.name}, ${city.stateOrCountry}"
                                        latitude = city.latitude.toString()
                                        longitude = city.longitude.toString()
                                        timezoneOffset = city.timezone.toString()
                                        coordinateFetchFeedback = "Selected: ${city.name} (${city.latitude}° N, ${city.longitude}° E)"
                                        showCitySearch = false
                                    }
                            )
                            HorizontalDivider(color = Color.DarkGray)
                        }

                        if (searchResults.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Type town name to search online & offline...",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                // Direct accept what's typed
                                if (cityQuery.isNotBlank()) {
                                    placeName = cityQuery
                                }
                                showCitySearch = false
                            }
                        ) {
                            Text("Yahi Naam Chunein", color = SacredGoldLight)
                        }

                        TextButton(
                            onClick = { showCitySearch = false }
                        ) {
                            Text("Band Karein", color = SacredGold)
                        }
                    }
                }
            }
        }
    }
}
