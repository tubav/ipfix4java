package eu.fp7_prism.backend.ipfix.model;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixEnterpriseNumbers;
import de.fhg.fokus.net.ipfix.api.IpfixRecord;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateForDataReader;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeObservationDomainId;
import de.fhg.fokus.net.ipfix.model.ie.IpfixIeSystemInitTimeMilliseconds;
import eu.fp7_prism.backend.ipfix.model.ie.IpfixIeSessionId;
import eu.fp7_prism.backend.ipfix.model.ie.IpfixIeTransactionId;

/**
 * <pre>
 * observationDomainId        {id:149, len:4, en:0}
 * systemInitTimeMilliseconds {id:160, len:8, en:0},
 * sessionId                  {id:330, len:4, en:12325}, 
 * transactionId              {id:331, len:4, en:12325, scope:true}
 * 
 * </pre>
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixRecordTransactionOptions implements IpfixRecord {
	private final long observationDomainId;
	private final BigInteger systemInitTimeMilliseconds;
	private final long sessionId;
	private final long transactionId;

	public IpfixRecordTransactionOptions(long observationDomainId,
			BigInteger systemInitTimeMilliseconds, long sessionId,
			long transactionId) {
		super();
		this.observationDomainId = observationDomainId;
		this.systemInitTimeMilliseconds = systemInitTimeMilliseconds;
		this.sessionId = sessionId;
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return String
				.format(
						"{observationDomainId:%d, systemInitTimeMilliseconds:%d, sessionId:%d, transactionId:%d }",
						observationDomainId, systemInitTimeMilliseconds,
						sessionId, transactionId);
	}

	public long getObservationDomainId() {
		return observationDomainId;
	}

	public BigInteger getSystemInitTimeMilliseconds() {
		return systemInitTimeMilliseconds;
	}

	public long getSessionId() {
		return sessionId;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public static IpfixDataRecordReader getReader() {
		return reader;
	}

	// -- data record reader --
	public final static IpfixDataRecordReader reader = new IpfixDataRecordReader() {
		// -- ie --
		private final IpfixIeObservationDomainId f0 = new IpfixIeObservationDomainId(
				4);
		private final IpfixIeSystemInitTimeMilliseconds f1 = new IpfixIeSystemInitTimeMilliseconds(
				8);
		private final IpfixIeSessionId f2 = new IpfixIeSessionId(4);
		private final IpfixIeTransactionId f3 = new IpfixIeTransactionId(4,
				IpfixEnterpriseNumbers.FRAUNHOFER_FOKUS.getDecimal(), true);
		// -- template --
		private final IpfixTemplateForDataReader template = new IpfixTemplateForDataReader(
				f0, f1, f2, f3);

		@Override
		public IpfixTemplateForDataReader getTemplate() {
			return template;
		}

		@Override
		public IpfixRecord getRecord(ByteBuffer setBuffer) {
			return new IpfixRecordTransactionOptions(f0.getLong(setBuffer), f1
					.getBigInteger(setBuffer), f2.getLong(setBuffer), f3
					.getLong(setBuffer));
		}
	};

}
