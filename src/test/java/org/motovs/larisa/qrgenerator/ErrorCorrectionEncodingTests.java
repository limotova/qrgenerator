package org.motovs.larisa.qrgenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ErrorCorrectionEncodingTests {

    @Test
    public void testExecute() {
        String bitMessage = "01000000 00110110 11010111 00110110 01110000 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100";
        String errorPortion = " 11101110 10111111 11100000 01100100 11001011 11101000 11100001 00100110 00011100 11011011";
        ErrorCorrectionEncoding errorCorrectionEncoding = new ErrorCorrectionEncoding();
        DataEncoding.EncodedData encodedData = new DataEncoding.EncodedData(TestUtils.parseBitBuffer(bitMessage), 1, 16, 10);
        ErrorCorrectionEncoding.DataWithErrorCorrection dataWithErrorCorrection = errorCorrectionEncoding.execute(encodedData);
        assertEquals(bitMessage + errorPortion, dataWithErrorCorrection.bitBuffer.toString());
        assertEquals(dataWithErrorCorrection.version, 1);
    }
}
