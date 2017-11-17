package com.merlin.core.secure;

/**
 * 16进制转换
 *
 * @author zal
 */
public class Hex {

    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 2进制转为16进制
     *
     * @param data
     * @return
     */
    public static char[] toHexChar(byte[] data) {
        int l = data.length;

        char[] out = new char[l << 1];

        int i = 0;
        for (int j = 0; i < l; ++i) {
            out[(j++)] = HEX[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = HEX[(0xF & data[i])];
        }

        return out;
    }

    /**
     * 2进制转为16进制
     *
     * @param data
     * @return
     */
    public static String toHex(byte[] data) {
//        return new String(toHexChar(data));
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            if (Integer.toHexString(0xFF & data[i]).length() == 1) {
                out.append("0").append(Integer.toHexString(0xFF & data[i]));
            } else {
                out.append(Integer.toHexString(0xFF & data[i]));
            }
        }
        return out.toString();
    }

    /**
     * 16进制转2进制
     *
     * @param hex
     * @return
     */
    public static byte[] toByte(String hex) {
        if (hex.length() < 1) {
            return null;
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length() / 2; i++) {
            bytes[i] = Integer.valueOf(hex.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return bytes;
    }

}
