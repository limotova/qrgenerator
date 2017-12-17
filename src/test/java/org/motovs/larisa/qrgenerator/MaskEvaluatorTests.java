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

import static org.junit.Assert.assertEquals;

public class MaskEvaluatorTests {

    // sample QR code and data are taken from https://www.thonky.com/qr-code-tutorial/data-masking
    final String picture = "####### ##    #######\n" +
            "#     # #  #  #     #\n" +
            "# ### # #  ## # ### #\n" +
            "# ### # #     # ### #\n" +
            "# ### # # #   # ### #\n" +
            "#     #   #   #     #\n" +
            "####### # # # #######\n" +
            "        #            \n" +
            " ## # ##    # # #####\n" +
            " #      ####    #   #\n" +
            "  ## ### ##   # ##   \n" +
            " ## ## #  ## # # ### \n" +
            "#   # # # ### ### # #\n" +
            "        ## #  #   # #\n" +
            "####### # #    # ##  \n" +
            "#     #  # ## ## #   \n" +
            "# ### # # #   #######\n" +
            "# ### #  # # # #   # \n" +
            "# ### # #   #### #  #\n" +
            "#     # # ## #   # ##\n" +
            "#######     ####    #";

    final byte[][] image = TestUtils.stringToPicture(picture);


    @Test
    public void testEvaluator1() {
        assertEquals(180, (new Evaluator1()).evaluate(image));
    }

    @Test
    public void testEvaluator2() {
        assertEquals(90, (new Evaluator2()).evaluate(image));
    }

    @Test
    public void testEvaluator3() {
        assertEquals(80, (new Evaluator3()).evaluate(image));
    }

    @Test
    public void testEvaluator4() {
        assertEquals(0, (new Evaluator4()).evaluate(image));
    }
}
