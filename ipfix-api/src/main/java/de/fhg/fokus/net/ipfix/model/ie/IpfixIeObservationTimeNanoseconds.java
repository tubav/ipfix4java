package de.fhg.fokus.net.ipfix.model.ie;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeStatus;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.api.codec.IpfixIeCodecDateTimeNanoseconds;



/**
 * <pre>
observationTimeNanoseconds:{ 
  elementId:325, 
  dataType:dateTimeNanoseconds, 
  dataTypeSemantis:quantity, 
  units:nanoseconds 
  status:current 
  en: 0 
}
</pre> 
 * 
 */
public final class IpfixIeObservationTimeNanoseconds extends IpfixIeCodecDateTimeNanoseconds implements IpfixIe {
	// -- model --
	private final IpfixFieldSpecifier fieldSpecifier;

	@Override
	public IpfixFieldSpecifier getFieldSpecifier() {
		return fieldSpecifier;
	}

	public IpfixIeObservationTimeNanoseconds() {
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(325)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeObservationTimeNanoseconds( int length ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(325)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeObservationTimeNanoseconds( int length, long enterpriseNumber, boolean isScope ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(enterpriseNumber).setId(325)
				.setFieldLength(this.fieldLength).setScope(isScope);
	}


	@Override
	public IpfixIeSemantics getSemantics() {
		return IpfixIeSemantics.QUANTITY;
	}

	@Override
	public IpfixIeStatus getStatus() {
		return IpfixIeStatus.CURRENT;
	}

	@Override
	public String getName() {
		return "observationTimeNanoseconds";
	}

	@Override
	public int getLength() {
		return fieldSpecifier.getIeLength();
	}

	@Override
	public IpfixIeUnits getUnits() {
		return IpfixIeUnits.NANOSECONDS;
	}
}
