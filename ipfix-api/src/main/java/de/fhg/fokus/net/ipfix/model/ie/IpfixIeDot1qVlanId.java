package de.fhg.fokus.net.ipfix.model.ie;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeStatus;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.api.codec.IpfixIeCodecUnsigned16;



/**
 * <pre>
dot1qVlanId:{ 
  elementId:243, 
  dataType:unsigned16, 
  dataTypeSemantis:identifier, 
  units:octets 
  status:current 
  en: 0 
}
</pre> 
 * 
 */
public final class IpfixIeDot1qVlanId extends IpfixIeCodecUnsigned16 implements IpfixIe {
	// -- model --
	private final IpfixFieldSpecifier fieldSpecifier;

	@Override
	public IpfixFieldSpecifier getFieldSpecifier() {
		return fieldSpecifier;
	}

	public IpfixIeDot1qVlanId() {
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(243)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeDot1qVlanId( int length ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(243)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeDot1qVlanId( int length, long enterpriseNumber, boolean isScope ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(enterpriseNumber).setId(243)
				.setFieldLength(this.fieldLength).setScope(isScope);
	}


	@Override
	public IpfixIeSemantics getSemantics() {
		return IpfixIeSemantics.IDENTIFIER;
	}

	@Override
	public IpfixIeStatus getStatus() {
		return IpfixIeStatus.CURRENT;
	}

	@Override
	public String getName() {
		return "dot1qVlanId";
	}

	@Override
	public int getLength() {
		return fieldSpecifier.getIeLength();
	}

	@Override
	public IpfixIeUnits getUnits() {
		return IpfixIeUnits.OCTETS;
	}
}
