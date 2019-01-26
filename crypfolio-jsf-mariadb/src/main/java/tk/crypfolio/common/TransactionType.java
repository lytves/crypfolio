package tk.crypfolio.common;

public enum TransactionType {

    BUY("BUY"),
    SELL("SELL");

    private String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
