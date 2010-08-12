package eu.fp7_prism.backend.ipfix.model;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateForDataReader;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeDestinationIPv4Address;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeObservationTimeMilliseconds;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;
import eu.fp7_prism.backend.ipfix.model.ie.IpfixIeRoundTripTime;

/**
 * IPFIX ASDF Address Delay Record. Note that the order of the information
 * elements is important.
 * 
 * <pre>
 * destinationIPv4Address       {id:12,  len:4, en:0} 
 * observationTimeMilliseconds  {id:323, len:8, en:0} 
 * roundTripTime                {id:199, len:4, en:12325}
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public final class IpfixRecordAsdfRtt  {
	private final Inet4Address destinationIPv4Address;
	private final BigInteger observationTimeMilliseconds;
	private final BigInteger roundTripTime;
	// a reference of the underlying byte buffer (mostly useful for debugging)
	private ByteBuffer byteBuffer;

	public IpfixRecordAsdfRtt(Inet4Address destinationIPv4Address,
			BigInteger observationTimeMilliseconds, BigInteger roundTripTime) {
		this.destinationIPv4Address = destinationIPv4Address;
		this.observationTimeMilliseconds = observationTimeMilliseconds;
		this.roundTripTime = roundTripTime;
	}
	

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}


	public void setByteBuffer(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}


	public Inet4Address getDestinationIPv4Address() {
		return destinationIPv4Address;
	}


	public BigInteger getObservationTimeMilliseconds() {
		return observationTimeMilliseconds;
	}


	public BigInteger getRoundTripTime() {
		return roundTripTime;
	}


	public static IpfixDataRecordReader getReader() {
		return reader;
	}


	@Override
	public String toString() {
		return String
				.format(
						"{destinationIPv4Address:%s, observationTimeMilliseconds:%s, roundTripTime:%s}",
						destinationIPv4Address, observationTimeMilliseconds,
						roundTripTime);
	}

	private static final IpfixDataRecordReader reader = new IpfixDataRecordReader() {
		private final IpfixIeDestinationIPv4Address f0 = new IpfixIeDestinationIPv4Address(
				4);
		private final IpfixIeObservationTimeMilliseconds f1 = new IpfixIeObservationTimeMilliseconds(
				8);
		private final IpfixIeRoundTripTime f2 = new IpfixIeRoundTripTime(4);
		// This reader will only match information elements in this order.
		private final IpfixTemplateForDataReader template = new IpfixTemplateForDataReader(
				f0, f1, f2);
		private final int recordSize = template.getRecordSize();

		@Override
		public IpfixTemplateForDataReader getTemplate() {
			return template;
		}

		@Override
		public Object getRecord(IpfixMessage msg, ByteBuffer setBuffer) {
			ByteBuffer bb = ByteBufferUtil.slice(setBuffer, recordSize);
			IpfixRecordAsdfRtt rtt = new IpfixRecordAsdfRtt(f0.getAddress(setBuffer), f1
					.getBigInteger(setBuffer), f2.getBigInteger(setBuffer));
			rtt.setByteBuffer(bb);
			return rtt;
		}
	};
}
