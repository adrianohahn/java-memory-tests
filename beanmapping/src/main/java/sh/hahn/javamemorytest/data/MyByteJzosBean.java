package sh.hahn.javamemorytest.data;

import com.ibm.jzos.fields.*;

public class MyByteJzosBean implements MyRootBean {

    private static final BinaryAsIntField intField;

    private static final BinaryAsLongField longField;

    private static final IbmFloatField floatField;

    private static final StringField stringField;

    private static final ByteArrayField byteArrayField;

    static {
        AssemblerDatatypeFactory factory = new AssemblerDatatypeFactory();
        intField = factory.getBinaryAsIntField(4, true);
        longField = factory.getBinaryAsLongField(8, true);
        floatField = factory.getIbmFloatField();
        stringField = factory.getStringField(10);
        byteArrayField = factory.getByteArrayField(10);
    }

    private byte[] data;

    public MyByteJzosBean(byte[] data) {
        this.data = data;
    }

    @Override
    public int getIntField() {
        return intField.getInt(this.data);
    }

    @Override
    public void setIntField(int value) {
        intField.putInt(value, this.data);
    }

    @Override
    public long getLongField() {
        return longField.getLong(this.data);
    }

    @Override
    public void setLongField(long value) {
        longField.putLong(value, this.data);
    }

    @Override
    public float getFloatField() {
        return floatField.getFloat(this.data);
    }

    @Override
    public void setFloatField(float value) {
        floatField.putFloat(value, this.data);
    }

    @Override
    public String getStringField() {
        return stringField.getString(this.data);
    }

    @Override
    public void setStringField(String value) {
        stringField.putString(value, this.data);
    }

    @Override
    public byte[] getByteArrayField() {
        return byteArrayField.getByteArray(this.data);
    }

    @Override
    public void setByteArrayField(byte[] value) {
        byteArrayField.putByteArray(value, this.data);
    }
}
