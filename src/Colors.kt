import androidx.compose.ui.graphics.Color

// Colors cribbed from https://tailwindcss.com/docs/customizing-colors

object Colors {
    val Red = Color(red = 0xdc, green = 0x26, blue=0x26)
    val Green = Color(red = 0x05, green = 0x96, blue=0x69)
    val Blue = Color(red = 0x25, green = 0x63, blue = 0xEB)
    val DarkBlue = Color(red = 0x1E, green = 0x3A, blue = 0x8A)
    val Yellow = Color(red = 0xFC, green = 0xD3, blue = 0x4D)
    val DarkGray = Color(red = 0x1F, green = 0x29, blue = 0x37)
    val None = Color(red = 0x00, green = 0x00, blue = 0x00, alpha = 0)

    val Yellows = listOf(
        Color(red=0x71, green=0x3f, blue=0x12),
        Color(red=0x85, green=0x4d, blue=0x0e),
        Color(red=0xa1, green=0x62, blue=0x07),
        Color(red=0xca, green=0x8a, blue=0x04),
        Color(red=0xea, green=0xb3, blue=0x08),
        Color(red=0xfa, green=0xcc, blue=0x15),
        Color(red=0xfd, green=0xe0, blue=0x47),
        Color(red=0xfe, green=0xf0, blue=0x8a),
        Color(red=0xfe, green=0xf9, blue=0xc3),
        Color(red=0xfe, green=0xfc, blue=0xe8),
    )
}
