package tk.crypfolio.common;

public enum PositionType {

    BUY("BUY"),
    SELL("SELL");

    private String type;

    PositionType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }

}
