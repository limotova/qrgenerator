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

import java.util.Arrays;
import java.util.List;

public class Pipeline<I, O> {
    private final List<Step> pipelineSteps;

    Pipeline(Step...steps){
        pipelineSteps = Arrays.asList(steps);
    }

    @SuppressWarnings("unchecked")
    public O execute(I input) {
        Object val = input;
        for (Step step : pipelineSteps) {
            val = step.execute(val);
        }
        return (O) val;
    }
}
