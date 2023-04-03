package sh.hahn.javamemorytest.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class MyByteBufferBean implements MyRootBean {

    private final ByteBuffer byteBuffer;

    public MyByteBufferBean(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public int getIntField() {
        return byteBuffer.getInt(INT_FIELD_OFFSET);
    }

    @Override
    public void setIntField(int value) {
        byteBuffer.putInt(INT_FIELD_OFFSET, value);
    }

    @Override
    public long getLongField() {
        return byteBuffer.getLong(LONG_FIELD_OFFSET);
    }

    @Override
    public void setLongField(long value) {
        byteBuffer.putLong(LONG_FIELD_OFFSET, value);
    }


    @Override
    public String getStringField() {
        byte[] buffer = new byte[10];
        byteBuffer.position(STRING_FIELD_OFFSET);
        byteBuffer.get(buffer, 0, 10);
        try {
            return new String(buffer, "Cp273");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStringField(String value) {
        byteBuffer.position(STRING_FIELD_OFFSET);
        byteBuffer.put(value.getBytes(), 0, 10);
    }

    @Override
    public float getFloatField() {
        return byteBuffer.getLong(FLOAT_FIELD_OFFSET);
    }

    @Override
    public void setFloatField(float value) {
        byteBuffer.putFloat(FLOAT_FIELD_OFFSET, value);
    }

    @Override
    public byte[] getByteArrayField() {
        byte[] buffer = new byte[10];
        byteBuffer.position(BYTE_ARRAY_FIELD_OFFSET);
        byteBuffer.get(buffer, 0, 10);
        return buffer;
    }

    @Override
    public void setByteArrayField(byte[] value) {
        byteBuffer.position(BYTE_ARRAY_FIELD_OFFSET);
        byteBuffer.put(value);
    }
}
