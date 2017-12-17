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

public class Evaluator3 implements MaskEvaluator {
    @Override
    public int evaluate(byte[][] image) {
        final int pattern1 = 1488;
        final int pattern2 = 93;
        final int bitsUsed = (1 << 11) - 1;
        int penaltyTotal = 0;

        for (int i = 0; i < image.length; i++) {
            int current = 0;
            for (int j = 0; j < image.length; j++) {
                current = current << 1;
                current &= bitsUsed;
                if (BitPlacement.moduleIsBlack(image, i, j)) {
                    current |= 1;
                }
                if (current == pattern1 || (current == pattern2 && j > 10)) {
                    penaltyTotal += 40;
                }
            }
        }

        for (int j = 0; j < image.length; j++) {
            int current = 0;
            for (int i = 0; i < image.length; i++) {
                current = current << 1;
                current &= bitsUsed;
                if (BitPlacement.moduleIsBlack(image, i, j)) {
                    current |= 1;
                }
                if (current == pattern1 || (current == pattern2 && i > 10)) {
                    penaltyTotal += 40;
                }
            }
        }
        return penaltyTotal;
    }
}
