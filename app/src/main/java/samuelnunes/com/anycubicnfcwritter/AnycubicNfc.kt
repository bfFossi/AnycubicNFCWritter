package samuelnunes.com.anycubicnfcwritter

import android.util.Log



const val FILAMENT_DIAM = 1.75
const val HOTEND_TEMP_MIN = 240
const val HOTEND_TEMP_MAX = 260
const val BED_TEMP_MIN = 90
const val BED_TEMP_MAX = 115

data class ARGB(val a: Int, val r: Int, val g: Int, val b: Int) {
    constructor(array: IntArray) : this(array[0],array[1],array[2],array[3])
}

data class Spool(
    val hexColor: ARGB,
    val material: String,
    val filamentDiam: Double = FILAMENT_DIAM,
    val hotendTempMin: Int = HOTEND_TEMP_MIN,
    val hotendTempMax: Int = HOTEND_TEMP_MAX,
    val bedTempMin: Int = BED_TEMP_MIN,
    val bedTempMax: Int = BED_TEMP_MAX
)


fun nfcMapper(spool: Spool): String {
    Log.i("NFC Data", "Spool: $spool")
    val (a, r, g, b) = spool.hexColor

    val materialName = spool. material.map { it.code.toHex() }.toMutableList()
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
        A2:14:${a.toHex()}:${b.toHex()}:${g.toHex()}:${r.toHex()},
        A2:15:00:00:00:00,
        A2:16:00:00:00:00,
        A2:17:32:00:64:00,
        A2:18:${HOTEND_TEMP_MIN.toHex()}:00:${HOTEND_TEMP_MAX.toHex()}:00,
        A2:19:00:00:00:00,
        A2:1A:00:00:00:00,
        A2:1B:00:00:00:00,
        A2:1C:00:00:00:00,
        A2:1D:${BED_TEMP_MIN.toHex()}:00:${BED_TEMP_MAX.toHex()}:00,
        A2:1E:${(FILAMENT_DIAM * 100).toInt().toHex()}:00:4A:01,
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

    Log.i("NFC Data", hexStuff)
    return hexStuff
}

fun Int.toHex(): String = this.toString(16).uppercase()
