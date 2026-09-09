package com.agri.costtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.Primary

enum class AgriScreen(val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    DASHBOARD(Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    FARMERS(Icons.Filled.People, Icons.Outlined.People),
    RATES(Icons.Filled.Payments, Icons.Outlined.Payments),
    HISTORY(Icons.Filled.History, Icons.Outlined.History)
}

@Composable
fun AgriBottomNavBar(
    currentScreen: AgriScreen,
    strings: AppStrings,
    onScreenSelected: (AgriScreen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                ambientColor = Color(0x1A2C160E),
                spotColor = Color(0x1A2C160E)
            ),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFFFAF9F6)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AgriScreen.entries.forEach { screen ->
                val selected = currentScreen == screen
                val containerColor = if (selected) Color(0xFFDCFCE7) else Color.Transparent
                val contentColor = if (selected) Primary else Color(0xFF78716C)

                val screenTitle = when (screen) {
                    AgriScreen.DASHBOARD -> strings.navDashboard
                    AgriScreen.FARMERS -> strings.navFarmers
                    AgriScreen.RATES -> strings.navRates
                    AgriScreen.HISTORY -> strings.navHistory
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(containerColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onScreenSelected(screen)
                        }
                        .padding(horizontal = if (selected) 12.dp else 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (selected) screen.activeIcon else screen.inactiveIcon,
                            contentDescription = screenTitle,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = screenTitle,
                            fontSize = 9.5.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.3.sp,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
