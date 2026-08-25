package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BusinessProfileEntity
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantCanvas
import com.example.ui.theme.VibrantOnPurpleContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantSurface

@Composable
fun SettingsScreen(
    profile: BusinessProfileEntity?,
    onSaveProfile: (BusinessProfileEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var businessName by remember(profile) { mutableStateOf(profile?.businessName ?: "") }
    var ownerName by remember(profile) { mutableStateOf(profile?.ownerName ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var gstin by remember(profile) { mutableStateOf(profile?.gstin ?: "") }
    var address by remember(profile) { mutableStateOf(profile?.address ?: "") }
    var upiId by remember(profile) { mutableStateOf(profile?.upiId ?: "") }
    var bankName by remember(profile) { mutableStateOf(profile?.bankName ?: "") }
    var accountNumber by remember(profile) { mutableStateOf(profile?.accountNumber ?: "") }
    var ifscCode by remember(profile) { mutableStateOf(profile?.ifscCode ?: "") }
    var termsAndConditions by remember(profile) { mutableStateOf(profile?.termsAndConditions ?: "") }

    var isSavedSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantCanvas),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Business Header Profile Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantPurpleContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, VibrantPurple.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(VibrantPurple)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (businessName.isNotBlank()) businessName else "Modern Traders",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantOnPurpleContainer
                        )
                        Text(
                            text = if (gstin.isNotBlank()) "GSTIN: $gstin" else "GST Billing Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VibrantPurple
                        )
                    }
                }
            }
        }

        // Section 1: Business Information
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("BUSINESS INFORMATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSlate400)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business / Enterprise Name *") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("settings_business_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Proprietor / Owner Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Business Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = gstin,
                            onValueChange = { gstin = it.uppercase() },
                            label = { Text("GSTIN") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Registered Business Address") },
                        maxLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 2: Bank & UPI Payment Details
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BANK & UPI QR FOR INVOICES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSlate400)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text("UPI ID (for Instant Payment)") },
                        placeholder = { Text("shopname@okhdfcbank") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank Name") },
                        placeholder = { Text("HDFC Bank, State Bank of India") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it },
                            label = { Text("Account Number") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            ),
                            modifier = Modifier.weight(1.4f)
                        )

                        OutlinedTextField(
                            value = ifscCode,
                            onValueChange = { ifscCode = it.uppercase() },
                            label = { Text("IFSC Code") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section 3: Terms and Conditions
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("INVOICE TERMS & CONDITIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSlate400)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = termsAndConditions,
                        onValueChange = { termsAndConditions = it },
                        label = { Text("Terms printed on footer of bills") },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = BusinessProfileEntity(
                        id = profile?.id ?: 1,
                        businessName = businessName.trim(),
                        ownerName = ownerName.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        gstin = gstin.trim(),
                        address = address.trim(),
                        state = profile?.state ?: "Maharashtra",
                        upiId = upiId.trim(),
                        bankName = bankName.trim(),
                        accountNumber = accountNumber.trim(),
                        ifscCode = ifscCode.trim(),
                        termsAndConditions = termsAndConditions.trim()
                    )
                    onSaveProfile(updated)
                    isSavedSnackbar = true
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_business_profile_btn")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile Changes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            if (isSavedSnackbar) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IncomeGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Business Profile successfully updated!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
