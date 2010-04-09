package de.fhg.fokus.net.ipfix.record;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixRecord;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateForDataReader;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIePacketDeltaCount;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeSourceIPv4Address;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixRecordSourceIpv4PacketDeltaCount implements IpfixRecord {
	private final Inet4Address sourceIpv4Address;
	private final BigInteger packetDeltaCount;

	public IpfixRecordSourceIpv4PacketDeltaCount(
			Inet4Address sourceIpv4Address, BigInteger packetDeltaCount) {
		super();
		this.sourceIpv4Address = sourceIpv4Address;
		this.packetDeltaCount = packetDeltaCount;
	}

	public Inet4Address getSourceIpv4Address() {
		return sourceIpv4Address;
	}

	public BigInteger getPacketDeltaCount() {
		return packetDeltaCount;
	}

	@Override
	public String toString() {
		return String.format("{sourceIpv4Address:%s, packetDeltaCount:%d}",
				sourceIpv4Address, packetDeltaCount);
	}

	/**
	 * IPFIX data record reader for
	 * {@link IpfixRecordSourceIpv4PacketDeltaCount }
	 */
	private static final IpfixDataRecordReader reader = new IpfixDataRecordReader() {
		private final IpfixIeSourceIPv4Address sourceIpv4Address = new IpfixIeSourceIPv4Address();
		private final IpfixIePacketDeltaCount packetDeltaCount = new IpfixIePacketDeltaCount(4);
		private final IpfixTemplateForDataReader template = new IpfixTemplateForDataReader(
				sourceIpv4Address, packetDeltaCount);

		@Override
		public IpfixRecordSourceIpv4PacketDeltaCount getRecord(
				ByteBuffer setBuffer) {

			if (!setBuffer.hasRemaining()) {
				return null;
			}
			return new IpfixRecordSourceIpv4PacketDeltaCount(
					sourceIpv4Address.getAddress(setBuffer), 
					packetDeltaCount.getBigInteger(setBuffer));

		}
		public String toString() {
			return "DRR(sourceIPv4_packetDeltaCount)";
		}
		@Override
		public IpfixTemplateForDataReader getTemplate() {
			return template;
		}
	};
	public final static IpfixDataRecordReader getReader(){
		return reader;
	}

}
