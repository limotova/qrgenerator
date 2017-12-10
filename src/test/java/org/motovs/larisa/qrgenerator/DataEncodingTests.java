package org.motovs.larisa.qrgenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataEncodingTests {

    @Test
    public void testExecute() {
        String testString = "msg";
        String bitSeq = "01000000 00110110 11010111 00110110 01110000 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100";
        DataAnalysis.AnalyzedString analyzedString = new DataAnalysis.AnalyzedString(testString, Mode.BYTE, 1);
        DataEncoding dataEncoding = new DataEncoding();
        DataEncoding.EncodedData encodedData = dataEncoding.execute(analyzedString);
        assertEquals(encodedData.bitBuffer.toString(), bitSeq);
        assertEquals(encodedData.codeWords, 16);
        assertEquals(encodedData.errorWords, 10);
        assertEquals(encodedData.version, 1);
    }
}
