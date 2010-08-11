package eu.fp7_prism.backend.ipfix.model;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixEnterpriseNumbers;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateForDataReader;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeHashOutputRangeMax;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeHashSelectedRangeMin;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeObservationDomainId;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeSystemInitTimeMilliseconds;

/**
 * TODO Rename this class.
 * 
 * <pre>
 * observationDomainId:        {id:149, len:4, en:0}
 * systemInitTimeMilliseconds: {id:160, len:8, en:0}
 * hashOutputRangeMax:         {id:330, len:4, en:12325}
 * hashSelectedRangeMin:       {id:331, len:4, en:12325, scope:true}
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixRecordScope01  {
	private final long observationDomainId;
	private final BigInteger systemInitTimeMilliseconds;
	private final long hashOutputRangeMax;
	private final long hashSelectedRangeMin;

	public IpfixRecordScope01(long observationDomainId,
			BigInteger systemInitTimeMilliseconds, long hashOutputRangeMax,
			long hashSelectedRangeMin) {
		this.observationDomainId = observationDomainId;
		this.systemInitTimeMilliseconds = systemInitTimeMilliseconds;
		this.hashOutputRangeMax = hashOutputRangeMax;
		this.hashSelectedRangeMin = hashSelectedRangeMin;
	}

	@Override
	public String toString() {
		return String.format("{observationDomainId:%d, systemInitTimeMilliseconds:%d, " +
				"hashOutputRangeMax:%d, hashSelectedRangeMin:%d }", observationDomainId,
				systemInitTimeMilliseconds, hashOutputRangeMax,
				hashSelectedRangeMin);
	}

	public long getObservationDomainId() {
		return observationDomainId;
	}

	public BigInteger getSystemInitTimeMilliseconds() {
		return systemInitTimeMilliseconds;
	}

	public long getHashOutputRangeMax() {
		return hashOutputRangeMax;
	}

	public long getHashSelectedRangeMin() {
		return hashSelectedRangeMin;
	}

	// -- data record reader --
	private final static IpfixDataRecordReader reader = new IpfixDataRecordReader() {
		private final IpfixIeObservationDomainId f0 = new IpfixIeObservationDomainId(
				4);
		private final IpfixIeSystemInitTimeMilliseconds f1 = new IpfixIeSystemInitTimeMilliseconds(
				8);
		private final IpfixIeHashOutputRangeMax f2 = new IpfixIeHashOutputRangeMax(
				4,IpfixEnterpriseNumbers.FRAUNHOFER_FOKUS.getDecimal(),false);
		private final IpfixIeHashSelectedRangeMin f3 = new IpfixIeHashSelectedRangeMin(
				4, IpfixEnterpriseNumbers.FRAUNHOFER_FOKUS.getDecimal(),true);
		// -- template --
		private final IpfixTemplateForDataReader template = new IpfixTemplateForDataReader(
				f0, f1, f2, f3);

		@Override
		public IpfixTemplateForDataReader getTemplate() {
			return template;
		}

		@Override
		public Object getRecord(ByteBuffer setBuffer) {
			return new IpfixRecordScope01(f0.getLong(setBuffer), f1
					.getBigInteger(setBuffer), f2.getBigInteger(setBuffer)
					.longValue(), f3.getBigInteger(setBuffer).longValue());
		}
	};

	public static IpfixDataRecordReader getReader() {
		return reader;
	}

}
