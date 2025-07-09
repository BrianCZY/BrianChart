package com.czy.brianchart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.czy.brianchart.ui.navigation.Route

@Composable
fun ChartApp() {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        ChartNavigationActions(navController)

    }
    Surface {

        NavHost(
            modifier = Modifier,
            navController = navController,
            startDestination = Route.Home,
        ) {
            composable<Route.Home> {
                Home(navigationActions)
            }
            composable<Route.LineChart> {
                LineChartView(navigationActions)
            }
            composable<Route.BarChart> {
                BarChartView(navigationActions)
            }
            composable<Route.EcgChart> {
                EcgChartView(navigationActions)
            }
        }


    }

}






