package la.shiro.nvramviewer.util;

import android.util.Log;

import com.android.internal.util.HexDump;

import java.util.ArrayList;

import vendor.mediatek.hardware.nvram.V1_0.INvram;

/**
 * author: Wang RuiLong
 * Date: 2024/02/22 20:50
 * Description : NvRam class
 */
public class NvRam {

    private static final String TAG = "Rin";
    private static final String PRODUCT_INFO_FILENAME = "/mnt/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";
    private static final int PRODUCT_INFO_SIZE = 1024;

    public static String getSn() {
        String sn = getAllFlagsFromNvRam();
        Log.d(TAG, "getSn --> sn: [" + sn + "]");
        // if sn start with 0x20 or 0x00, the sn is empty ,otherwise split with 0x20
        if (sn.startsWith(" ") || sn.startsWith("\0")) {
            sn = "N/A";
        } else {
            sn = sn.split(" ")[0];
        }
        return sn;
    }

    public static String getAllFlagsFromNvRam() {
        StringBuilder barcodeBuf = new StringBuilder();
        try {
            int nvRamBuffer;
            String buff;
            INvram agent = INvram.getService();
            if (agent == null) {
                Log.e(TAG, "getBarcodeFromNvRam --> Cannot get NvRam Agent");
                return "";
            }
            try {
                buff = agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "getBarcodeFromNvRam --> Read barcode from NvRam failed");
                return "";
            }
            Log.d(TAG, "getBarcodeFromNvRam --> Raw data:" + buff);
            if (buff.length() < 2 * (PRODUCT_INFO_SIZE)) {
                Log.e(TAG, "getBarcodeFromNvRam --> The format of NvRam is not correct");
                return "";
            }
            // Remove \0 in the end
            byte[] buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
            for (nvRamBuffer = 0; nvRamBuffer < 60; nvRamBuffer++) {
                barcodeBuf.append((char) buffArr[nvRamBuffer]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getBarcodeFromNvRam --> getBarcodeFromNvRam failed");
            return "";
        }
        return barcodeBuf.toString();
    }

    // WRITE IMEI 54
    // AGING 55
    // MMI 56
    // ADC 57
    // ATA 58
    // ANTENNA 59
    // CT 60 61
    // FT 62
    public static String getTestFlagsFromNvRam() {
        StringBuilder builder = new StringBuilder();
        try {
            int flagIndex;
            String nvRamBuffer;
            INvram agent = INvram.getService();
            if (agent == null) {
                Log.e(TAG, "getFlagsFromNvRam --> Cannot get NvRam Agent");
                return "";
            }
            try {
                nvRamBuffer = agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "getFlagsFromNvRam --> Read barcode from NvRam failed");
                return "";
            }
            Log.d(TAG, "getFlagsFromNvRam --> Raw data:" + nvRamBuffer);
            if (nvRamBuffer.length() < 2 * (PRODUCT_INFO_SIZE)) {
                Log.e(TAG, "getFlagsFromNvRam --> The format of NvRam is not correct");
                return "";
            }
            // Remove \0 in the end
            byte[] bytes = HexDump.hexStringToByteArray(nvRamBuffer.substring(0, nvRamBuffer.length() - 1));
            for (flagIndex = 54; flagIndex < 63; flagIndex++) {
                builder.append((char) bytes[flagIndex]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getFlagsFromNvRam --> getTestFlagsFromNvRam: " + e.getMessage());
            return "";
        }
        String flagsResult = builder.toString().replace(" ", "N");
        Log.d(TAG, "getFlagsFromNvRam --> flagsResult: " + flagsResult);

        return "54 WRITE IMEI: " + flagsResult.charAt(0) + "\n" + "55 AGING: " + flagsResult.charAt(1) + "\n" + "56 MMI: " + flagsResult.charAt(2) + "\n" + "57 ADC: " + flagsResult.charAt(3) + "\n" + "58 ATA: " + flagsResult.charAt(4) + "\n" + "59 ANTENNA: " + flagsResult.charAt(5) + "\n" + "60 CT: " + flagsResult.charAt(6) + "\n" + "61 CT: " + flagsResult.charAt(7) + "\n" + "62 FT: " + flagsResult.charAt(8) + "\n";
    }

    public static boolean setFlagToNvRam(int flagIndex, boolean isPass) {
        try {
            INvram agent = INvram.getService();
            String nvRamBuffer;
            if (agent == null) {
                Log.e(TAG, "writeFlagsToNvRam --> Cannot get NvRam Agent");
                return false;
            }
            try {
                nvRamBuffer = agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "writeFlagsToNvRam --> Read barcode from NvRam failed");
                return false;
            }
            byte[] buff = new byte[0];
            if (nvRamBuffer != null) {
                buff = HexDump.hexStringToByteArray(nvRamBuffer.substring(0, nvRamBuffer.length() - 1));
            }
            ArrayList<Byte> dataArray = new ArrayList<>(PRODUCT_INFO_SIZE);
            if (isPass) {
                buff[flagIndex] = 'P';
            } else {
                buff[flagIndex] = 'F';
            }
            for (int index = 0; index < PRODUCT_INFO_SIZE; index++) {
                dataArray.add(index, buff[index]);
            }
            try {
                agent.writeFileByNamevec(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE, dataArray);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "writeFlagsToNvRam --> writeFlagsToNvRam failed, flagIndex: " + flagIndex + " isPass: " + isPass);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "writeFlagsToNvRam --> writeFlagsToNvRam failed, flagIndex: " + flagIndex + " isPass: " + isPass);
            return false;
        }
        return true;
    }

    public static boolean unsetFlagToNvRam(int flagIndex) {
        try {
            INvram agent = INvram.getService();
            String nvRamBuffer;
            if (agent == null) {
                Log.e(TAG, "unsetFlagToNvRam --> Cannot get NvRam Agent");
                return false;
            }
            try {
                nvRamBuffer = agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "unsetFlagToNvRam --> Read barcode from NvRam failed");
                return false;
            }
            byte[] buff = new byte[0];
            if (nvRamBuffer != null) {
                buff = HexDump.hexStringToByteArray(nvRamBuffer.substring(0, nvRamBuffer.length() - 1));
            }
            ArrayList<Byte> dataArray = new ArrayList<>(PRODUCT_INFO_SIZE);
            buff[flagIndex] = 0x20;
            for (int index = 0; index < PRODUCT_INFO_SIZE; index++) {
                dataArray.add(index, buff[index]);
            }
            try {
                agent.writeFileByNamevec(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE, dataArray);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "unsetFlagToNvRam --> unsetFlagToNvRam failed, flagIndex: " + flagIndex);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "unsetFlagToNvRam --> unsetFlagToNvRam failed, flagIndex: " + flagIndex);
            return false;
        }
        return true;
    }

    public static String getFlagFromNvRam(int flagIndex) {
        try {
            String nvRamBuffer;
            INvram agent = INvram.getService();
            if (agent == null) {
                Log.e(TAG, "getFlagFromNvRam --> Cannot get NvRam Agent");
                return "";
            }
            try {
                nvRamBuffer = agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "getFlagFromNvRam --> Read barcode from NvRam failed");
                return "";
            }
            Log.d(TAG, "getFlagFromNvRam --> Raw data:" + nvRamBuffer);
            if (nvRamBuffer.length() < 2 * (PRODUCT_INFO_SIZE)) {
                Log.e(TAG, "getFlagFromNvRam --> The format of NvRam is not correct");
                return "";
            }
            // Remove \0 in the end
            byte[] bytes = HexDump.hexStringToByteArray(nvRamBuffer.substring(0, nvRamBuffer.length() - 1));
            // print flag hex
            Log.d(TAG, "getFlagFromNvRam --> flag hex: " + Integer.toHexString(bytes[flagIndex]));
            String flag = String.valueOf((char) bytes[flagIndex]);
            Log.d(TAG, "getFlagFromNvRam --> flag index: " + flagIndex + " is " + flag);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFlagFromNvRam --> getFlagFromNvRam failed");
            return "";
        }
    }

    public static boolean initNvRam() {
        try {
            INvram agent = INvram.getService();
            String nvRamBuffer;
            if (agent == null) {
                Log.e(TAG, "initNvRam --> Cannot get NvRam Agent");
                return false;
            }
            try {
                nvRamBuffer = agent.readFileByName(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "initNvRam --> Read barcode from NvRam failed");
                return false;
            }
            byte[] buff = new byte[0];
            if (nvRamBuffer != null) {
                buff = HexDump.hexStringToByteArray(nvRamBuffer.substring(0, nvRamBuffer.length() - 1));
            }
            ArrayList<Byte> dataArray = new ArrayList<>(PRODUCT_INFO_SIZE);
            for (int index = 0; index < PRODUCT_INFO_SIZE; index++) {
                if (index < 64) {
                    if (buff[index] == 0) {
                        buff[index] = 0x20;
                        Log.d(TAG, "initNvRam --> flag index: " + index + " is empty, fill with space");
                    }
                }
                dataArray.add(index, buff[index]);
            }
            try {
                agent.writeFileByNamevec(PRODUCT_INFO_FILENAME, PRODUCT_INFO_SIZE, dataArray);
            } catch (Exception e) {
                Log.e(TAG, "initNvRam --> WriteNvRam failed");
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "initNvRam --> WriteNvRam failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
