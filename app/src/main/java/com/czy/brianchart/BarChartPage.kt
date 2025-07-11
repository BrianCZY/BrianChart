package com.czy.brianchart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brian.screenmanager.ui.imagevector.MyIconPack
import com.brian.screenmanager.ui.imagevector.myiconpack.IcChevronLeft
import com.czy.brianchart.ui.navigation.ChartNavigationActions

@Composable
fun BarChartPage(navigationActions: ChartNavigationActions? = null) {
    BarChartView(modifier = Modifier.fillMaxSize(), backClick = {
        navigationActions?.navigateBack()
    })

}

@Composable
fun BarChartView(modifier: Modifier, backClick: () -> Unit?) {
    Surface(modifier = modifier) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Icon(
                    imageVector = MyIconPack.IcChevronLeft,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(28.dp)
                        .align(Alignment.CenterStart)
                        .clickable {
                            backClick()
                        }
                )
                Text(
                    "BarChart",
                    modifier = Modifier
                        .align(Alignment.Center)

                        .height(48.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
            HorizontalDivider(thickness = 1.dp)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            }
        }
    }
}