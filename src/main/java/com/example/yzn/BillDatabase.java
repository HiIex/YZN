package com.example.yzn;

public class BillDatabase {
    private String billID;
    private String issuerID;
    private String productName;
    private String price;
    private String imageUrl;
    private int type;
    private String middleName;
    private int currency;
    private String detail;
    private int isTaken;

    public BillDatabase(){}

    public BillDatabase(String billID, String issuerID, String productName, String price, int currency, int type, String middleName, String imageUrl, String detail, int isTaken){
        this.billID=billID;
        this.issuerID = issuerID;
        this.productName = productName;
        this.price=price;
        this.type=type;
        this.middleName = middleName;
        this.imageUrl = imageUrl;
        this.currency=currency;
        this.detail=detail;
        this.isTaken=isTaken;
    }


    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getIssuerID() {
        return issuerID;
    }

    public void setIssuerID(String issuerID) {
        this.issuerID = issuerID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int isTaken() {
        return isTaken;
    }

    public void setTaken(int taken) {
        isTaken = taken;
    }
}
