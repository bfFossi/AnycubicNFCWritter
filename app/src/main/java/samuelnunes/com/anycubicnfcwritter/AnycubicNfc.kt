package samuelnunes.com.anycubicnfcwritter


fun hexToRgb(rgbOriginal: String): Triple<String, String, String> {
    val r = rgbOriginal.substring(0, 2).uppercase()
    val g = rgbOriginal.substring(2, 4).uppercase()
    val b = rgbOriginal.substring(4, 6).uppercase()
    return Triple(r, g, b)
}

fun nfcMapper(hexColor: String, material: String= "PLA+"): String {
    val rgbOriginal: String = hexColor.removePrefix("#")
    val (r, g, b) = hexToRgb(rgbOriginal)

    val materialName = material.map { it.code.toString(16).uppercase() }.toMutableList()
    while (materialName.size < 20) {
        materialName.add("00")
    }

    val hexStuff = """
        A2:04:7B:00:65:00,
        A2:05:41:48:50:4C,
        A2:06:50:42:57:2D,
        A2:07:31:30:32:00,
        A2:08:00:00:00:00,
        A2:09:00:00:00:00,
        A2:0A:41:43:00:00,
        A2:0B:00:00:00:00,
        A2:0C:00:00:00:00,
        A2:0D:00:00:00:00,
        A2:0E:00:00:00:00,
        A2:0F:${materialName[0]}:${materialName[1]}:${materialName[2]}:${materialName[3]},
        A2:10:${materialName[4]}:${materialName[5]}:${materialName[6]}:${materialName[7]},
        A2:11:${materialName[8]}:${materialName[9]}:${materialName[10]}:${materialName[11]},
        A2:12:${materialName[12]}:${materialName[13]}:${materialName[14]}:${materialName[15]},
        A2:13:${materialName[16]}:${materialName[17]}:${materialName[18]}:${materialName[19]},
        A2:14:FF:$b:$g:$r,
        A2:15:00:00:00:00,
        A2:16:00:00:00:00,
        A2:17:32:00:64:00,
        A2:18:CD:00:D7:00,
        A2:19:00:00:00:00,
        A2:1A:00:00:00:00,
        A2:1B:00:00:00:00,
        A2:1C:00:00:00:00,
        A2:1D:32:00:3C:00,
        A2:1E:AF:00:4A:01,
        A2:1F:E8:03:00:00,
        A2:20:00:00:00:00,
        A2:21:00:00:00:00,
        A2:22:00:00:00:00,
        A2:23:00:00:00:00,
        A2:24:00:00:00:00,
        A2:25:00:00:00:00,
        A2:26:00:00:00:00,
        A2:27:00:00:00:00,
        A2:28:00:00:00:BD,
        A2:29:04:00:00:04,
        A2:2A:47:00:00:00,
        A2:2B:00:00:00:00,
        A2:2C:00:00:00:00
    """.trimIndent()

    return hexStuff
}
