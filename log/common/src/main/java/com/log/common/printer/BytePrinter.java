package com.log.common.printer;

public class BytePrinter {

    /**
     * Print a byte into binary.
     *
     * @param b byte
     * @return binary string
     */
    public static String toString(byte b) {
        StringBuilder stringBuilder = new StringBuilder(8);
        for (int i = 7; i >= 0; i--) {
            stringBuilder.append(b >> i & 1);
        }
        return stringBuilder.toString();
    }

    /**
     * Print byte array into binary, split by comma
     *
     * @param bs byte array
     * @return binary string
     */
    public static String toString(byte[] bs) {
        String[] a = new String[bs.length];
        for (int i = 0; i < bs.length; i++) {
            a[i] = toString(bs[i]);
        }
        return String.join(",", a);
    }

    /**
     * Print byte array into hex, split by divider
     *
     * @param bs        byte array
     * @param delimiter delimiter
     * @return hex string
     */
    public static String toString0(byte[] bs, String delimiter) {
        String[] a = new String[bs.length];
        for (int i = 0; i < bs.length; i++) {
            int b = bs[i] & 0xff;
            final String hs = Integer.toHexString(b);
            a[i] = hs.length() == 1 ? 0 + hs : hs;
        }
        return String.join(delimiter, a);
    }

    /**
     * Print byte array into hex, split by comma
     *
     * @param bs byte array
     * @return hex string
     */
    public static String toString1(byte[] bs) {
        return toString0(bs, ",");
    }
}
