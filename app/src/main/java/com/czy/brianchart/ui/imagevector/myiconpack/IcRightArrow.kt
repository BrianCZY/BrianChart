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

public val MyIconPack.IcRightArrow: ImageVector
    get() {
        if (_icRightArrow != null) {
            return _icRightArrow!!
        }
        _icRightArrow = Builder(name = "IcRightArrow", defaultWidth = 10.0.dp, defaultHeight =
                18.0.dp, viewportWidth = 10.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0xFFDADADA)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(1.0f, 17.75f)
                curveTo(1.199f, 17.751f, 1.39f, 17.672f, 1.53f, 17.53f)
                lineTo(9.53f, 9.53f)
                curveTo(9.822f, 9.237f, 9.822f, 8.763f, 9.53f, 8.47f)
                lineTo(1.53f, 0.47f)
                curveTo(1.235f, 0.195f, 0.774f, 0.203f, 0.488f, 0.488f)
                curveTo(0.203f, 0.774f, 0.195f, 1.234f, 0.47f, 1.53f)
                lineTo(7.94f, 9.0f)
                lineTo(0.47f, 16.47f)
                curveTo(0.178f, 16.763f, 0.178f, 17.237f, 0.47f, 17.53f)
                curveTo(0.61f, 17.672f, 0.801f, 17.751f, 1.0f, 17.75f)
                close()
            }
        }
        .build()
        return _icRightArrow!!
    }

private var _icRightArrow: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.IcRightArrow, contentDescription = "")
    }
}
