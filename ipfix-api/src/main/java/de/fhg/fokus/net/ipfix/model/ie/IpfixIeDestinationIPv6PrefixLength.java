package de.fhg.fokus.net.ipfix.model.ie;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeStatus;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.api.codec.IpfixIeCodecUnsigned8;



/**
 * <pre>
destinationIPv6PrefixLength:{ 
  elementId:30, 
  dataType:unsigned8, 
  dataTypeSemantis:null, 
  units:bits 
  status:current 
  en: 0 
}
</pre> 
 * 
 */
public final class IpfixIeDestinationIPv6PrefixLength extends IpfixIeCodecUnsigned8 implements IpfixIe {
	// -- model --
	private final IpfixFieldSpecifier fieldSpecifier;

	@Override
	public IpfixFieldSpecifier getFieldSpecifier() {
		return fieldSpecifier;
	}

	public IpfixIeDestinationIPv6PrefixLength() {
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(30)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeDestinationIPv6PrefixLength( int length ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(30)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeDestinationIPv6PrefixLength( int length, long enterpriseNumber, boolean isScope ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(enterpriseNumber).setId(30)
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
		return "destinationIPv6PrefixLength";
	}

	@Override
	public int getLength() {
		return fieldSpecifier.getIeLength();
	}

	@Override
	public IpfixIeUnits getUnits() {
		return IpfixIeUnits.BITS;
	}
}
