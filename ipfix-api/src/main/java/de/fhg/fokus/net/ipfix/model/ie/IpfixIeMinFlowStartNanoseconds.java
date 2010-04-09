package de.fhg.fokus.net.ipfix.model.ie;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeStatus;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.api.codec.IpfixIeCodecDateTimeNanoseconds;



/**
 * <pre>
minFlowStartNanoseconds:{ 
  elementId:273, 
  dataType:dateTimeNanoseconds, 
  dataTypeSemantis:null, 
  units:nanoseconds 
  status:current 
  en: 0 
}
</pre> 
 * 
 */
public final class IpfixIeMinFlowStartNanoseconds extends IpfixIeCodecDateTimeNanoseconds implements IpfixIe {
	// -- model --
	private final IpfixFieldSpecifier fieldSpecifier;

	@Override
	public IpfixFieldSpecifier getFieldSpecifier() {
		return fieldSpecifier;
	}

	public IpfixIeMinFlowStartNanoseconds() {
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(273)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeMinFlowStartNanoseconds( int length ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(273)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeMinFlowStartNanoseconds( int length, long enterpriseNumber, boolean isScope ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(enterpriseNumber).setId(273)
				.setFieldLength(this.fieldLength).setScope(isScope);
	}


	@Override
	public IpfixIeSemantics getSemantics() {
		return IpfixIeSemantics.DEFAULT;
	}

	@Override
	public IpfixIeStatus getStatus() {
		return IpfixIeStatus.CURRENT;
	}

	@Override
	public String getName() {
		return "minFlowStartNanoseconds";
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
