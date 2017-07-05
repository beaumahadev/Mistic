package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Altair on 4/6/2017.
 */
public class Perlin {
    public int repeatXY, repeatZ;
    private static final int ANIM_SPAN = 360;

    public Perlin(int repeatXY, int repeatZ) {
        this.repeatXY = repeatXY;
        this.repeatZ = repeatZ;
    }

    public Perlin() {
        this.repeatXY = -1;
        this.repeatZ = -1;
    }

    private static final int[] p = {
            151, 160, 137, 91, 90, 15,
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180,
            151, 160, 137, 91, 90, 15,
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
    };

    public double octaveNoise(double x, double y, double z, int octaves) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxVal = 0;
        double persistence = .4;

        for(int i=0;i<octaves;i++) {
            total += noise(x * frequency, y * frequency, z * frequency) * amplitude;

            maxVal += amplitude;

            amplitude *= persistence;
            frequency *= 2;
        }

        return total/maxVal;
    }

    public double noise(double x, double y, double z) {
        if (repeatXY > 0 && repeatZ > 0) {
            x = x % repeatXY;
            y = y % repeatXY;
            z = z % repeatZ;
        }

        int xi = (int)x & 255;
        int yi = (int)y & 255;
        int zi = (int)z & 255;

        double xf = x - (int) x;
        double yf = y - (int) y;
        double zf = z - (int) z;

        double u = fade(xf);
        double v = fade(yf);
        double w = fade(zf);

        int a, b, c, d, e, f, g, h;
        a = p[p[p[      xi ]+      yi ]+     zi ];
        b = p[p[p[      xi ]+incXY(yi)]+     zi ];
        c = p[p[p[      xi ]+      yi ]+incZ(zi)];
        d = p[p[p[      xi ]+incXY(yi)]+incZ(zi)];
        e = p[p[p[incXY(xi)]+      yi ]+     zi ];
        f = p[p[p[incXY(xi)]+incXY(yi)]+     zi ];
        g = p[p[p[incXY(xi)]+      yi ]+incZ(zi)];
        h = p[p[p[incXY(xi)]+incXY(yi)]+incZ(zi)];

        double x1, x2, y1, y2;
        x1 = lerp(grad (a, xf, yf  , zf), grad(e, xf-1, yf  , zf), u);										// surrounding points in its unit cube.
        x2 = lerp(grad (b, xf, yf-1, zf), grad(f, xf-1, yf-1, zf), u);
        y1 = lerp(x1, x2, v);

        x1 = lerp(grad (c, xf, yf  , zf-1), grad (g, xf-1, yf  , zf-1), u);
        x2 = lerp(grad (d, xf, yf-1, zf-1), grad (h, xf-1, yf-1, zf-1), u);
        y2 = lerp(x1, x2, v);

        return (lerp(y1, y2, w) + 1) / 2;
    }

    private int incXY(int num) {
        num++;
        if (repeatXY > 0) num %= repeatXY;

        return num;
    }

    private int incZ(int num) {
        num++;
        if (repeatZ > 0) num %= repeatZ;

        return num;
    }

    private static double grad(int hash, double x, double y, double z) {
        switch(hash & 0xF)
        {
            case 0x0: return  x + y;
            case 0x1: return -x + y;
            case 0x2: return  x - y;
            case 0x3: return -x - y;
            case 0x4: return  x + z;
            case 0x5: return -x + z;
            case 0x6: return  x - z;
            case 0x7: return -x - z;
            case 0x8: return  y + z;
            case 0x9: return -y + z;
            case 0xA: return  y - z;
            case 0xB: return -y - z;
            case 0xC: return  y + x;
            case 0xD: return -y + z;
            case 0xE: return  y - x;
            case 0xF: return -y - z;
            default: return 0; // never happens
        }
    }

    private static double fade(double t) {
        // Fade function as defined by Ken Perlin.  This eases coordinate values
        // so that they will "ease" towards integral values.  This ends up smoothing
        // the final output.
        return t * t * t * (t * (t * 6 - 15) + 10);            // 6t^5 - 15t^4 + 10t^3
    }

    private static double lerp(double a, double b, double x) {
        return a + x * (b - a);
    }

    // Adapted from code provided at http://flafla2.github.io/2014/08/09/perlinnoise.html
    public static void main(String[] args) {
        final int WIDTH = 1080, HEIGHT = 576;

        float[][] data = new float[ANIM_SPAN][WIDTH * HEIGHT];

        Perlin perlin = new Perlin(36, 2);
        for (int t = 0; t < ANIM_SPAN; t++){
            int count = 0;
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    data[t][count++] = (float)Math.pow(perlin.octaveNoise(36.0 * (float) x / WIDTH, 36.0 * (float) y / HEIGHT, 2.0 * (float) t / ANIM_SPAN, 4), .2);
                }
            }
        }

        float minValue, maxValue;
        int[] pixelData = new int[WIDTH * HEIGHT];

        for (int t=0; t<ANIM_SPAN; t++) {
            minValue = data[t][0];
            maxValue = data[t][0];
            for (int i = 0; i < data[t].length; i++) {
                minValue = Math.min(data[t][i], minValue);
                maxValue = Math.max(data[t][i], maxValue);
            }

            for (int i = 0; i < data[t].length; i++) {
                pixelData[i] = (int) (255 * (data[t][i] - minValue) / (maxValue - minValue));
            }

            BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
            img.getRaster().setPixels(0, 0, WIDTH, HEIGHT, pixelData);

//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            byte[] pixelBytes = new byte[0];
//            try {
//                ImageIO.write(img, "jpg", outputStream);
//                outputStream.flush();
//                pixelBytes = outputStream.toByteArray();
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Pixmap perlinPix = new Pixmap(pixelBytes, 0, pixelBytes.length);
//            perlinTex[t] = new Texture(perlinPix);

            File fileImage = new File("mistic/noise/noise" + t + ".png");
            try {
                ImageIO.write(img, "png", fileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        File output = new File("image.png");
//        try {
//            ImageIO.write(img, "jpg", output);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//		ByteBuffer byteBuffer = ByteBuffer.allocate(4*pixelData.length);
//		for (int j=0; j<pixelData.length; j++) {
//			byteBuffer.putInt(pixelData[j]);
//		}
//		byte[] pixelBytes = byteBuffer.array();
//		System.out.println(pixelData[1]);
//		System.out.println((byte)pixelData[1]);
//		System.out.println(byteBuffer.get(7));
//		Pixmap perlinPix = new Pixmap(pixelBytes, 0, WIDTH*HEIGHT);
//		Pixmap perlinPix = new Pixmap(new FileHandle("image.png"));
//		PixmapIO.writePNG(new FileHandle("image2.png"), perlinPix);
    }
}