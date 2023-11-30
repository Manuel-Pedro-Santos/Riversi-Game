package pt.isel.tds.view

import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import java.awt.image.BufferedImage

fun PixelMap.subimage(offset: IntOffset, size: IntSize): ImageBitmap {
    val buffered = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB)
    for (y in 0 until size.height) {
        for (x in 0 until size.width) {
            val rgb = this[x+offset.x, y+offset.y].run { Color(red, green, blue, alpha).toArgb() }
            buffered.setRGB(x, y, rgb)
        }
    }
    return buffered.toComposeImageBitmap()
}

class Sprites(resourceName: String, private val defaultSize: IntSize = IntSize(64,64)) {

    private val pixels = useResource(resourceName){ loadImageBitmap(it) }.toPixelMap()

    private val cache = mutableMapOf< Pair<IntOffset,IntSize>, ImageBitmap>()


    operator fun get(offset: IntOffset, size: IntSize = defaultSize): ImageBitmap =
        cache.getOrPut(offset to size) { pixels.subimage(offset, size) }
    operator fun get(row: Int, col: Int): ImageBitmap =
        get(IntOffset(col * defaultSize.width, row * defaultSize.height))
}

