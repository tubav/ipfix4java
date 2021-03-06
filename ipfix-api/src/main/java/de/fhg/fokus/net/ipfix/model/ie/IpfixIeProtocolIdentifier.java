package de.fhg.fokus.net.ipfix.model.ie;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeStatus;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.api.codec.IpfixIeCodecUnsigned8;



/**
 * <pre>
protocolIdentifier:{ 
  elementId:4, 
  dataType:unsigned8, 
  dataTypeSemantis:identifier, 
  units:null 
  status:current 
  en: 0 
}
</pre> 
 * 
 */
public final class IpfixIeProtocolIdentifier extends IpfixIeCodecUnsigned8 implements IpfixIe {
	// -- model --
	private final IpfixFieldSpecifier fieldSpecifier;

	@Override
	public IpfixFieldSpecifier getFieldSpecifier() {
		return fieldSpecifier;
	}

	public IpfixIeProtocolIdentifier() {
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(4)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeProtocolIdentifier( int length ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(4)
				.setFieldLength(this.fieldLength);
		if(length!=1){
			throw new RuntimeException("Field length must be 1!");
		}
	}
	public IpfixIeProtocolIdentifier( int length, long enterpriseNumber, boolean isScope ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(enterpriseNumber).setId(4)
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
		return "protocolIdentifier";
	}

	@Override
	public int getLength() {
		return fieldSpecifier.getIeLength();
	}

	@Override
	public IpfixIeUnits getUnits() {
		return IpfixIeUnits.NONE;
	}
}
