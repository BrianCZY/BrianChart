package com.czy.brianchart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.czy.brianchart.ui.navigation.Route
import com.czy.brianchart.ui.theme.BrianChartTheme

@Composable
fun Home(navigationActions: ChartNavigationActions) {
    HomeView(
        modifier = Modifier.fillMaxSize(), lineChartClick = {
            navigationActions.navigateTo(Route.LineChart)
        },
        barChartClick = { navigationActions.navigateTo(Route.BarChart) },
        ecgChartClick = { navigationActions.navigateTo(Route.EcgChart) })
}

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    lineChartClick: (() -> Unit)? = null,
    barChartClick: (() -> Unit)? = null,
    ecgChartClick: (() -> Unit)? = null
) {
    Surface {
        Column(modifier = modifier) {
            Text(
                "BrianChart",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
                    .height(48.dp)
                    .wrapContentSize(Alignment.Center)
            )
            HorizontalDivider(thickness = 1.dp)
            Item(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(78.dp)
                    .clickable {
                        lineChartClick?.invoke()
                    }, "LineChart"
            )

            Item(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()

                    .height(78.dp)
                    .clickable {
                        barChartClick?.invoke()
                    }, "BarChart"
            )
            Item(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(78.dp)
                    .clickable {
                        ecgChartClick?.invoke()
                    }, "EcgChart"
            )

        }
    }

}

@Composable
fun Item(modifier: Modifier, text: String) {
    Box(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small
        )
    ) {
        Text(
            text,
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(Alignment.Center)
        )
    }
}


@Composable
@Preview(widthDp = 360, heightDp = 720)
fun HomeViewPrevie() {
    BrianChartTheme {
        HomeView()
    }

}




