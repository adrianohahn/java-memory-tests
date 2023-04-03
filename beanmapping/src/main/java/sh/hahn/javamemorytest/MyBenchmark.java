/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package sh.hahn.javamemorytest;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import sh.hahn.javamemorytest.data.MyByteBufferBean;
import sh.hahn.javamemorytest.data.MyByteJzosBean;
import sh.hahn.javamemorytest.data.MyRootBean;
import sh.hahn.javamemorytest.data.MyUnsafeBean;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Random;

public class MyBenchmark {

    private static Random random = new Random();

    @State(Scope.Thread)
    public static class UnsafeState {

        private Unsafe unsafe;
        private long address;
        private int byteArrayOffset;
        public MyUnsafeBean bean;

        @Setup
        public void setUp() {
            Field f;
            try {
                f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafe = (Unsafe) f.get(null);
                byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            address = unsafe.allocateMemory(MyRootBean.BEAN_SIZE);
            bean = new MyUnsafeBean(unsafe, address, byteArrayOffset);
        }

        @TearDown
        public void tearDown() {
            unsafe.freeMemory(address);
        }

    }

    @State(Scope.Thread)
    public static class ByteBufferUnsafeState {

        private Unsafe unsafe;
        private long address;
        private int byteArrayOffset;
        public MyByteBufferBean bean;
        private ByteBuffer byteBuffer;

        @Setup
        public void setUp() {
            Field f;
            try {
                f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafe = (Unsafe) f.get(null);
                byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            address = unsafe.allocateMemory(MyRootBean.BEAN_SIZE);
            byte[] buffer = new byte[40];
            unsafe.copyMemory(null, address, buffer, byteArrayOffset, 40);
            byteBuffer = ByteBuffer.wrap(buffer);
            bean = new MyByteBufferBean(byteBuffer);
        }

        @TearDown
        public void tearDown() {
            unsafe.freeMemory(address);
        }

    }

    @State(Scope.Thread)
    public static class ByteBufferState {

        private ByteBuffer byteBuffer;
        private MyByteBufferBean bean;

        @Setup
        public void setUp() {
            this.byteBuffer = ByteBuffer.allocateDirect(MyRootBean.BEAN_SIZE);
            this.bean = new MyByteBufferBean(this.byteBuffer);
        }

        @TearDown
        public void tearDown() {

        }

    }

    @State(Scope.Thread)
    public static class JzosState {

        private byte[] data;
        private MyByteJzosBean bean;

        @Setup
        public void setUp() {
            this.data = new byte[MyRootBean.BEAN_SIZE];
            this.bean = new MyByteJzosBean(data);
        }

        @TearDown
        public void tearDown() {

        }

    }


    @Benchmark
    public void testByteBuffer(ByteBufferState state, Blackhole blackhole) {
        MyByteBufferBean bean = state.bean;
        executeCommonTest(blackhole, bean);
    }

    @Benchmark
    public void testByteBufferNewBean(ByteBufferState state, Blackhole blackhole) {
        MyByteBufferBean bean = new MyByteBufferBean(state.byteBuffer);
        executeCommonTest(blackhole, bean);
    }

    @Benchmark
    public void testByteBufferUnsafe(ByteBufferUnsafeState state, Blackhole blackhole) {
        MyByteBufferBean bean = state.bean;
        executeCommonTest(blackhole, bean);
    }

    @Benchmark
    public void testByteBufferUnsafeNewBean(ByteBufferUnsafeState state, Blackhole blackhole) {
        MyByteBufferBean bean = new MyByteBufferBean(state.byteBuffer);
        executeCommonTest(blackhole, bean);
    }

    @Benchmark
    public void testUnsafe(UnsafeState state, Blackhole blackhole) {
        MyRootBean bean = state.bean;
        executeCommonTest(blackhole, bean);
    }

    @Benchmark
    public void testUnsafeNewBean(UnsafeState state, Blackhole blackhole) {
        MyRootBean bean = new MyUnsafeBean(state.unsafe, state.address, state.byteArrayOffset);
        executeCommonTest(blackhole, bean);
    }

    @Benchmark
    public void testJzos(JzosState state, Blackhole blackhole) {
        MyRootBean bean = state.bean;
        executeCommonTest(blackhole, bean);
    }

    @Benchmark
    public void testJzosNewBean(JzosState state, Blackhole blackhole) {
        MyRootBean bean = new MyByteJzosBean(state.data);
        executeCommonTest(blackhole, bean);
    }

    private static void executeCommonTest(Blackhole blackhole, MyRootBean bean) {
        byte[] buffer = new byte[10];
//        random.nextBytes(buffer);
//        bean.setFloatField(random.nextFloat());
//        bean.setIntField(random.nextInt());
//        bean.setLongField(random.nextLong());
//        bean.setByteArrayField(buffer);
//        bean.setStringField(new String(buffer));
        bean.setIntField(10);
        bean.setLongField(5465465);
        bean.setFloatField(4.45121f);
        bean.setStringField("blablablas");
        bean.setByteArrayField("blablablas".getBytes());
        blackhole.consume(bean.getIntField());
        blackhole.consume(bean.getLongField());
        blackhole.consume(bean.getFloatField());
        blackhole.consume(bean.getByteArrayField());
        blackhole.consume(bean.getStringField());
    }

//    @Benchmark
//    public void testMethod() {
//        // This is a demo/sample template for building your JMH benchmarks. Edit as needed.
//        // Put your benchmark code here.
//    }

}
