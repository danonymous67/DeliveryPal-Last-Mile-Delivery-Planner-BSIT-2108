package com.example.deliverypal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliverypal.ui.ComposeMapView
import com.example.deliverypal.ui.theme.DeliveryPalTheme
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    STEP1_SENDER_INFO,
    STEP2_PACKAGE_DETAILS,
    STEP3_SENDER_ADDRESS,
    STEP4_SENDER_PIN,
    STEP5_RECIPIENT_ADDRESS,
    STEP6_RECIPIENT_PIN,
    STEP7_SUMMARY,
    STEP8_RIDER_SELECTION
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidGraphicFactory.createInstance(application)

        setContent {
            DeliveryPalTheme {
                DeliveryPalApp()
            }
        }
    }

    override fun onDestroy() {
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }
}

@Composable
fun DeliveryPalApp() {
    val context = LocalContext.current

    // Screen State
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    // Active Deliveries List
    val activeDeliveries = remember { mutableStateListOf<Delivery>() }

    // Sender Info & Address State
    var senderFirstName by remember { mutableStateOf("") }
    var senderLastName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var senderStreet by remember { mutableStateOf("") }
    var senderBarangay by remember { mutableStateOf("") }
    var senderLandmark by remember { mutableStateOf("") }

    // Package Details State
    var itemDescription by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Normal") }

    // Recipient Info & Address State
    var recipientFirstName by remember { mutableStateOf("") }
    var recipientLastName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var recipientStreet by remember { mutableStateOf("") }
    var recipientBarangay by remember { mutableStateOf("") }
    var recipientLandmark by remember { mutableStateOf("") }

    // Map Pin Coordinates
    var pickupLat by remember { mutableDoubleStateOf(14.0722) }
    var pickupLng by remember { mutableDoubleStateOf(120.6306) }
    var destLat by remember { mutableDoubleStateOf(14.0780) }
    var destLng by remember { mutableDoubleStateOf(120.6350) }

    // Real OSRM Road Route Points
    var routePoints by remember { mutableStateOf<List<LatLong>>(emptyList()) }

    LaunchedEffect(pickupLat, pickupLng, destLat, destLng) {
        if (pickupLat != 0.0 && pickupLng != 0.0 && destLat != 0.0 && destLng != 0.0) {
            routePoints = RouteRepository.fetchRoadRoute(pickupLat, pickupLng, destLat, destLng)
        } else {
            routePoints = emptyList()
        }
    }

    // Pending Delivery Request & Selected Rider
    var pendingDelivery by remember { mutableStateOf<Delivery?>(null) }
    var selectedRider by remember { mutableStateOf<Rider?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (currentScreen != AppScreen.DASHBOARD) {
                val currentStepNum = when (currentScreen) {
                    AppScreen.STEP1_SENDER_INFO -> 1
                    AppScreen.STEP2_PACKAGE_DETAILS -> 2
                    AppScreen.STEP3_SENDER_ADDRESS -> 3
                    AppScreen.STEP4_SENDER_PIN -> 4
                    AppScreen.STEP5_RECIPIENT_ADDRESS -> 5
                    AppScreen.STEP6_RECIPIENT_PIN -> 6
                    AppScreen.STEP7_SUMMARY -> 7
                    AppScreen.STEP8_RIDER_SELECTION -> 8
                    else -> 1
                }
                StepProgressHeader(currentStep = currentStepNum)
            }

            Box(modifier = Modifier.weight(1f)) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen(
                        deliveries = activeDeliveries,
                        onSendPackageClick = {
                            senderFirstName = ""
                            senderLastName = ""
                            senderPhone = ""
                            senderStreet = ""
                            senderBarangay = ""
                            senderLandmark = ""

                            itemDescription = ""
                            weightText = ""
                            quantityText = ""
                            priority = "Normal"

                            recipientFirstName = ""
                            recipientLastName = ""
                            recipientPhone = ""
                            recipientStreet = ""
                            recipientBarangay = ""
                            recipientLandmark = ""

                            pickupLat = 14.0722
                            pickupLng = 120.6306
                            destLat = 14.0780
                            destLng = 120.6350

                            currentScreen = AppScreen.STEP1_SENDER_INFO
                        }
                    )

                    AppScreen.STEP1_SENDER_INFO -> Step1SenderInfo(
                        firstName = senderFirstName,
                        onFirstNameChange = { senderFirstName = it },
                        lastName = senderLastName,
                        onLastNameChange = { senderLastName = it },
                        phone = senderPhone,
                        onPhoneChange = { senderPhone = it },
                        onProceed = {
                            if (senderFirstName.isBlank() || senderLastName.isBlank() || senderPhone.isBlank()) {
                                Toast.makeText(context, "Please enter your name and phone number", Toast.LENGTH_SHORT).show()
                            } else {
                                currentScreen = AppScreen.STEP2_PACKAGE_DETAILS
                            }
                        }
                    )

                    AppScreen.STEP2_PACKAGE_DETAILS -> Step2PackageDetails(
                        description = itemDescription,
                        onDescriptionChange = { itemDescription = it },
                        weight = weightText,
                        onWeightChange = { weightText = it },
                        quantity = quantityText,
                        onQuantityChange = { quantityText = it },
                        priority = priority,
                        onPriorityChange = { priority = it },
                        onBack = { currentScreen = AppScreen.STEP1_SENDER_INFO },
                        onProceed = {
                            if (itemDescription.isBlank()) {
                                Toast.makeText(context, "Please describe package contents", Toast.LENGTH_SHORT).show()
                            } else {
                                currentScreen = AppScreen.STEP3_SENDER_ADDRESS
                            }
                        }
                    )

                    AppScreen.STEP3_SENDER_ADDRESS -> Step3SenderAddress(
                        street = senderStreet,
                        onStreetChange = { senderStreet = it },
                        barangay = senderBarangay,
                        onBarangayChange = { senderBarangay = it },
                        landmark = senderLandmark,
                        onLandmarkChange = { senderLandmark = it },
                        onBack = { currentScreen = AppScreen.STEP2_PACKAGE_DETAILS },
                        onProceed = { currentScreen = AppScreen.STEP4_SENDER_PIN }
                    )

                    AppScreen.STEP4_SENDER_PIN -> Step4SenderPinMap(
                        pickupLat = pickupLat,
                        pickupLng = pickupLng,
                        onMapTap = { lat, lng ->
                            pickupLat = lat
                            pickupLng = lng
                            MapUtils.reverseGeocode(context, lat, lng) { str, brgy ->
                                if (senderStreet.isBlank()) senderStreet = str
                                if (senderBarangay.isBlank()) senderBarangay = brgy
                            }
                        },
                        onBack = { currentScreen = AppScreen.STEP3_SENDER_ADDRESS },
                        onProceed = { currentScreen = AppScreen.STEP5_RECIPIENT_ADDRESS }
                    )

                    AppScreen.STEP5_RECIPIENT_ADDRESS -> Step5RecipientAddress(
                        firstName = recipientFirstName,
                        onFirstNameChange = { recipientFirstName = it },
                        lastName = recipientLastName,
                        onLastNameChange = { recipientLastName = it },
                        phone = recipientPhone,
                        onPhoneChange = { recipientPhone = it },
                        street = recipientStreet,
                        onStreetChange = { recipientStreet = it },
                        barangay = recipientBarangay,
                        onBarangayChange = { recipientBarangay = it },
                        landmark = recipientLandmark,
                        onLandmarkChange = { recipientLandmark = it },
                        onBack = { currentScreen = AppScreen.STEP4_SENDER_PIN },
                        onProceed = {
                            if (recipientFirstName.isBlank() || recipientLastName.isBlank() || recipientPhone.isBlank()) {
                                Toast.makeText(context, "Please enter recipient name and phone number", Toast.LENGTH_SHORT).show()
                            } else {
                                currentScreen = AppScreen.STEP6_RECIPIENT_PIN
                            }
                        }
                    )

                    AppScreen.STEP6_RECIPIENT_PIN -> Step6RecipientPinMap(
                        pickupLat = pickupLat,
                        pickupLng = pickupLng,
                        destLat = destLat,
                        destLng = destLng,
                        routePoints = routePoints,
                        onMapTap = { lat, lng ->
                            destLat = lat
                            destLng = lng
                            MapUtils.reverseGeocode(context, lat, lng) { str, brgy ->
                                if (recipientStreet.isBlank()) recipientStreet = str
                                if (recipientBarangay.isBlank()) recipientBarangay = brgy
                            }
                        },
                        onBack = { currentScreen = AppScreen.STEP5_RECIPIENT_ADDRESS },
                        onProceed = {
                            val weightVal = weightText.toDoubleOrNull() ?: 1.0
                            val qtyVal = quantityText.toIntOrNull() ?: 1

                            val fullName = "$senderFirstName $senderLastName".trim()
                            val recipientFullName = "$recipientFirstName $recipientLastName".trim()

                            val sender = Sender(fullName, senderPhone)
                            sender.setSenderAddress(senderStreet, senderBarangay, senderLandmark, pickupLat, pickupLng)

                            val delivery = sender.createDeliveryRequest(
                                orderId = "DEL-" + System.currentTimeMillis().toString().takeLast(6),
                                recipientName = recipientFullName,
                                recipientPhone = recipientPhone,
                                street = recipientStreet,
                                baranggay = recipientBarangay,
                                landmark = recipientLandmark,
                                description = itemDescription,
                                weight = weightVal,
                                quantity = qtyVal,
                                priority = priority,
                                pickupLat = pickupLat,
                                pickupLng = pickupLng,
                                destLat = destLat,
                                destLng = destLng
                            )

                            pendingDelivery = delivery

                            // Pre-select nearest rider by default
                            val nearest = RiderRepository.findNearestRider(pickupLat, pickupLng)
                            selectedRider = nearest ?: RiderRepository.riders.firstOrNull()

                            currentScreen = AppScreen.STEP7_SUMMARY
                        }
                    )

                    AppScreen.STEP7_SUMMARY -> Step7DeliverySummary(
                        delivery = pendingDelivery,
                        assignedRider = selectedRider,
                        pickupLat = pickupLat,
                        pickupLng = pickupLng,
                        destLat = destLat,
                        destLng = destLng,
                        routePoints = routePoints,
                        onBack = { currentScreen = AppScreen.STEP6_RECIPIENT_PIN },
                        onProceed = { currentScreen = AppScreen.STEP8_RIDER_SELECTION }
                    )

                    AppScreen.STEP8_RIDER_SELECTION -> Step8RiderSelection(
                        pickupLat = pickupLat,
                        pickupLng = pickupLng,
                        selectedRider = selectedRider,
                        onRiderSelect = { selectedRider = it },
                        onBack = { currentScreen = AppScreen.STEP7_SUMMARY },
                        onConfirm = {
                            val delivery = pendingDelivery
                            val rider = selectedRider
                            if (delivery != null && rider != null) {
                                delivery.assignRider(rider)
                                activeDeliveries.add(0, delivery)
                                Toast.makeText(context, "Delivery confirmed with ${rider.name}!", Toast.LENGTH_SHORT).show()
                            }
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    deliveries: List<Delivery>,
    onSendPackageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard Header Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome to DeliveryPal 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Last-Mile Delivery Service",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSendPackageClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("📦 Send a Package", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            text = "Active Deliveries (${deliveries.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        if (deliveries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No active delivery requests yet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap 'Send a Package' above to start your first delivery!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(deliveries) { delivery ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ID: ${delivery.deliveryId}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(delivery.status, fontWeight = FontWeight.Bold) }
                                )
                            }

                            Text(
                                text = "Recipient: ${delivery.recipient.name} (${delivery.recipient.phone})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Item: ${delivery.packageDetails.description}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            delivery.rider?.let { rider ->
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    text = "🛵 Assigned Rider: ${rider.name} (${rider.phone})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepProgressHeader(currentStep: Int) {
    val steps = listOf("1. Sender", "2. Package", "3. Address", "4. Pickup Pin", "5. Recipient", "6. Dest Pin", "7. Summary", "8. Rider")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, title ->
                val stepNum = index + 1
                val isActive = stepNum <= currentStep
                val isCurrent = stepNum == currentStep

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) MaterialTheme.colorScheme.primary
                                else if (isActive) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$stepNum",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimary
                            else if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title.substringAfter(". "),
                        fontSize = 8.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { currentStep / 8f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun Step1SenderInfo(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Sender Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = onFirstNameChange,
                        label = { Text("First Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = onLastNameChange,
                        label = { Text("Last Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone Number") },
                    placeholder = { Text("09XXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Step2PackageDetails(
    description: String,
    onDescriptionChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    priority: String,
    onPriorityChange: (String) -> Unit,
    onBack: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Package Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Item Description / Contents") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = onWeightChange,
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = onQuantityChange,
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Priority Level",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Normal", "Urgent", "Relaxed").forEach { level ->
                        val isSelected = (priority == level)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onPriorityChange(level) },
                            label = { Text(level) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Step3SenderAddress(
    street: String,
    onStreetChange: (String) -> Unit,
    barangay: String,
    onBarangayChange: (String) -> Unit,
    landmark: String,
    onLandmarkChange: (String) -> Unit,
    onBack: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Sender Address",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = street,
                        onValueChange = onStreetChange,
                        label = { Text("Street Address") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = barangay,
                        onValueChange = onBarangayChange,
                        label = { Text("Barangay") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = landmark,
                    onValueChange = onLandmarkChange,
                    label = { Text("Landmark / Extra Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Step4SenderPinMap(
    pickupLat: Double,
    pickupLng: Double,
    onMapTap: (Double, Double) -> Unit,
    onBack: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "🟢 Set Pick-Up Pin on Map",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
        ) {
            ComposeMapView(
                pickupLat = pickupLat,
                pickupLng = pickupLng,
                allRiders = RiderRepository.riders,
                onTapLocation = onMapTap,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Step5RecipientAddress(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    street: String,
    onStreetChange: (String) -> Unit,
    barangay: String,
    onBarangayChange: (String) -> Unit,
    landmark: String,
    onLandmarkChange: (String) -> Unit,
    onBack: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Recipient Info & Address",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = onFirstNameChange,
                        label = { Text("First Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = onLastNameChange,
                        label = { Text("Last Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone Number") },
                    placeholder = { Text("09XXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = street,
                        onValueChange = onStreetChange,
                        label = { Text("Street Address") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = barangay,
                        onValueChange = onBarangayChange,
                        label = { Text("Barangay") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = landmark,
                    onValueChange = onLandmarkChange,
                    label = { Text("Landmark / Extra Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Step6RecipientPinMap(
    pickupLat: Double,
    pickupLng: Double,
    destLat: Double,
    destLng: Double,
    routePoints: List<LatLong>,
    onMapTap: (Double, Double) -> Unit,
    onBack: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "🔴 Set Destination Pin on Map",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC62828)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
        ) {
            ComposeMapView(
                pickupLat = pickupLat,
                pickupLng = pickupLng,
                destLat = destLat,
                destLng = destLng,
                routePoints = routePoints,
                allRiders = RiderRepository.riders,
                onTapLocation = onMapTap,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Step7DeliverySummary(
    delivery: Delivery?,
    assignedRider: Rider?,
    pickupLat: Double,
    pickupLng: Double,
    destLat: Double,
    destLng: Double,
    routePoints: List<LatLong>,
    onBack: () -> Unit,
    onProceed: () -> Unit
) {
    if (delivery == null || assignedRider == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No delivery request found.")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Delivery Request Summary (${delivery.deliveryId})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                Text(text = "Sender: ${delivery.sender.name} (${delivery.sender.phone})", fontSize = 13.sp)
                Text(text = "Recipient: ${delivery.recipient.name} (${delivery.recipient.phone})", fontSize = 13.sp)
                Text(text = "Destination: ${delivery.recipient.getRecipientAddress()}", fontSize = 13.sp)
                Text(text = "Item: ${delivery.packageDetails.description}", fontSize = 13.sp)
                Text(text = "Weight: ${delivery.packageDetails.weight} kg  •  Qty: ${delivery.packageDetails.quantity}", fontSize = 13.sp)
                Text(text = "Priority: ${delivery.recipient.priorityLevel}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Route Map
        ComposeMapView(
            pickupLat = pickupLat,
            pickupLng = pickupLng,
            destLat = destLat,
            destLng = destLng,
            routePoints = routePoints,
            assignedRider = assignedRider,
            allRiders = RiderRepository.riders,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Step8RiderSelection(
    pickupLat: Double,
    pickupLng: Double,
    selectedRider: Rider?,
    onRiderSelect: (Rider) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val riders = RiderRepository.riders
    val nearestRider = remember(pickupLat, pickupLng) { RiderRepository.findNearestRider(pickupLat, pickupLng) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🛵 Select Delivery Rider",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "The nearest rider is pre-selected by default. You may choose any rider below.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(riders) { rider ->
                val isSelected = (selectedRider?.name == rider.name)
                val isNearest = (nearestRider?.name == rider.name)
                val distanceKm = rider.distanceTo(pickupLat, pickupLng)

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRiderSelect(rider) }
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = rider.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (isNearest) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("Nearest", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }

                            Text(
                                text = "📞 ${rider.phone}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "📍 ${String.format(Locale.US, "%.2f km away", distanceKm)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { onRiderSelect(rider) }
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
