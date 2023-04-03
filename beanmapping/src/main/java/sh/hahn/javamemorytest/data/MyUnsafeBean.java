package sh.hahn.javamemorytest.data;

import sun.misc.Unsafe;

import java.io.UnsupportedEncodingException;

public class MyUnsafeBean implements MyRootBean {

    private final Unsafe unsafe;
    private final long address;
    private final int bytArrayOffset;

    public MyUnsafeBean(Unsafe unsafe, long address, int byteArrayOffset) {
        this.unsafe = unsafe;
        this.address = address;
        this.bytArrayOffset = byteArrayOffset;
    }

    @Override
    public int getIntField() {
        return unsafe.getInt(address + INT_FIELD_OFFSET);
    }

    @Override
    public void setIntField(int value) {
        unsafe.putInt(address + INT_FIELD_OFFSET, value);
    }

    @Override
    public long getLongField() {
        return unsafe.getLong(address + LONG_FIELD_OFFSET);
    }

    @Override
    public void setLongField(long value) {
        unsafe.putLong(address + LONG_FIELD_OFFSET, value);
    }

    @Override
    public float getFloatField() {
        return unsafe.getFloat(address + FLOAT_FIELD_OFFSET);
    }

    @Override
    public void setFloatField(float value) {
        unsafe.putFloat(address + FLOAT_FIELD_OFFSET, value);
    }

    @Override
    public String getStringField() {
        byte[] buffer = new byte[10];
        unsafe.copyMemory(null, address + STRING_FIELD_OFFSET, buffer,bytArrayOffset, 10);
        try {
            return new String(buffer, "Cp273");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStringField(String value) {
        unsafe.copyMemory(value.getBytes(), bytArrayOffset, null, address + STRING_FIELD_OFFSET, 10);
    }

    @Override
    public byte[] getByteArrayField() {
        byte[] buffer = new byte[10];
        unsafe.copyMemory(null, address + BYTE_ARRAY_FIELD_OFFSET, buffer,bytArrayOffset, 10);
        return buffer;
    }

    @Override
    public void setByteArrayField(byte[] value) {
        unsafe.copyMemory(value, bytArrayOffset, null, address + BYTE_ARRAY_FIELD_OFFSET, 10);
    }
}
