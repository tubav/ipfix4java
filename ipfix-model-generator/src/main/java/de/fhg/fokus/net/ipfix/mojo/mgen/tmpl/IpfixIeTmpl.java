package de.fhg.fokus.net.ipfix.mojo.mgen.tmpl;

import java.io.File;
/**
 * Text template definitions for generating IPFIX information elements.
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public class IpfixIeTmpl {
	/**
	 * Defined tokens in the template.
	 * 
	 * @author FhG-FOKUS NETwork Research
	 *
	 */
	public static enum Tokens {
		TARGET_PACKAGE,
		IMPORTS,
		CODEC_METHODS,
		CLASS_DOCUMENTATION,
		IE_ENTERPRISE_NUMBER,
		IE_ID,
		IE_CODEC,
		IE_CLASS_NAME,
		IE_DATA_TYPES,
		IE_SEMANTICS,
		IE_STATUS,
		IE_NAME,
		IE_UNITS
	}
	/**
	 * IpfixIe.tmpl
	 */
	public static File FILE = new File(IpfixIeTmpl.class.getResource(
			IpfixIeTmpl.class.getSimpleName() + ".tmpl").getFile());
}
