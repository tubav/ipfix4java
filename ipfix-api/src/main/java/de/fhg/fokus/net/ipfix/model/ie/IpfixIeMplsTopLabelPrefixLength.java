package de.fhg.fokus.net.ipfix.model.ie;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeStatus;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.api.codec.IpfixIeCodecUnsigned8;



/**
 * <pre>
mplsTopLabelPrefixLength:{ 
  elementId:91, 
  dataType:unsigned8, 
  dataTypeSemantis:identifier, 
  units:bits 
  status:current 
  en: 0 
}
</pre> 
 * 
 */
public final class IpfixIeMplsTopLabelPrefixLength extends IpfixIeCodecUnsigned8 implements IpfixIe {
	// -- model --
	private final IpfixFieldSpecifier fieldSpecifier;

	@Override
	public IpfixFieldSpecifier getFieldSpecifier() {
		return fieldSpecifier;
	}

	public IpfixIeMplsTopLabelPrefixLength() {
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(91)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeMplsTopLabelPrefixLength( int length ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(0).setId(91)
				.setFieldLength(this.fieldLength);
	}
	public IpfixIeMplsTopLabelPrefixLength( int length, long enterpriseNumber, boolean isScope ) {
		this.fieldLength = length;
		this.fieldSpecifier = new IpfixFieldSpecifier(enterpriseNumber).setId(91)
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
		return "mplsTopLabelPrefixLength";
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
