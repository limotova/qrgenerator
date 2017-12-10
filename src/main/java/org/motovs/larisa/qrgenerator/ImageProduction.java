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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProduction implements Step<BitPlacement.PlacedBits, String>{

    private final int BLOCK_SIZE = 5;

    @Override
    public String execute(BitPlacement.PlacedBits input) {
        return createImage(input.image);
    }

    private String createImage(byte[][] image){
        int size = image.length + 8;
        BufferedImage bufferedImage = new BufferedImage(size * BLOCK_SIZE, size * BLOCK_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();

        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, size * BLOCK_SIZE, size * BLOCK_SIZE);

        graphics2D.setColor(Color.BLACK);
        for(int i = 0; i < image.length; i++)
            for(int j = 0; j < image[i].length; j++)
                if(image[i][j] == BitPlacement.RESERVED_BLACK || image[i][j] == BitPlacement.BLACK)
                    graphics2D.fillRect((4 + j) * BLOCK_SIZE, (4 + i) * BLOCK_SIZE , BLOCK_SIZE, BLOCK_SIZE);

        String name = "qr_image.png";
        try{
            File file = new File(name);
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e){
            throw new QRException("Cannot save image", e);
        }
        return name;
    }

}
