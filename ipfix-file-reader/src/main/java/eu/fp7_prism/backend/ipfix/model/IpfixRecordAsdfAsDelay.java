package eu.fp7_prism.backend.ipfix.model;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateForDataReader;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeBgpDestinationAsNumber;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeObservationTimeMilliseconds;
import eu.fp7_prism.backend.ipfix.model.ie.IpfixIeRoundTripTime;

/**
 * <pre>
 * bgpDestinationAsNumber       {id:17,  len:4, en:0}
 * observationTimeMilliseconds  {id:323, len:8, en:0}
 * roundTripTime                {id:199, len:8, en:12325}
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */

public final class IpfixRecordAsdfAsDelay {
	private final long ipfixIeBgpDestinationAsNumber;
	private final BigInteger observationTimeMilliseconds;
	private final BigInteger rountTripTime;

	public IpfixRecordAsdfAsDelay(long ipfixIeBgpDestinationAsNumber,
			BigInteger observationTimeMilliseconds, BigInteger rountTripTime) {
		this.ipfixIeBgpDestinationAsNumber = ipfixIeBgpDestinationAsNumber;
		this.observationTimeMilliseconds = observationTimeMilliseconds;
		this.rountTripTime = rountTripTime;
	}

	public long getIpfixIeBgpDestinationAsNumber() {
		return ipfixIeBgpDestinationAsNumber;
	}

	public BigInteger getObservationTimeMilliseconds() {
		return observationTimeMilliseconds;
	}

	public BigInteger getRountTripTime() {
		return rountTripTime;
	}

	public static IpfixDataRecordReader getReader() {
		return reader;
	}
	private static final IpfixDataRecordReader reader = new IpfixDataRecordReader() {
		// -- fields --
		private final IpfixIeBgpDestinationAsNumber f0 = new IpfixIeBgpDestinationAsNumber(
				4);
		private final IpfixIeObservationTimeMilliseconds f1 = new IpfixIeObservationTimeMilliseconds(
				8);
		private final IpfixIeRoundTripTime f2 = new IpfixIeRoundTripTime(8);
		// -- template --
		private final IpfixTemplateForDataReader template = new IpfixTemplateForDataReader(
				f0, f1, f2);

		@Override
		public IpfixTemplateForDataReader getTemplate() {
			return template;
		}

		@Override
		public Object getRecord(ByteBuffer setBuffer) {
			return new IpfixRecordAsdfAsDelay(f0.getLong(setBuffer), f1
					.getBigInteger(setBuffer), f2.getBigInteger(setBuffer));
		}
	};

}
