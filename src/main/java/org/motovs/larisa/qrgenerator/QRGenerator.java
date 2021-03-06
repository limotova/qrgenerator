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

public class QRGenerator {

    public static void main(String[] args) {

        Pipeline<String, String> pipeline = new Pipeline<>(
                new DataAnalysis(),
                new DataEncoding(),
                new ErrorCorrectionEncoding(),
                new BitPlacement(),
                new ImageProduction()
        );
        pipeline.execute("I'm a simple message!");
    }

}
