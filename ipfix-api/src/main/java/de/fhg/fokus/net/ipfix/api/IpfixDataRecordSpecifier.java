package de.fhg.fokus.net.ipfix.api;
/**
 * <p> Can be an {@link IpfixOptionsTemplateRecord} or 
 * an {@link IpfixTemplateRecord }. </p>
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public interface IpfixDataRecordSpecifier {

	public int getTemplateId();
	public int getDataRecordLength();
	public boolean isVariableLength();
}
