package model;

public class Request {
    public RequestCode code;
    public String bookName;

    public Request(RequestCode code, String bookName) {
        this.code = code;
        this.bookName = bookName;
    }

    public RequestCode getCode() {
        return code;
    }

    public String getBookName() {
        return bookName;
    }
}
