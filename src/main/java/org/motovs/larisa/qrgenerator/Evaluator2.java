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
    @Override
    public int evaluate(byte[][] image) {
        int penaltyTotal = 0;
        for (int i = 0; i < image.length - 1; i++) {
            for (int j = 0; j < image.length - 1; j++) {
                if ((BitPlacement.moduleIsBlack(image, i, j) == BitPlacement.moduleIsBlack(image, i + 1, j)) &&
                        (BitPlacement.moduleIsBlack(image, i + 1, j + 1) == BitPlacement.moduleIsBlack(image, i, j + 1)) &&
                        (BitPlacement.moduleIsBlack(image, i, j) == BitPlacement.moduleIsBlack(image, i + 1, j + 1))) {
                    penaltyTotal += 3;
                }
            }
        }
        return penaltyTotal;
    }
}
