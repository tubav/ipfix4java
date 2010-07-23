package de.fhg.fokus.net.ipfix.record;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixRecord;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateForDataReader;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeDestinationIPv4Address;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeDestinationTransportPort;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeFlowEndMilliseconds;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeFlowStartMilliseconds;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeIpClassOfService;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeOctetDeltaCount;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIePacketDeltaCount;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeProtocolIdentifier;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeSourceIPv4Address;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeSourceTransportPort;

/**
 * Record reader for LibIPFIX probe IPv4 flows
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public final class IpfixRecordFlow4 implements IpfixRecord {

	private final BigInteger flowStartMilliseconds;
	private final BigInteger flowEndMilliseconds;
	private final Inet4Address sourceIPv4Address;
	private final Inet4Address destinationIPv4Address;
	private final int sourceTransportPort;
	private final int destinationTransportPort;
	private final short protocolIdentifier;
	private final short ipClassOfService;
	private final long packetDeltaCount;
	private final long octetDeltaCount;

	public IpfixRecordFlow4(BigInteger flowStartMilliseconds,
			BigInteger flowEndMilliseconds, Inet4Address sourceIPv4Address,
			Inet4Address destinationIPv4Address, int sourceTransportPort,
			int destinationTransportPort, short protocolIdentifier,
			short ipClassOfService, long packetDeltaCount, long octetDeltaCount) {

		this.flowStartMilliseconds = flowStartMilliseconds;
		this.flowEndMilliseconds = flowEndMilliseconds;
		this.sourceIPv4Address = sourceIPv4Address;
		this.destinationIPv4Address = destinationIPv4Address;
		this.sourceTransportPort = sourceTransportPort;
		this.destinationTransportPort = destinationTransportPort;
		this.protocolIdentifier = protocolIdentifier;
		this.ipClassOfService = ipClassOfService;
		this.packetDeltaCount = packetDeltaCount;
		this.octetDeltaCount = octetDeltaCount;
	}

	@Override
	public String toString() {

		return String
				.format(
						" {flowStartMilliseconds:%s, flowEndMilliseconds:%s, "
								+ "sourceIPv4Address:%s, destinationIPv4Address:%s, "
								+ "sourceTransportPort:%d, destinationTransportPort:%d, "
								+ "protocolIdentifier:%d, ipClassOfService:%d, "
								+ "packetDeltaCount:%d, octetDeltaCount:%d}",
						flowStartMilliseconds, flowEndMilliseconds,
						sourceIPv4Address, destinationIPv4Address,
						sourceTransportPort, destinationTransportPort,
						protocolIdentifier, ipClassOfService,
						packetDeltaCount, octetDeltaCount);
	}

	private static final IpfixDataRecordReader reader = new IpfixDataRecordReader() {
		private final IpfixIeFlowStartMilliseconds f0 = new IpfixIeFlowStartMilliseconds(
				8);
		private final IpfixIeFlowEndMilliseconds f1 = new IpfixIeFlowEndMilliseconds(
				8);
		private final IpfixIeSourceIPv4Address f2 = new IpfixIeSourceIPv4Address(
				4);
		private final IpfixIeDestinationIPv4Address f3 = new IpfixIeDestinationIPv4Address(
				4);
		private final IpfixIeSourceTransportPort f4 = new IpfixIeSourceTransportPort(
				2);
		private final IpfixIeDestinationTransportPort f5 = new IpfixIeDestinationTransportPort(
				2);
		private final IpfixIeProtocolIdentifier f6 = new IpfixIeProtocolIdentifier();
		private final IpfixIeIpClassOfService f7 = new IpfixIeIpClassOfService();
		private final IpfixIePacketDeltaCount f8 = new IpfixIePacketDeltaCount(
				4);
		private final IpfixIeOctetDeltaCount f9 = new IpfixIeOctetDeltaCount(4);

		private final IpfixTemplateForDataReader template = new IpfixTemplateForDataReader(
				f0, f1, f2, f3, f4, f5, f6, f7, f8, f9);

		@Override
		public IpfixRecord getRecord(ByteBuffer setBuffer) {
			if (!setBuffer.hasRemaining()) {
				return null;
			}
			return new IpfixRecordFlow4(f0.getBigInteger(setBuffer), f1
					.getBigInteger(setBuffer), f2.getAddress(setBuffer), f3
					.getAddress(setBuffer), f4.getInt(setBuffer), f5
					.getInt(setBuffer), f6.getShort(setBuffer), f7
					.getShort(setBuffer), f8.getLong(setBuffer), f9
					.getLong(setBuffer));
		}
		public String toString() {
			return "DRR(record flow 4)";
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
