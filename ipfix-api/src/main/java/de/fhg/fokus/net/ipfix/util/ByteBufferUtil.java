package de.fhg.fokus.net.ipfix.util;

import java.nio.ByteBuffer;

/**
 * Utilities to operate on {@link ByteBuffer}s. 
 * <h2> Unsigned</h2>
 * Utility class to get and put unsigned values to a ByteBuffer object. All
 * methods here are static and take a ByteBuffer object argument. Since java
 * does not provide unsigned primitive types, each unsigned value read from the
 * buffer is promoted up to the next bigger primitive data type.
 * getUnsignedByte() returns a short, getUnsignedShort() returns an int and
 * getUnsignedInt() returns a long. There is no getUnsignedLong() since there is
 * no primitive type to hold the value returned. If needed, methods returning
 * BigInteger could be implemented. Likewise, the put methods take a value
 * larger than the type they will be assigning. putUnsignedByte takes a short
 * argument, etc.
 * <p><b>Note:</b> Class name, argument names, and package were renamed to match 
 * this project. </p>
 * 
 * @author Ron Hitchens (ron@ronsoft.com)
 * @version $Id: Unsigned.java,v 1.1 2002/02/12 22:06:44 ron Exp $
 */
public class ByteBufferUtil {
	// hide constructor
	private ByteBufferUtil(){
		
	}
	public static short getUnsignedByte(ByteBuffer bbuf) {
		return ((short) (bbuf.get() & 0xff));
	}

	public static void putUnsignedByte(ByteBuffer bbuf, int value) {
		bbuf.put((byte) (value & 0xff));
	}

	public static short getUnsignedByte(ByteBuffer bbuf, int position) {
		return ((short) (bbuf.get(position) & (short) 0xff));
	}

	public static void putUnsignedByte(ByteBuffer bbuf, int position, int value) {
		bbuf.put(position, (byte) (value & 0xff));
	}

	// ---------------------------------------------------------------

	public static int getUnsignedShort(ByteBuffer bbuf) {
		return (bbuf.getShort() & 0xffff);
	}

	public static void putUnsignedShort(ByteBuffer bbuf, int value) {
		bbuf.putShort((short) (value & 0xffff));
	}

	public static int getUnsignedShort(ByteBuffer bbuf, int position) {
		return (bbuf.getShort(position) & 0xffff);
	}

	public static void putUnsignedShort(ByteBuffer bbuf, int position, int value) {
		bbuf.putShort(position, (short) (value & 0xffff));
	}

	// ---------------------------------------------------------------

	public static long getUnsignedInt(ByteBuffer bbuf) {
		return ((long) bbuf.getInt() & 0xffffffffL);
	}

	public static void putUnsignedInt(ByteBuffer bbuf, long value) {
		bbuf.putInt((int) (value & 0xffffffffL));
	}

	public static long getUnsignedInt(ByteBuffer bbuf, int position) {
		return ((long) bbuf.getInt(position) & 0xffffffffL);
	}

	public static void putUnsignedInt(ByteBuffer bbuf, int position, long value) {
		bbuf.putInt(position, (int) (value & 0xffffffffL));
	}
	/**
	 * Slice buffer and skip sliced region. It is useful for wrapping objects 
	 * around chunks of bytes. No checks are done so it can throw runtime 
	 * exceptions. 
	 * 
	 * @param byteBuffer
	 * @param length
	 */
	public static ByteBuffer sliceAndSkip(ByteBuffer byteBuffer, int length ){
		final int endpos = byteBuffer.position() + length;
		final int limit = byteBuffer.limit();
		byteBuffer.limit(endpos);
		final ByteBuffer slice = byteBuffer.slice().order(byteBuffer.order());
		// skipping bytes
		byteBuffer.limit(limit).position(endpos);
		return slice;
	}
	
	/**
	 * Slice buffer using current position and length. No checks are done
	 * so it can throw ByteBuffer runtime exceptions.
	 * 
	 * @param byteBuffer
	 * @param length
	 */
	public static ByteBuffer slice(ByteBuffer byteBuffer, int length ){
		final int limit = byteBuffer.limit();
		byteBuffer.limit(byteBuffer.position() + length);
		final ByteBuffer slice = byteBuffer.slice().order(byteBuffer.order());
		// restoring limit
		byteBuffer.limit(limit);
		return slice;
	}
	
	
	
	
}