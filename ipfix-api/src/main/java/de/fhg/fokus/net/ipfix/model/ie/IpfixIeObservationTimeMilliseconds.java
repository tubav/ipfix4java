package de.fhg.fokus.net.ipfix.model.ie;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeStatus;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.api.codec.IpfixIeCodecDateTimeMilliseconds;



/**
 * <pre>
observationTimeMilliseconds:{ 
  elementId:323, 
  dataType:dateTimeMilliseconds, 
  dataTypeSemantis:quantity, 
  units:milliseconds 
  status:current 
  en: 0 
}
</pre> 
 * 
 */
public final class IpfixIeObservationTimeMilliseconds extends IpfixIeCodecDateTimeMilliseconds implements IpfixIe {
	// -- model --
	private final IpfixFieldSpecifier fieldSpecifier;

	@Override
	public IpfixFieldSpecifier getFieldSpecifier() {
		return fieldSpecifier;
	}

	public IpfixIeObservationTimeMilliseconds() {
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(323)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeObservationTimeMilliseconds( int length ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(323)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeObservationTimeMilliseconds( int length, long enterpriseNumber, boolean isScope ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(enterpriseNumber).setId(323)
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
		return "observationTimeMilliseconds";
	}

	@Override
	public int getLength() {
		return fieldSpecifier.getIeLength();
	}

	@Override
	public IpfixIeUnits getUnits() {
		return IpfixIeUnits.MILLISECONDS;
	}
}
