package com.lookforlog.protocol.util;

import com.lookforlog.protocol.util.Conversion;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConversionTest {

    @Test
    public void testBinaryToByte(){
        boolean[] src = new boolean[]{false, true, true, false, true, false};
        byte expect = 0x2;
        assertEquals(expect, Conversion.binaryToByte(src, 2, (byte)1, 2));
    }

    @Test
    public void testBinaryToShort(){
        boolean[] src = new boolean[]{ false, true, true, false, true, true};
        short expect = 5;
        assertEquals(expect, Conversion.binaryToShort(src, 2, (short)2, 3));
    }

    @Test
    public void testIntToByteArray(){
        byte[] result = new byte[2];
        int res = 0x332344;
        Conversion.intToByteArray(res, 8, result, 1, 1);
        assertEquals(0, result[0]);
        assertEquals(0x33, result[1]);
    }

    @Test
    public void testIntToBinary(){
        boolean[] result = new boolean[8];
        int res = 0x23;
        Conversion.intToBinary(res, 24, result, 0, 8);
        assertEquals(true, result[2]);
        assertEquals(true, result[6]);
        assertEquals(true, result[7]);
    }

    @Test
    public void testByteToBinary(){
        boolean[] result = new boolean[4];
        byte res = 0x23;
        Conversion.byteToBinary(res, 4, result, 0, 4);
        assertEquals(true, result[2]);
        assertEquals(true, result[3]);
    }

    @Test
    public void testShortToByteArray(){
        byte[] result = new byte[2];
        short res = 0x1123;
        Conversion.shortToByteArray(res, 0, result, 1, 1);
        assertEquals(0x11, result[1]);

        Conversion.shortToByteArray(res, 8, result, 1, 1);
        assertEquals(0x23, result[1]);
    }

    @Test
    public void testShortToBinary(){
        boolean[] result = new boolean[5];
        short res = 0x1123;
        Conversion.shortToBinary(res, 0, result, 1, 4);
        assertEquals(true, result[4]);

        Conversion.shortToBinary(res, 8, result, 1, 4);
        assertEquals(true, result[3]);
    }

    @Test
    public void testByteArrayToShort(){
        byte[] res = new byte[]{0x11, 0x23};
        short r = Conversion.byteArrayToShort(res, 0, (short)0x2533, 1);
        assertEquals(0x2511, r);

        short r2 = Conversion.byteArrayToShort(res, 0, (short)0x2533, 2);
        assertEquals(0x1123, r2);

    }

    @Test
    public void testBinaryToInt(){
        boolean[] src = new boolean[]{false, true, true, false, true, false};
        int r = Conversion.binaryToInt(src, 2, 0x2533, 4);
        assertEquals(0x253A, r);
    }

    @Test
    public void testByteArrayToInt(){
        byte[] res = new byte[]{0x11, 0x23};
        int r = Conversion.byteArrayToInt(res, 0, 0x23232533, 2);
        assertEquals(0x23231123, r);
    }
}
