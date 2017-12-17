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

public class Evaluator4 implements MaskEvaluator {

    @Override
    public int evaluate(byte[][] image) {
        int blackCount = 0;
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image.length; j++) {
                if (BitPlacement.moduleIsBlack(image, i, j)) {
                    blackCount++;
                }
            }
        }
        int percentBlack = 100 * blackCount / (image.length * image.length);
        int prevMultiple5 = percentBlack - (percentBlack % 5);
        int nextMultiple5 = prevMultiple5 + 5;
        if (percentBlack % 5 == 0 && percentBlack != 0) {
            prevMultiple5 -= 5;
        }
        int difference1 = Math.abs(prevMultiple5 - 50);
        int difference2 = Math.abs(nextMultiple5 - 50);
        difference1 /= 5;
        difference2 /= 5;
        return Math.min(difference1, difference2) * 10;
    }
}
