package com.brian.screenmanager.ui.imagevector

import androidx.compose.ui.graphics.vector.ImageVector
import com.brian.screenmanager.ui.imagevector.myiconpack.IcAdd
import com.brian.screenmanager.ui.imagevector.myiconpack.IcChevronLeft
import com.brian.screenmanager.ui.imagevector.myiconpack.IcRightArrow

import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(IcAdd, IcChevronLeft, IcRightArrow)
    return __AllIcons!!
  }
