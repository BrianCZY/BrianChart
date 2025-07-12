package com.czy.brianchart.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brian.screenmanager.ui.imagevector.MyIconPack
import com.brian.screenmanager.ui.imagevector.myiconpack.IcChevronLeft

@Composable
fun TopBar(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
    title: String,
    backClick: () -> Unit?
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    backClick()
                }) {
            Icon(
                imageVector = MyIconPack.IcChevronLeft,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .size(28.dp)
                    .align(Alignment.CenterStart)

            )
        }

        Text(
            title,
            modifier = Modifier
                .align(Alignment.Center)

                .height(48.dp)
                .wrapContentSize(Alignment.Center)
        )
    }
}