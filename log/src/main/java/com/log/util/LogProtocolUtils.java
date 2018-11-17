package com.log.util;

import org.apache.commons.lang3.Validate;

/**
 * log protocol encode & decode utils
 */
public class LogProtocolUtils {

    /**
     * Generate 16 bit checksum
     *
     * @param from frame to generate the checksum
     * @return check sum, 16 bit
     */
    public static int calculateChecksum(byte[] from) {
        Validate.isTrue(from.length % 2 == 0, "Frame size should be even");
        int checksum = 0;
        for (int i = 0; i < from.length; i += 2) {
            checksum += Conversion.byteArrayToInt(from, i, 0, 2);
        }
        while (checksum >> 16 != 0) {
            checksum = (checksum >> 16) + (checksum & 0x0000ffff);
        }
        return ~checksum & 0x0000ffff;
    }
}
