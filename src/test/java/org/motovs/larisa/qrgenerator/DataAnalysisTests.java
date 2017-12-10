package org.motovs.larisa.qrgenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataAnalysisTests {

    @Test
    public void testExecute() {
        DataAnalysis dA = new DataAnalysis();
        String testString = "I'm a string!!";
        DataAnalysis.AnalyzedString analyzedString = dA.execute(testString);
        assertEquals(Mode.BYTE, analyzedString.mode);
        assertEquals(1, analyzedString.version);
        assertEquals(testString, analyzedString.inputString);
    }
}
