/*
 * Copyright 2017 Larisa Motova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.motovs.larisa.qrgenerator;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class BitPlacementTests {

    @Test
    public void testExecute() {
        String bitMessage = "01000000 00110110 11010111 00110110 01110000 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100 00010001 11101100";
        String errorPortion = " 11101110 10111111 11100000 01100100 11001011 11101000 11100001 00100110 00011100 11011011";

        String finalMessage =
                "#######  # #  #######\n" +
                        "#     # ####  #     #\n" +
                        "# ### # # #   # ### #\n" +
                        "# ### # ## #  # ### #\n" +
                        "# ### #   # # # ### #\n" +
                        "#     #   ##  #     #\n" +
                        "####### # # # #######\n" +
                        "        #            \n" +
                        "#     # ## # ##  ### \n" +
                        " ## #  ####### ## ## \n" +
                        "  #  ##  ## # ## # # \n" +
                        "### #   ## ##### ### \n" +
                        "  ### #  #########   \n" +
                        "        ### #    ## #\n" +
                        "#######  # # #  ## # \n" +
                        "#     #       #   ## \n" +
                        "# ### #   ## #  # #  \n" +
                        "# ### #  ####### #   \n" +
                        "# ### #  # ### ### ##\n" +
                        "#     #   ###### #   \n" +
                        "####### ##  #  #  ## \n";

        BitPlacement bitPlacement = new BitPlacement();
        ErrorCorrectionEncoding.DataWithErrorCorrection dataWithErrorCorrection =
                new ErrorCorrectionEncoding.DataWithErrorCorrection(TestUtils.parseBitBuffer(bitMessage + errorPortion), 1);
        BitPlacement.PlacedBits placedBits = bitPlacement.execute(dataWithErrorCorrection);

        assertEquals(finalMessage, TestUtils.pictureToString(placedBits.image));
    }

}
