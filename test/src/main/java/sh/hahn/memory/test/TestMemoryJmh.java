package sh.hahn.memory.test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
@State(Scope.Benchmark)
public class TestMemoryJmh {

	private static final int BUFFER_SIZE = (Short.MAX_VALUE - 7) / 4;

	private ByteBuffer byteBuffer;
	private long bufferAddress;
	
//	private byte[] randomBuffer = new byte[BUFFER_SIZE];
	private static Unsafe unsafe;
	private static long byteOffset;

	static {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
			byteOffset = unsafe.arrayBaseOffset(byte[].class);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	@Setup(Level.Trial)
	public void setUp() {
		this.byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.bufferAddress = unsafe.allocateMemory(BUFFER_SIZE);
//		Random random = new Random(Instant.now().toEpochMilli());
//		random.nextBytes(randomBuffer);
	}

	@TearDown(Level.Trial)
	public void tearDown() {
		unsafe.freeMemory(this.bufferAddress);
	}

	@Fork(value = 1, warmups = 1, jvmArgs = { "-Xms512m", "-Xmx1024m" })
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void testByteBuffer(Blackhole blackHole) {
		long l;
		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			byteBuffer.putLong(i, Long.MAX_VALUE);
		}

		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			l = byteBuffer.getLong(i);
			blackHole.consume(l);
		}
	}

	@Fork(value = 1, warmups = 1, jvmArgs = { "-Xms512m", "-Xmx1024m" })
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void tesUnsafe(Blackhole blackHole) {
		long l;
		
		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			unsafe.putLong(bufferAddress + i, Long.MAX_VALUE);
		}

		for (int i = 0; i < BUFFER_SIZE; i += Long.BYTES) {
			l = unsafe.getLong(bufferAddress + i);
			blackHole.consume(l);
		}
	}
	
	@Fork(value = 1, warmups = 1, jvmArgs = { "-Xms512m", "-Xmx1024m" })
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void tesUnsafeCopyMemoryLong(Blackhole blackHole) {
		byte[] tempBuffer = new byte[BUFFER_SIZE];
		
		for (int i = 0; i <  tempBuffer.length; i+= Long.BYTES) {
			unsafe.putLong(tempBuffer, byteOffset + i, unsafe.getLong(bufferAddress + i));
		}

		for (int i = 0; i < tempBuffer.length; i++) {
			blackHole.consume(tempBuffer[i]);
		}
	}
	
	@Fork(value = 1, warmups = 1, jvmArgs = { "-Xms512m", "-Xmx1024m" })
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void tesUnsafeCopyMemoryAll(Blackhole blackHole) {
		byte[] tempBuffer = new byte[BUFFER_SIZE];
		
		unsafe.copyMemory(null, bufferAddress, tempBuffer, byteOffset, BUFFER_SIZE);

		for (int i = 0; i < tempBuffer.length; i++) {
			blackHole.consume(tempBuffer[i]);
		}
	}
	
	public void executeExtern(long bufferAddress, ByteBuffer byteBuffer)  {
		this.bufferAddress = bufferAddress;
		this.byteBuffer = byteBuffer;
		
		long startTime;
		
		System.out.println("Executing");
		
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			tesUnsafe(null);
		}
		System.out.println(String.format("Unsafe test: %d",System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			testByteBuffer(null);
		}
		System.out.println(String.format("ByteBuffer test: %d", System.currentTimeMillis() - startTime));
		

	}

	public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);
		long startTime;
		TestMemoryJmh test = new TestMemoryJmh();
		test.setUp();

		
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			test.tesUnsafe(null);
		}
		System.out.println(String.format("Unsafe test: %d",System.currentTimeMillis() - startTime));
			
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			test.testByteBuffer(null);
		}
		System.out.println(String.format("ByteBuffer test: %d", System.currentTimeMillis() - startTime));
		
		test.tearDown();

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
