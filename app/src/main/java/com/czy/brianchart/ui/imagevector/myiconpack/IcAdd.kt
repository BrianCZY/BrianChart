package com.brian.screenmanager.ui.imagevector.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brian.screenmanager.ui.imagevector.MyIconPack
import kotlin.Unit

public val MyIconPack.IcAdd: ImageVector
    get() {
        if (_icAdd != null) {
            return _icAdd!!
        }
        _icAdd = Builder(name = "IcAdd", defaultWidth = 82.0.dp, defaultHeight = 82.0.dp,
                viewportWidth = 82.0f, viewportHeight = 82.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFDEE3E6)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(41.0f, 28.0f)
                verticalLineTo(54.0f)
                moveTo(28.0f, 41.0f)
                horizontalLineTo(54.0f)
                moveTo(81.0f, 41.0f)
                curveTo(81.0f, 63.091f, 63.091f, 81.0f, 41.0f, 81.0f)
                curveTo(18.909f, 81.0f, 1.0f, 63.091f, 1.0f, 41.0f)
                curveTo(1.0f, 18.909f, 18.909f, 1.0f, 41.0f, 1.0f)
                curveTo(63.091f, 1.0f, 81.0f, 18.909f, 81.0f, 41.0f)
                close()
            }
        }
        .build()
        return _icAdd!!
    }

private var _icAdd: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.IcAdd, contentDescription = "")
    }
}
