package tk.crypfolio.common;

public abstract class SettingsDB {

    public enum Type {JPA, NoSQL}

    public static final Type APP_DB_TYPE = Type.JPA;

}