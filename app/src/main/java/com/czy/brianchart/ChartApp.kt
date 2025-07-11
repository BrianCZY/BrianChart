package com.czy.brianchart

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                LineChartPage(navigationActions)
            }
            composable<Route.BarChart> {
                BarChartPage(navigationActions)
            }
            composable<Route.EcgChart> {
                EcgChartPage(navigationActions)
            }
        }


    }

}






