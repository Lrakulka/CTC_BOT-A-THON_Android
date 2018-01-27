package com.google.android.gms.samples.vision.barcodereader.dto;

public class DataModel {
 
    private String productName;
    private String productBarcode;
    private Integer productQuantity;
    private String productImageURI;
    private String productInfo;

    public DataModel(String productName, String productBarcode, Integer productQuantity,
                     String productImageURI, String productInfo) {
        this.productName = productName;
        this.productBarcode = productBarcode;
        this.productQuantity = productQuantity;
        this.productImageURI = productImageURI;
        this.productInfo = productInfo;

    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductImageURI() {
        return productImageURI;
    }

    public void setProductImageURI(String productImageURI) {
        this.productImageURI = productImageURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataModel dataModel = (DataModel) o;

        if (productName != null ? !productName.equals(dataModel.productName) : dataModel.productName != null)
            return false;
        if (productBarcode != null ? !productBarcode.equals(dataModel.productBarcode) : dataModel.productBarcode != null)
            return false;
        return productImageURI != null ? productImageURI.equals(dataModel.productImageURI) : dataModel.productImageURI == null;
    }

    @Override
    public int hashCode() {
        int result = productName != null ? productName.hashCode() : 0;
        result = 31 * result + (productBarcode != null ? productBarcode.hashCode() : 0);
        result = 31 * result + (productImageURI != null ? productImageURI.hashCode() : 0);
        return result;
    }
}