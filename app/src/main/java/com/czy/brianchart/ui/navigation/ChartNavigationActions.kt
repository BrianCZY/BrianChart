/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.czy.brianchart.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data object LineChart : Route
    @Serializable data object BarChart : Route
    @Serializable data object EcgChart : Route
}


class ChartNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: Route) {
        navController.navigate(destination) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    // 返回上一页面
    fun navigateBack() {
        navController.popBackStack()
    }
}
