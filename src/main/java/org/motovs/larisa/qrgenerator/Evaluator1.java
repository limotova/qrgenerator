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

public class Evaluator1 implements MaskEvaluator {

    /**
     * Evaluates QR Code by seeing how many consecutive strips of one color there are
     * (+3 penalty for 5 consecutive bits, +1 for each bit above 5)
     *
     * @param image QR code to be evaluated
     * @return  penalty points
     */
    @Override
    public int evaluate(byte[][] image) {
        // TODO: optimize this
        int penaltyTotal = 0;
        for (int i = 0; i < image.length; i++) {
            boolean prevBlack = false;
            int consecutiveCount = 0;
            for (int j = 0; j < image.length; j++) {
                if (prevBlack == BitPlacement.bitIsBlack(image, i, j)) {
                    consecutiveCount++;
                    if (consecutiveCount == 5) {
                        penaltyTotal += 3;
                    } else if (consecutiveCount > 5) {
                        penaltyTotal++;
                    }
                } else {
                    consecutiveCount = 1;
                    prevBlack = !prevBlack;
                }
            }
        }


        for (int j = 0; j < image.length; j++) {
            boolean prevBlack = false;
            int consecutiveCount = 0;
            for (int i = 0; i < image.length; i++) {
                if (prevBlack == BitPlacement.bitIsBlack(image, i, j)) {
                    consecutiveCount++;
                    if (consecutiveCount == 5) {
                        penaltyTotal += 3;
                    } else if (consecutiveCount > 5) {
                        penaltyTotal++;
                    }
                } else {
                    consecutiveCount = 1;
                    prevBlack = !prevBlack;
                }
            }
        }
        return penaltyTotal;
    }
}
