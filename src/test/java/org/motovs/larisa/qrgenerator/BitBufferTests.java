package org.motovs.larisa.qrgenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitBufferTests {

    @Test
    public void testPush() {
        BitBuffer bitBuffer = new BitBuffer();
        bitBuffer.push((byte) 7, 4);
        assertEquals("0111", bitBuffer.toString());
        bitBuffer.push((byte) 250, 8);
        assertEquals("01111111 1010", bitBuffer.toString());
    }


}
