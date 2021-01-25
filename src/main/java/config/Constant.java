package config;

public class Constant {

    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/contract?characterEncoding=utf8&serverTimezone=Asia/Shanghai";
    public static final String DATABASE_USER = "";
    public static final String DATABASE_PASSWORD = "";

    public static final String QUERY_ALL_SQL = "select * from contract_20201116";

    public static final int NUM_OF_SHARDS = 3;
    public static final int NUM_OF_REPLICAS = 1;
    public static final String TXT_CLEAR_PATH = "F:\\task\\contract\\txt_clear";
}