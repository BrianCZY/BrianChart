package com.brian.screenmanager.ui.imagevector.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brian.screenmanager.ui.imagevector.MyIconPack
import kotlin.Unit

public val MyIconPack.IcChevronLeft: ImageVector
    get() {
        if (_icChevronLeft != null) {
            return _icChevronLeft!!
        }
        _icChevronLeft = Builder(name = "IcChevronLeft", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFFDEE3E6)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(16.0f, 20.75f)
                curveTo(15.801f, 20.751f, 15.61f, 20.672f, 15.47f, 20.53f)
                lineTo(7.47f, 12.53f)
                curveTo(7.178f, 12.237f, 7.178f, 11.763f, 7.47f, 11.47f)
                lineTo(15.47f, 3.47f)
                curveTo(15.766f, 3.195f, 16.226f, 3.203f, 16.512f, 3.488f)
                curveTo(16.797f, 3.774f, 16.805f, 4.234f, 16.53f, 4.53f)
                lineTo(9.06f, 12.0f)
                lineTo(16.53f, 19.47f)
                curveTo(16.823f, 19.763f, 16.823f, 20.237f, 16.53f, 20.53f)
                curveTo(16.39f, 20.672f, 16.199f, 20.751f, 16.0f, 20.75f)
                close()
            }
        }
        .build()
        return _icChevronLeft!!
    }

private var _icChevronLeft: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.IcChevronLeft, contentDescription = "")
    }
}
