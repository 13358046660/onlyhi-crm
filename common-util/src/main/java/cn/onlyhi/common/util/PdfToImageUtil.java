package cn.onlyhi.common.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/12/27.
 */
public class PdfToImageUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfToImageUtil.class);
    private static final String JPG = "jpg";
    private static final String PNG = "png";
    private static final String JPG_SUFFIX = "." + JPG;
    private static final String PNG_SUFFIX = "." + PNG;

    /*public static Integer getPages(String pdfFile) {
        PDDocument doc = null;
        int pageCount = 0;
        try {
            doc = PDDocument.load(new File(pdfFile));
            pageCount = doc.getNumberOfPages();
        } catch (IOException e) {
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                }
            }
        }
        return pageCount;
    }*/

    public static ConverResult convert(File pdfFile, File imageSaveDir) throws Exception {
        long startTime = System.currentTimeMillis();
        if (pdfFile == null || pdfFile.length() <= 0) {
            throw new Exception("pdfFile is null or size == 0 ");
        }
        ConverResult converResult = new ConverResult();
        List<String> imageNameList = new ArrayList<>();
        if (!imageSaveDir.exists()) {
            imageSaveDir.mkdirs();
        }
        PDDocument doc = PDDocument.load(pdfFile);
        PDFRenderer renderer = new PDFRenderer(doc);
        int pageSize = doc.getNumberOfPages();
        LOGGER.info("pdf pageSize={}", pageSize);

        //---开启并行任务
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        PdfToImageTask task = new PdfToImageTask(renderer, imageSaveDir, 0, pageSize);
        //执行一个任务
        forkJoinPool.submit(task);

        /*while (!task.isDone()) {
            TimeUnit.MILLISECONDS.sleep(50);
            LOGGER.info("task is executing...");
        }
        if (task.isCompletedAbnormally()) {
            LOGGER.info("an exception has been throwed");
            LOGGER.info("exception:", task.getException());
            throw new Exception(task.getException());
        } else {
            LOGGER.info("no exception");
        }*/

        task.join();
        // 关闭线程池
        forkJoinPool.shutdown();
        //---并行任务结束
        doc.close();
        LOGGER.info("pdf conver success pageSize={}", task.atomicInteger);
        for (int i = 0; i < pageSize; i++) {
            String imageName = i + PNG_SUFFIX;
            imageNameList.add(imageName);
        }
        String imageName0 = 0 + PNG_SUFFIX;
        File imageFile = new File(imageSaveDir, imageName0);
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        converResult.setImageWidth(imageWidth);
        converResult.setImageHeight(imageHeight);
        converResult.setImageNameList(imageNameList);
        converResult.setPageSize(pageSize);
        LOGGER.info("------------- pdfToImage success -------------------");
        LOGGER.info("pdf转image时间:{}ms", System.currentTimeMillis() - startTime);
        return converResult;
    }


    /**
     * 转换pdf文件指定页数为图片
     *
     * @param pdfFile
     * @param imageSaveDir
     * @param pageIndex
     * @throws Exception
     */
    public static void convertOne(File pdfFile, File imageSaveDir, int pageIndex) throws Exception {
        if (pdfFile == null || pdfFile.length() <= 0) {
            throw new Exception("pdfFile is null or size == 0 ");
        }
        if (!imageSaveDir.exists()) {
            imageSaveDir.mkdirs();
        }
        PDDocument doc = PDDocument.load(pdfFile);
        PDFRenderer renderer = new PDFRenderer(doc);
        int pageSize = doc.getNumberOfPages();
        if (pageIndex >= pageSize) {
            throw new Exception("pageIndex=" + pageIndex + " >=pageSize= " + pageSize);
        }
        String imageName = pageIndex + PNG_SUFFIX;
        BufferedImage image = renderer.renderImageWithDPI(pageIndex, 100);
        ImageIO.write(image, PNG, new File(imageSaveDir, imageName));    //正常生成图片
    }

    /**
     * 一张一张转图片
     *
     * @param pdfFile
     * @param imageSaveDir
     * @throws Exception
     */
    public static ConverResult convertOne2One(File pdfFile, File imageSaveDir) throws Exception {
        long startTime = System.currentTimeMillis();
        if (pdfFile == null || pdfFile.length() <= 0) {
            throw new Exception("pdfFile is null or size == 0 ");
        }
        ConverResult converResult = new ConverResult();
        List<String> imageNameList = new ArrayList<>();
        if (!imageSaveDir.exists()) {
            imageSaveDir.mkdirs();
        }
        PDDocument doc = PDDocument.load(pdfFile);
        PDFRenderer renderer = new PDFRenderer(doc);
        int pageSize = doc.getNumberOfPages();
        LOGGER.info("pdf pageSize={}", pageSize);

        String imageName = null;
        BufferedImage image = null;
        for (int i = 0; i < pageSize; i++) {
            imageName = i + PNG_SUFFIX;
            imageNameList.add(imageName);
            try {
                image = renderer.renderImageWithDPI(i, 100);
                ImageIO.write(image, PNG, new File(imageSaveDir, imageName));    //正常生成图片
            } catch (IOException e) {
                LOGGER.info("转换图片{}失败:", imageName, e);
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        doc.close();

        String imageName0 = 0 + PNG_SUFFIX;
        File imageFile = new File(imageSaveDir, imageName0);
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        converResult.setImageWidth(imageWidth);
        converResult.setImageHeight(imageHeight);
        converResult.setImageNameList(imageNameList);
        double doubleLength=imageFile.length()/1024.0;
        float floatLength = (float)doubleLength;
        double doubleLength2=floatLength*1024;
        Integer imageLength = (new Double(doubleLength2)).intValue();
        converResult.setPageSize(pageSize);
        converResult.setImageLength(imageLength);
        //LOGGER.info("------------- pdfToImage success -------------------");
        //LOGGER.info("文件{} pdf转image时间:{}ms", pdfFile.getName(), System.currentTimeMillis() - startTime);
        return converResult;

    }

    /**
     * 一张一张转图片测试
     *
     * @param pdfFile
     * @param imageSaveDir
     * @throws Exception
     */
    public static void convertOne2OneTest(File pdfFile, File imageSaveDir) throws Exception {
        if (pdfFile == null || pdfFile.length() <= 0) {
            throw new Exception("pdfFile is null or size == 0 ");
        }
        if (!imageSaveDir.exists()) {
            imageSaveDir.mkdirs();
        }
        PDDocument doc = PDDocument.load(pdfFile);
        PDFRenderer renderer = new PDFRenderer(doc);
        int pageSize = doc.getNumberOfPages();
        String imageName = null;
        BufferedImage image = null;
        for (int i = 0; i < pageSize; i++) {
            imageName = i + PNG_SUFFIX;
            try {
                image = renderer.renderImageWithDPI(i, 100);
                ImageIO.write(image, PNG, new File(imageSaveDir, imageName));    //正常生成图片
            } catch (IOException e) {
                LOGGER.info("转换图片{}失败:", imageName, e);
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private static class PdfToImageTask extends RecursiveAction {
        private static final long serialVersionUID = -3611254198265061729L;
        private static AtomicInteger atomicInteger = new AtomicInteger(0);
        private static final int threshold = 20;
        private PDFRenderer renderer;
        private File imageSaveDir;
        private int start;
        private int end;

        PdfToImageTask(PDFRenderer renderer, File imageSaveDir, int start, int end) {
            this.renderer = renderer;
            this.imageSaveDir = imageSaveDir;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            //如果任务足够小就计算任务
            boolean canCompute = (end - start) < threshold;
            if (canCompute) {
                BufferedImage image;
                String imageName;
                for (int i = start; i < end; i++) {
                    imageName = i + PNG_SUFFIX;
                    try {

                        image = renderer.renderImageWithDPI(i, 100);
                        ImageIO.write(image, PNG, new File(imageSaveDir, imageName));    //正常生成图片
                        atomicInteger.incrementAndGet();
//                        int i1 = 1 / 0;
                    } catch (Exception e) {
                        LOGGER.info("转换图片{}失败:", imageName, e);
                        LOGGER.error(e.getMessage());
                        throw new RuntimeException(e);
//                        return;
                    }
                }
            } else {
                // 如果任务大于阈值，就分裂成两个子任务计算
                int middle = (start + end) / 2;
                PdfToImageTask leftTask = new PdfToImageTask(renderer, imageSaveDir, start, middle);
                PdfToImageTask rightTask = new PdfToImageTask(renderer, imageSaveDir, middle, end);

                // 执行子任务
                leftTask.fork();
                rightTask.fork();

                //等待任务执行结束合并其结果
                leftTask.join();
                rightTask.join();
            }
        }
    }

    public static class ConverResult {
        private int imageWidth;
        private int imageHeight;
        private List<String> imageNameList;
        private int pageSize;
        private Integer imageLength;

        public Integer getImageLength() {
            return imageLength;
        }

        public void setImageLength(Integer imageLength) {
            this.imageLength = imageLength;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public List<String> getImageNameList() {
            return imageNameList;
        }

        public void setImageNameList(List<String> imageNameList) {
            this.imageNameList = imageNameList;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }
}


