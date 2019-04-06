package com.log.common.conversion;


/**
 * Byte utils.
 */
public class ByteConversion {

    /**
     * <p>
     * Converts binary (represented as boolean array) into a byte using the default (big
     * endian, Msb0) byte and bit ordering.
     * </p>
     *
     * @param src     the binary to convert
     * @param srcPos  the position in {@code src}, in boolean unit, from where to start the
     *                conversion
     * @param dstInit initial value of the destination byte
     * @param nBools  the number of booleans to convert
     * @return a byte containing the selected bits
     * @throws NullPointerException           if {@code src} is {@code null}
     * @throws IllegalArgumentException       if {@code nBools > 8}
     * @throws ArrayIndexOutOfBoundsException if {@code srcPos + nBools > src.length}
     */
    public static byte binaryToByte(final boolean[] src, final int srcPos, final byte dstInit, final int nBools) {
        if (src.length == 0 && srcPos == 0 || 0 == nBools) {
            return dstInit;
        }
        if (nBools > 8) {
            throw new IllegalArgumentException("nBools is greater than 8");
        }
        byte out = dstInit;
        for (int i = 0; i < nBools; i++) {
            final int shift = nBools - i - 1;
            final int bits = (src[i + srcPos] ? 1 : 0) << shift;
            final int mask = 0x1 << shift;
            out = (byte) ((out & ~mask) | bits);
        }
        return out;
    }

    /**
     * <p>
     * Converts binary (represented as boolean array) into a short using the default (big
     * endian, Msb0) byte and bit ordering.
     * </p>
     *
     * @param src     the binary to convert
     * @param srcPos  the position in {@code src}, in boolean unit, from where to start the
     *                conversion
     * @param dstInit initial value of the destination short
     * @param nBools  the number of booleans to convert
     * @return a short containing the selected bits
     * @throws NullPointerException           if {@code src} is {@code null}
     * @throws IllegalArgumentException       if {@code nBools > 16}
     * @throws ArrayIndexOutOfBoundsException if {@code srcPos + nBools > src.length}
     */
    public static short binaryToShort(final boolean[] src, final int srcPos, final short dstInit, final int nBools) {
        if (src.length == 0 && srcPos == 0 || 0 == nBools) {
            return dstInit;
        }
        if (nBools > 16) {
            throw new IllegalArgumentException("nBools is greater than 16");
        }
        short out = dstInit;
        for (int i = 0; i < nBools; i++) {
            final int shift = nBools - i - 1;
            final int bits = (src[i + srcPos] ? 1 : 0) << shift;
            final int mask = 0x1 << shift;
            out = (short) ((out & ~mask) | bits);
        }
        return out;
    }

    /**
     * <p>
     * Converts an int into an array of byte using the default (big endian, Msb0) byte and bit
     * ordering.
     * </p>
     *
     * @param src    the int to convert
     * @param srcPos the position in {@code src}, in bits, from where to start the conversion
     * @param dst    the destination array
     * @param dstPos the position in {@code dst} where to copy the result
     * @param nBytes the number of bytes to copy to {@code dst}, must be smaller or equal to the
     *               width of the input (from srcPos to lsb)
     * @return {@code dst}
     * @throws NullPointerException           if {@code dst} is {@code null}
     * @throws IllegalArgumentException       if {@code (nBytes-1)*8+srcPos >= 32}
     * @throws ArrayIndexOutOfBoundsException if {@code dstPos + nBytes > dst.length}
     */
    public static byte[] intToByteArray(final int src, final int srcPos, final byte[] dst, final int dstPos,
                                        final int nBytes) {
        if (0 == nBytes) {
            return dst;
        }
        if ((nBytes - 1) * 8 + srcPos >= 32) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 32");
        }
        for (int i = 0; i < nBytes; i++) {
            final int shift = (3 - i) * 8 - srcPos;
            dst[dstPos + i] = (byte) (0xff & (src >> shift));
        }
        return dst;
    }

    /**
     * <p>
     * Converts an int into an array of boolean using the default (big endian, Msb0) byte and
     * bit ordering.
     * </p>
     *
     * @param src    the int to convert
     * @param srcPos the position in {@code src}, in bits, from where to start the conversion
     * @param dst    the destination array
     * @param dstPos the position in {@code dst} where to copy the result
     * @param nBools the number of booleans to copy to {@code dst}, must be smaller or equal to
     *               the width of the input (from srcPos to lsb)
     * @return {@code dst}
     * @throws NullPointerException           if {@code dst} is {@code null}
     * @throws IllegalArgumentException       if {@code nBools-1+srcPos >= 32}
     * @throws ArrayIndexOutOfBoundsException if {@code dstPos + nBools > dst.length}
     */
    public static boolean[] intToBinary(final int src, final int srcPos, final boolean[] dst, final int dstPos,
                                        final int nBools) {
        if (0 == nBools) {
            return dst;
        }
        if (nBools - 1 + srcPos >= 32) {
            throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 32");
        }
        for (int i = 0; i < nBools; i++) {
            final int shift = 31 - i - srcPos;
            dst[dstPos + i] = (0x1 & (src >> shift)) != 0;
        }
        return dst;
    }

    /**
     * <p>
     * Converts a byte into an array of boolean using the default (big endian, Msb0) byte and
     * bit ordering.
     * </p>
     *
     * @param src    the byte to convert
     * @param srcPos the position in {@code src}, in bits, from where to start the conversion
     * @param dst    the destination array
     * @param dstPos the position in {@code dst} where to copy the result
     * @param nBools the number of booleans to copy to {@code dst}, must be smaller or equal to
     *               the width of the input (from srcPos to lsb)
     * @return {@code dst}
     * @throws NullPointerException           if {@code dst} is {@code null}
     * @throws IllegalArgumentException       if {@code nBools-1+srcPos >= 8}
     * @throws ArrayIndexOutOfBoundsException if {@code dstPos + nBools > dst.length}
     */
    public static boolean[] byteToBinary(final byte src, final int srcPos, final boolean[] dst, final int dstPos,
                                         final int nBools) {
        if (0 == nBools) {
            return dst;
        }
        if (nBools - 1 + srcPos >= 8) {
            throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 8");
        }
        for (int i = 0; i < nBools; i++) {
            final int shift = 7 - srcPos - i;
            dst[dstPos + i] = (0x1 & (src >> shift)) != 0;
        }
        return dst;
    }

    /**
     * <p>
     * Converts a short into an array of byte using the default (big endian, Msb0) byte and
     * bit ordering.
     * </p>
     *
     * @param src    the short to convert
     * @param srcPos the position in {@code src}, in bits, from where to start the conversion
     * @param dst    the destination array
     * @param dstPos the position in {@code dst} where to copy the result
     * @param nBytes the number of bytes to copy to {@code dst}, must be smaller or equal to the
     *               width of the input (from srcPos to lsb)
     * @return {@code dst}
     * @throws NullPointerException           if {@code dst} is {@code null}
     * @throws IllegalArgumentException       if {@code (nBytes-1)*8+srcPos >= 16}
     * @throws ArrayIndexOutOfBoundsException if {@code dstPos + nBytes > dst.length}
     */
    public static byte[] shortToByteArray(final short src, final int srcPos, final byte[] dst, final int dstPos, final int nBytes) {
        if (0 == nBytes) {
            return dst;
        }
        if ((nBytes - 1) * 8 + srcPos >= 16) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 16");
        }
        for (int i = 0; i < nBytes; i++) {
            final int shift = (1 - i) * 8 - srcPos;
            dst[dstPos + i] = (byte) (0xff & (src >> shift));
        }
        return dst;
    }

    /**
     * <p>
     * Converts a short into an array of boolean using the default (big endian, Msb0) byte
     * and bit ordering.
     * </p>
     *
     * @param src    the short to convert
     * @param srcPos the position in {@code src}, in bits, from where to start the conversion
     * @param dst    the destination array
     * @param dstPos the position in {@code dst} where to copy the result
     * @param nBools the number of booleans to copy to {@code dst}, must be smaller or equal to
     *               the width of the input (from srcPos to lsb)
     * @return {@code dst}
     * @throws NullPointerException           if {@code dst} is {@code null}
     * @throws IllegalArgumentException       if {@code nBools-1+srcPos >= 16}
     * @throws ArrayIndexOutOfBoundsException if {@code dstPos + nBools > dst.length}
     */
    public static boolean[] shortToBinary(final short src, final int srcPos, final boolean[] dst, final int dstPos,
                                          final int nBools) {
        if (0 == nBools) {
            return dst;
        }
        if (nBools - 1 + srcPos >= 16) {
            throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 16");
        }
        assert (nBools - 1) < 16 - srcPos;
        for (int i = 0; i < nBools; i++) {
            final int shift = 15 - i - srcPos;
            dst[dstPos + i] = (0x1 & (src >> shift)) != 0;
        }
        return dst;
    }

    /**
     * <p>
     * Converts an array of byte into a short using the default (big endian, Msb0) byte and
     * bit ordering.
     * </p>
     *
     * @param src     the byte array to convert
     * @param srcPos  the position in {@code src}, in byte unit, from where to start the
     *                conversion
     * @param dstInit initial value of the destination short
     * @param nBytes  the number of bytes to convert
     * @return a short containing the selected bits
     * @throws NullPointerException           if {@code src} is {@code null}
     * @throws IllegalArgumentException       if {@code nBytes*8 > 16}
     * @throws ArrayIndexOutOfBoundsException if {@code srcPos + nBytes > src.length}
     */
    public static short byteArrayToShort(final byte[] src, final int srcPos, final short dstInit, final int nBytes) {
        if (src.length == 0 && srcPos == 0 || 0 == nBytes) {
            return dstInit;
        }
        if (nBytes * 8 > 16) {
            throw new IllegalArgumentException("nBytes*8  is greater than 16");
        }
        short out = dstInit;
        for (int i = 0; i < nBytes; i++) {
            final int shift = (nBytes - i - 1) * 8;
            final int bits = (0xff & src[i + srcPos]) << shift;
            final int mask = 0xff << shift;
            out = (short) ((out & ~mask) | bits);
        }
        return out;
    }

    /**
     * <p>
     * Converts binary (represented as boolean array) into an int using the default (big
     * endian, Msb0) byte and bit ordering.
     * </p>
     *
     * @param src     the binary to convert
     * @param srcPos  the position in {@code src}, in boolean unit, from where to start the
     *                conversion
     * @param dstInit initial value of the destination int
     * @param nBools  the number of booleans to convert
     * @return an int containing the selected bits
     * @throws NullPointerException           if {@code src} is {@code null}
     * @throws IllegalArgumentException       if {@code nBools > 32}
     * @throws ArrayIndexOutOfBoundsException if {@code srcPos + nBools > src.length}
     */
    public static int binaryToInt(final boolean[] src, final int srcPos, final int dstInit, final int nBools) {
        if (src.length == 0 && srcPos == 0 || 0 == nBools) {
            return dstInit;
        }
        if (nBools > 32) {
            throw new IllegalArgumentException("nBools is greater than 32");
        }
        int out = dstInit;
        for (int i = 0; i < nBools; i++) {
            final int shift = nBools - i - 1;
            final int bits = (src[i + srcPos] ? 1 : 0) << shift;
            final int mask = 0x1 << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }

    /**
     * <p>
     * Converts an array of byte into an int using the default (big endian, Msb0) byte and bit
     * ordering.
     * </p>
     *
     * @param src     the byte array to convert
     * @param srcPos  the position in {@code src}, in byte unit, from where to start the
     *                conversion
     * @param dstInit initial value of the destination int
     * @param nBytes  the number of bytes to convert
     * @return an int containing the selected bits
     * @throws NullPointerException           if {@code src} is {@code null}
     * @throws IllegalArgumentException       if {@code nBytes * 8 > 32}
     * @throws ArrayIndexOutOfBoundsException if {@code srcPos + nBytes > src.length}
     */
    public static int byteArrayToInt(final byte[] src, final int srcPos, final int dstInit, final int nBytes) {
        if (src.length == 0 && srcPos == 0 || 0 == nBytes) {
            return dstInit;
        }
        if (nBytes * 8 > 32) {
            throw new IllegalArgumentException("nBytes * 8 > 32 is greater than 32");
        }
        int out = dstInit;
        for (int i = 0; i < nBytes; i++) {
            final int shift = (nBytes - i - 1) * 8;
            final int bits = (0xff & src[i + srcPos]) << shift;
            final int mask = 0xff << shift;
            out = (out & ~mask) | bits;
        }
        return out;
    }
}
