package model;

public class FindRequest {
    private String bookName;
    private Double priceFromFirstDB;
    private Double priceFromSecondDB;

    public FindRequest(String bookName) {
        this.bookName = bookName;
        this.priceFromFirstDB = null;
        this.priceFromSecondDB = null;
    }

    public void setPriceFromFirstDB(double priceFromFirstDB) {
        this.priceFromFirstDB = priceFromFirstDB;
    }

    public void setPriceFromSecondDB(double priceFromSecondDB) {
        this.priceFromSecondDB = priceFromSecondDB;
    }
}
