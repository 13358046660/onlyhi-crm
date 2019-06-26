package cn.onlyhi.file.request;

import cn.onlyhi.common.request.BaseRequest;

import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/4/23.
 */
public class ConverRequest extends BaseRequest {
    private int converStatus;
    private String originalUrl;
    private String pdfUrl;  //若文件为图片，则为图片url
    private int imageWidth;
    private int imageHeight;
    private List<Image> imageList;

    public static class Image {
        private String url;
        private String name;
        private String md5; //图片内容MD5加密后的值

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "url='" + url + '\'' +
                    ", name='" + name + '\'' +
                    ", md5='" + md5 + '\'' +
                    '}';
        }
    }

    public int getConverStatus() {
        return converStatus;
    }

    public void setConverStatus(int converStatus) {
        this.converStatus = converStatus;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
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

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    @Override
    public String toString() {
        return "ConverRequest{" +
                "converStatus=" + converStatus +
                ", originalUrl='" + originalUrl + '\'' +
                ", pdfUrl='" + pdfUrl + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", imageList=" + imageList +
                "} " + super.toString();
    }
}
