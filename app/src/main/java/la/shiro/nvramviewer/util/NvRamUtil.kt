package la.shiro.nvramviewer.util

import android.util.Log
import com.android.internal.util.HexDump
import la.shiro.nvramviewer.config.PRODUCT_INFO_FILENAME
import la.shiro.nvramviewer.config.PRODUCT_INFO_FILE_SIZE
import la.shiro.nvramviewer.config.TAG
import la.shiro.nvramviewer.config.TRACKED_STATE
import la.shiro.nvramviewer.config.UNKNOWN_STATE
import la.shiro.nvramviewer.config.UNTRACKED_STATE
import la.shiro.nvramviewer.config.WIFI_NV_FILENAME
import la.shiro.nvramviewer.config.WIFI_NV_FILE_SIZE
import vendor.mediatek.hardware.nvram.V1_0.INvram


object NvRamUtil {

    fun dumpProductInfoNvRam(): String {
        try {
            val agent = INvram.getService()
            if (agent == null) {
                Log.e(TAG, "readNvRamState --> NvRamAgent is null")
                return "NvRamAgent is null"
            }
            val nvRamStringBuffer =
                agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_FILE_SIZE)
            Log.d(TAG, "dumpNvRam --> PRODUCT_INFO")
            val result = formatHexArrayLog(nvRamStringBuffer)
            Log.d(TAG, "dumpNvRam --> PRODUCT_INFO End")
            return result
        } catch (e: Exception) {
            Log.e(TAG, "dumpNvRam --> Exception: $e")
            return "NvRamAgent is null"
        }
    }

    fun dumpWifiNvRam(): String {
        try {
            val agent = INvram.getService()
            if (agent == null) {
                Log.e(TAG, "readNvRamState --> NvRamAgent is null")
                return "NvRamAgent is null"
            }
            val nvRamStringBuffer = agent.readFileByName(WIFI_NV_FILENAME, WIFI_NV_FILE_SIZE)
            Log.d(TAG, "dumpWifiNvRam --> WIFI_NV Start")
            val result = formatHexArrayLog(nvRamStringBuffer)
            Log.d(TAG, "dumpWifiNvRam --> WIFI_NV End")
            return result
        } catch (e: Exception) {
            Log.e(TAG, "dumpWifiNvRam --> Exception: $e")
            return "NvRamAgent is null"
        }
    }

    fun readNvRamState(): Int {
        val nvRamByteArray: ByteArray
        try {
            val agent = INvram.getService()
            if (agent == null) {
                Log.e(TAG, "readNvRamState --> NvRamAgent is null")
                return UNKNOWN_STATE
            }
            val nvRamStringBuffer =
                agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_FILE_SIZE)
            nvRamByteArray = HexDump.hexStringToByteArray(
                nvRamStringBuffer.dropLast(1)
            )
            return byteToInt(
                byteArrayOf(
                    nvRamByteArray[0], nvRamByteArray[1], nvRamByteArray[2], nvRamByteArray[3]
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "readNvRamState --> Exception : $e")
        }
        return UNKNOWN_STATE
    }

    fun writeNvRamState(state: Boolean) {
        val nvRamByteArray: ByteArray
        try {
            val agent = INvram.getService()
            if (agent == null) {
                Log.e(TAG, "writeNvRamState --> NvRamAgent is null")
                return
            }
            val nvRamStringBuffer =
                agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_FILE_SIZE)
            nvRamByteArray = HexDump.hexStringToByteArray(
                nvRamStringBuffer.dropLast(1)
            )
            val tempNvRamBuffer: ByteArray = getBytes(if (state) TRACKED_STATE else UNTRACKED_STATE)
            nvRamByteArray[0] = tempNvRamBuffer[0]
            nvRamByteArray[1] = tempNvRamBuffer[1]
            nvRamByteArray[2] = tempNvRamBuffer[2]
            nvRamByteArray[3] = tempNvRamBuffer[3]
            val dataArray = ArrayList<Byte>(4)
            for (i in 0 until PRODUCT_INFO_FILE_SIZE) {
                dataArray.add(i, nvRamByteArray[i])
            }
            val stateFlag = agent.writeFileByNamevec(
                PRODUCT_INFO_FILENAME, PRODUCT_INFO_FILE_SIZE, dataArray
            ).toInt()
            Log.d(TAG, "writeNvRamState --> newState = $state")
            if (stateFlag >= 0) {
                Log.d(TAG, "writeNvRamState --> Success to write to NvRam State")
            } else {
                Log.d(TAG, "writeNvRamState --> Fail to write to NvRam State")
            }
        } catch (e: Exception) {
            Log.e(TAG, "writeNvRamState --> Exception : $e")
        }
    }

    private fun byteToInt(res: ByteArray): Int {
        return res[0].toInt() and 0xff or (res[1].toInt() shl 8 and 0xff00) or (res[2].toInt() shl 24 ushr 8) or (res[3].toInt() shl 24)
    }

    private fun getBytes(data: Int): ByteArray {
        val bytes = ByteArray(4)
        bytes[0] = (data and 0xff).toByte()
        bytes[1] = (data and 0xff00 shr 8).toByte()
        bytes[2] = (data and 0xff0000 shr 16).toByte()
        bytes[3] = (data and -0x1000000 shr 24).toByte()
        return bytes
    }

    private fun formatHexArrayLog(text: String): String {
        val stringBuilder = StringBuilder()
        val resultString = StringBuilder()
        var lineNumber = 0
        var formatLineNumber: String
        for (i in text.indices) {
            stringBuilder.append(text[i])
            if (i % 2 != 0 && i < text.length - 1) {
                stringBuilder.append(" ")
            }
            if ((i + 1) % 32 == 0 && i < text.length - 1) {
                lineNumber++
                formatLineNumber = lineNumber.toString().padStart(4, '0')
                Log.d(TAG, "Line $formatLineNumber > $stringBuilder")
                resultString.append("$formatLineNumber > $stringBuilder\n")
                stringBuilder.clear()
            }
        }
        return resultString.toString()
    }
}