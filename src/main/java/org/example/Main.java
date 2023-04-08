package org.example;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.AlphaInterpolation;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;


public class Main {
    public static int targetHeight = 400;
    public static int targetWidth = 300;

    public static double calculateScale(int height, int targetHeight) {
        return (((double) targetHeight) / height);
    }

    public static void main(String[] args) throws IOException {
        File inputFile = new File("./src/main/resources/orig.jpg");
        BufferedImage inputImage = ImageIO.read(inputFile);
        targetWidth = (int) Math.round(inputImage.getWidth() * calculateScale(inputImage.getHeight(), targetHeight));
        thumbnailSolution(inputImage);
        graphicSolution(inputImage);
        imgGetScaledSolution(inputImage);
        scalrSolution(inputImage);

        imagejSolution();
    }

    public static void thumbnailSolution(BufferedImage img) throws IOException {
        var start = System.nanoTime();

        var res = Thumbnails.of(img)
                .scalingMode(ScalingMode.BICUBIC)
                .rendering(Rendering.QUALITY)
                .alphaInterpolation(AlphaInterpolation.QUALITY)
                .antialiasing(Antialiasing.ON)
                .outputFormat("jpg")
                .outputQuality(1)
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .asBufferedImage();

        var finish = System.nanoTime();

        System.out.println("Time consumed by thumbnail is " + (finish - start)/1e6 + " ns");

        File file = new File("./src/main/resources/res/thumbnail.jpg");
        ImageIO.write(res, "jpg", file);
    }

    public static void graphicSolution(BufferedImage img) throws IOException {
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        var start = System.nanoTime();
        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(Re);
        graphics2D.drawImage(img, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        var finish = System.nanoTime();

        System.out.println("Time consumed by graphic2d is " + (finish - start)/1e6 + " ns");

        File file = new File("./src/main/resources/res/graphic2d.jpg");
        ImageIO.write(outputImage, "jpg", file);
    }

    public static void scalrSolution(BufferedImage img) throws IOException {
        var start = System.nanoTime();

        var res = Scalr.resize(img, Scalr.Method.ULTRA_QUALITY,
                Scalr.Mode.AUTOMATIC,
                targetWidth,
                targetHeight, Scalr.OP_ANTIALIAS);

        var finish = System.nanoTime();

        System.out.println("Time consumed by Scalr is " + (finish - start)/1e6 + " ns");

        File file = new File("./src/main/resources/res/scalr.jpg");
        ImageIO.write(res, "jpg", file);
    }

    public static void imgGetScaledSolution(BufferedImage img) throws IOException {

        var start = System.nanoTime();
        var res = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_REPLICATE);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(res, 0, 0, null);
        var finish = System.nanoTime();

        System.out.println("Time consumed by getScaled is " + (finish - start)/1e6 + " ns");

        File file = new File("./src/main/resources/res/getScaled.jpg");
        ImageIO.write(outputImage, "jpg", file);
    }

//    public static void imgjSolution() {
//        var srcImage = new ImgOpener().openImgs("source-image.png", new FloatType()).get(0);
//
//        // Create a scaled version of the image
//        Scale2D scaleTransform = new Scale2D(
//                (double) targetWidth / srcImage.dimension(0),
//                (double) targetHeight / srcImage.dimension(1));
//        Img<FloatType> scaledImage = Views
//                .interval(Views.raster(RealViews., srcImage);
//
//
//
//        new ImgSaver().saveImg("imagej2.jpg", scaledImage);
//    }

    public static void imagejSolution() {
        ImagePlus imp = IJ.openImage("./src/main/resources/orig.jpg");

        var start = System.nanoTime();

        // Specify the desired output size
        int newWidth = targetWidth;
        int newHeight = targetHeight;

        // Create a new empty image with the desired size
        ImageProcessor ip = new ByteProcessor(newWidth, newHeight);

        // Scale the image using bicubic interpolation
        imp.setProcessor(imp.getProcessor().resize(newWidth, newHeight, true).convertToByte(true));

        // Copy the scaled image into the new image
        BufferedImage bi = imp.getBufferedImage();
        WritableRaster wr = bi.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        byte[] data = db.getData();

        ip.setPixels(data);

        // Save the resulting image to a file
        ImagePlus outputImp = new ImagePlus("output", ip);

        var finish = System.nanoTime();

        System.out.println("Time consumed by ImageJ is " + (finish - start)/1e6 + " ns");


        FileSaver fs = new FileSaver(outputImp);
        fs.saveAsJpeg("./src/main/resources/imagej.jpg");
    }

}