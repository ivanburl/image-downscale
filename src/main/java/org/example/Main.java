package org.example;

import com.mortennobel.imagescaling.ResampleFilters;
import com.twelvemonkeys.image.ResampleOp;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Dithering;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.imgscalr.Scalr;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class Main {

    public static int targetHeight = 200;
    public static int targetWidth = 300;

    public static double calculateScale(int height, int targetHeight) {
        return (((double) targetHeight) / height);
    }

    public static void main(String[] args) throws IOException {
        File inputFile = new File("./src/main/resources/orig2.jpg");
        BufferedImage inputImage = ImageIO.read(inputFile);
        targetWidth = (int) Math.round(
            inputImage.getWidth() * calculateScale(inputImage.getHeight(), targetHeight));

        //thumbnailSolution(inputImage);
        //graphicSolution(inputImage);
        //imgGetScaledSolution(inputImage);
        //scalrSolution(inputImage);

//        System.out.println(
//            String.format("Scale factor %s", calculateScale(inputImage.getHeight(), targetHeight)));
        //progressiveScaling(inputImage, calculateScale(inputImage.getHeight(), targetHeight));
        //NobilsSolution(inputImage);
        //lancoz3MonkeySolution(inputImage);
            OpenCV();
        //imagejSolution();
    }

    public static BufferedImage thumbnailSolution(BufferedImage img, double scale, double stamp)
        throws IOException {
        var start = System.nanoTime();

        var res = Thumbnails.of(img)
            .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
            .rendering(Rendering.SPEED)
            .antialiasing(Antialiasing.OFF)
            .dithering(Dithering.ENABLE)
            .outputFormat("jpg")
            .outputQuality(1)
            .scale(scale)
            .asBufferedImage();

        var finish = System.nanoTime();

        System.out.println("Time consumed by thumbnail is " + (finish - start) / 1e6 + " ns");

        File file = new File(String.format("./src/main/resources/res/thumbnail_%s.jpg", stamp));
        ImageIO.write(res, "jpg", file);

        return res;
    }

    public static void graphicSolution(BufferedImage img) throws IOException {
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight,
            BufferedImage.TYPE_INT_RGB);

        var start = System.nanoTime();
        Graphics2D graphics2D = outputImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
            RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(img, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        var finish = System.nanoTime();

        System.out.println("Time consumed by graphic2d is " + (finish - start) / 1e6 + " ns");

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

        System.out.println("Time consumed by Scalr is " + (finish - start) / 1e6 + " ns");

        File file = new File("./src/main/resources/res/scalr.jpg");
        ImageIO.write(res, "jpg", file);
    }

    public static void imgGetScaledSolution(BufferedImage img) throws IOException {

        var start = System.nanoTime();
        var res = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_AREA_AVERAGING);

        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight,
            BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(res, 0, 0, null);
        var finish = System.nanoTime();

        System.out.println("Time consumed by getScaled is " + (finish - start) / 1e6 + " ns");

        File file = new File("./src/main/resources/res/getScaled.jpg");
        ImageIO.write(outputImage, "jpg", file);
    }

//    public static void lancoz3MonkeySolution(BufferedImage bufferedImage) throws IOException {
//        var start = System.nanoTime();
//
//        BufferedImageOp resampler = new ResampleOp(targetWidth, targetHeight, ResampleOp.FILTER_LANCZOS);
//        BufferedImage output = resampler.filter(bufferedImage, null);
//
//        var finish = System.nanoTime();
//
//        System.out.println("Time consumed by monkey is " + (finish - start)/1e6 + " ns");
//
//        File file = new File("./src/main/resources/res/monkey_impl.jpg");
//        ImageIO.write(output, "jpg", file);
//    }

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

        System.out.println("Time consumed by ImageJ is " + (finish - start) / 1e6 + " ns");

        FileSaver fs = new FileSaver(outputImp);
        fs.saveAsJpeg("./src/main/resources/imagej.jpg");
    }

    public static BufferedImage bestScaling(BufferedImage img, double scale, double stamp)
        throws IOException {
        BufferedImage outputImage = new BufferedImage((int) Math.round(img.getWidth() * scale),
            (int) Math.round(img.getHeight() * scale), BufferedImage.TYPE_INT_RGB);

        var start = System.nanoTime();
        Graphics2D graphics2D = outputImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
            RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(img, 0, 0, (int) Math.round(img.getWidth() * scale),
            (int) Math.round(img.getHeight() * scale), null);
        graphics2D.dispose();
        var finish = System.nanoTime();

        System.out.println("Time consumed by scaling is " + (finish - start) / 1e6 + " ns");

        File file = new File(
            String.format("./src/main/resources/res/progressive_scaling_%s.jpg", stamp));
        ImageIO.write(outputImage, "jpg", file);

        return outputImage;
    }

    public static void progressiveScaling(BufferedImage bufferedImage, double scaleFactor)
        throws IOException {
        var start = System.nanoTime();

        while (scaleFactor < 0.5) {
            bufferedImage = thumbnailSolution(bufferedImage, 0.5, scaleFactor);
            scaleFactor /= 0.5;
            System.out.println("Scale factor res: " + scaleFactor);
        }

        bufferedImage = thumbnailSolution(bufferedImage, scaleFactor, scaleFactor);

        var finish = System.nanoTime();

        System.out.println(
            "Time consumed by progressive scaling is " + (finish - start) / 1e6 + " ns");

        File file = new File("./src/main/resources/res/progressive_scaling.jpg");
        ImageIO.write(bufferedImage, "jpg", file);
    }

    public static void NobilsSolution(BufferedImage img) throws IOException {
        com.mortennobel.imagescaling.ResampleOp resizeOp = new com.mortennobel.imagescaling.ResampleOp(targetWidth, targetHeight);
        resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
        var start = System.nanoTime();
        BufferedImage scaledImage = resizeOp.filter(img, null);
        var finish = System.nanoTime();
        System.out.println(
            "Time consumed by nobils is " + (finish - start) / 1e6 + " ns");
        File file = new File("./src/main/resources/res/nobelSolution.jpg");
        ImageIO.write(scaledImage, "jpg", file);
    }

    public static void OpenCV() {
        Mat inputImage = Imgcodecs.imread("/src/main/resources/orig2.jpg");

        // Define the output size
        var start = System.nanoTime();
        // Create the output image matrix
        Mat outputImage = new Mat(targetHeight, targetWidth, inputImage.type());

        // Downscale the input image using Lanczos interpolation
        Imgproc.resize(inputImage, outputImage, new Size(targetWidth, targetHeight), 0, 0, Imgproc.INTER_LANCZOS4);
        var finish = System.nanoTime();
        System.out.println(
            "Time consumed by opencv is " + (finish - start) / 1e6 + " ns");
        // Save the output image
        Imgcodecs.imwrite("/src/main/resources/res/opencv.jpg", outputImage);
    }
}