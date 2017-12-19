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

public class Evaluator2 implements MaskEvaluator {

    /**
     * Evaluates QR Code by seeing how many 2x2 blocks of a single color there are
     * (+3 penalty for each 2x2 block)
     *
     * @param image QR code to be evaluated
     * @return  penalty points
     */
    @Override
    public int evaluate(byte[][] image) {
        int penaltyTotal = 0;
        for (int i = 0; i < image.length - 1; i++) {
            for (int j = 0; j < image.length - 1; j++) {
                if ((BitPlacement.bitIsBlack(image, i, j) == BitPlacement.bitIsBlack(image, i + 1, j)) &&
                        (BitPlacement.bitIsBlack(image, i + 1, j + 1) == BitPlacement.bitIsBlack(image, i, j + 1)) &&
                        (BitPlacement.bitIsBlack(image, i, j) == BitPlacement.bitIsBlack(image, i + 1, j + 1))) {
                    penaltyTotal += 3;
                }
            }
        }
        return penaltyTotal;
    }
}
