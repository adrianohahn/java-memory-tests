package sh.hahn.javamemorytest.data;

public interface MyRootBean {

    public static int INT_FIELD_OFFSET = 0;
    public static int LONG_FIELD_OFFSET = 4;
    public static int FLOAT_FIELD_OFFSET = 12;
    public static int STRING_FIELD_OFFSET = 20;
    public static int BYTE_ARRAY_FIELD_OFFSET = 30;
    public static int BEAN_SIZE = 40;


    int getIntField();
    void setIntField(int value);
    long getLongField();
    void setLongField(long value);
    float getFloatField();
    void setFloatField(float value);
    String getStringField();
    void setStringField(String value);
    byte[] getByteArrayField();
    void setByteArrayField(byte[] value);
}
