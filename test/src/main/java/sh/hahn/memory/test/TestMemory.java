package sh.hahn.memory.test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class TestMemory {

	private static final int BUFFER_SIZE = (Short.MAX_VALUE - 7) * 10;

	private ByteBuffer byteBuffer;
	private long bufferAddress;

	private static Unsafe unsafe;
//	private static int byteOffset;

	static {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
//			byteOffset = unsafe.arrayBaseOffset(byte[].class);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public void setUp() {
		this.byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.bufferAddress = unsafe.allocateMemory(BUFFER_SIZE);
	}

	public void tearDown() {
		unsafe.freeMemory(this.bufferAddress);
	}

	public void testByteBuffer() {
		long l;
		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			byteBuffer.putLong(i, Long.MAX_VALUE);
		}

		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			l = byteBuffer.getLong(i) / 10;
//			blackHole.consume(byteBuffer.getLong(i));
		}
	}

	public void tesUnsafe() {
		long l;
		
		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			unsafe.putLong(bufferAddress + i, Long.MAX_VALUE);
		}

		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			l = unsafe.getLong(bufferAddress + i) / 10;
//			blackHole.consume(unsafe.getLong(bufferAddress + i));
		}
	}
	
	public void executeExtern(long bufferAddress, ByteBuffer byteBuffer)  {
		this.bufferAddress = bufferAddress;
		this.byteBuffer = byteBuffer;
		
		long startTime;
		
		System.out.println("Executing");
		
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			tesUnsafe();
		}
		System.out.println(String.format("Unsafe test: %d",System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			testByteBuffer();
		}
		System.out.println(String.format("ByteBuffer test: %d", System.currentTimeMillis() - startTime));
		

	}

	public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);
		long startTime;
		TestMemory test = new TestMemory();
		test.setUp();

		
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			test.tesUnsafe();
		}
		System.out.println(String.format("Unsafe test: %d",System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			test.testByteBuffer();
		}
		System.out.println(String.format("ByteBuffer test: %d", System.currentTimeMillis() - startTime));
		
		test.tearDown();

	}

}
