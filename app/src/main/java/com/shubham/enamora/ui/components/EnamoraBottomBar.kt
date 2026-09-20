package com.shubham.enamora.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shubham.enamora.navigation.EnamoraDestination
import com.shubham.enamora.ui.theme.EnamoraObsidian
import com.shubham.enamora.ui.theme.EnamoraOutline
import com.shubham.enamora.ui.theme.EnamoraRose
import com.shubham.enamora.ui.theme.EnamoraTextSecondary

@Composable
fun EnamoraBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier.drawBehind {
            drawLine(
                color = EnamoraOutline.copy(alpha = 0.65f),
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        },
        containerColor = EnamoraObsidian,
        tonalElevation = 0.dp
    ) {
        EnamoraDestination.bottomNavigationItems.forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    EnamoraDestinationIcon(
                        destination = destination,
                        selected = isSelected
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EnamoraRose,
                    selectedTextColor = EnamoraRose,
                    unselectedIconColor = EnamoraTextSecondary,
                    unselectedTextColor = EnamoraTextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun EnamoraDestinationIcon(
    destination: EnamoraDestination,
    selected: Boolean
) {
    val iconColor = if (selected) EnamoraRose else EnamoraTextSecondary

    Canvas(
        modifier = Modifier.size(22.dp)
    ) {
        val iconWidth = size.width
        val iconHeight = size.height

        val outline = Stroke(
            width = if (selected) 2.dp.toPx() else 1.6.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (destination) {
            EnamoraDestination.HOME -> {
                val roof = Path().apply {
                    moveTo(iconWidth * 0.14f, iconHeight * 0.48f)
                    lineTo(iconWidth * 0.50f, iconHeight * 0.17f)
                    lineTo(iconWidth * 0.86f, iconHeight * 0.48f)
                }

                val house = Path().apply {
                    moveTo(iconWidth * 0.23f, iconHeight * 0.43f)
                    lineTo(iconWidth * 0.23f, iconHeight * 0.84f)
                    lineTo(iconWidth * 0.77f, iconHeight * 0.84f)
                    lineTo(iconWidth * 0.77f, iconHeight * 0.43f)
                }

                drawPath(
                    path = roof,
                    color = iconColor,
                    style = outline
                )

                drawPath(
                    path = house,
                    color = iconColor,
                    style = outline
                )

                drawLine(
                    color = iconColor,
                    start = Offset(
                        iconWidth * 0.50f,
                        iconHeight * 0.84f
                    ),
                    end = Offset(
                        iconWidth * 0.50f,
                        iconHeight * 0.64f
                    ),
                    strokeWidth = outline.width,
                    cap = StrokeCap.Round
                )
            }

            EnamoraDestination.EXPLORE -> {
                drawCircle(
                    color = iconColor,
                    radius = iconWidth * 0.27f,
                    center = Offset(
                        iconWidth * 0.43f,
                        iconHeight * 0.42f
                    ),
                    style = outline
                )

                drawLine(
                    color = iconColor,
                    start = Offset(
                        iconWidth * 0.63f,
                        iconHeight * 0.63f
                    ),
                    end = Offset(
                        iconWidth * 0.85f,
                        iconHeight * 0.85f
                    ),
                    strokeWidth = outline.width,
                    cap = StrokeCap.Round
                )
            }

            EnamoraDestination.CHAT -> {
                drawRoundRect(
                    color = iconColor,
                    topLeft = Offset(
                        iconWidth * 0.10f,
                        iconHeight * 0.14f
                    ),
                    size = Size(
                        iconWidth * 0.80f,
                        iconHeight * 0.62f
                    ),
                    cornerRadius = CornerRadius(
                        iconWidth * 0.18f,
                        iconWidth * 0.18f
                    ),
                    style = outline
                )

                val tail = Path().apply {
                    moveTo(iconWidth * 0.33f, iconHeight * 0.75f)
                    lineTo(iconWidth * 0.22f, iconHeight * 0.88f)
                    lineTo(iconWidth * 0.49f, iconHeight * 0.76f)
                }

                drawPath(
                    path = tail,
                    color = iconColor,
                    style = outline
                )
            }

            EnamoraDestination.ABOUT -> {
                drawCircle(
                    color = iconColor,
                    radius = iconWidth * 0.16f,
                    center = Offset(
                        iconWidth * 0.50f,
                        iconHeight * 0.29f
                    ),
                    style = outline
                )

                drawArc(
                    color = iconColor,
                    startAngle = 195f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(
                        iconWidth * 0.18f,
                        iconHeight * 0.48f
                    ),
                    size = Size(
                        iconWidth * 0.64f,
                        iconHeight * 0.40f
                    ),
                    style = outline
                )
            }
        }
    }
}