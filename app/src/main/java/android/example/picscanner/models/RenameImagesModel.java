package android.example.picscanner.models;

import android.net.Uri;

public class RenameImagesModel {

    String imageBarcode;
    String imageNo;
    String imageUrl;

    String imageCount;
    Uri imagePath;

    public RenameImagesModel(String barcode, String imageNo, String imageUrl) {
        this.imageBarcode = barcode;
        this.imageNo = imageNo;
        this.imageUrl = imageUrl;
    }

    public RenameImagesModel(String imageCount, Uri imagePath) {
        this.imageCount = imageCount;
        this.imagePath = imagePath;
    }

    public String getImageCount() {
        return imageCount;
    }

    public void setImageCount(String imageCount) {
        this.imageCount = imageCount;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public void setImagePath(Uri imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageBarcode() {
        return imageBarcode ;
    }

    public void setImageBarcode(String barcode) {
        this.imageBarcode  = barcode;
    }

    public String getImageNo() {
        return imageNo;
    }

    public void setImageNo(String imageNo) {
        this.imageNo = imageNo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
