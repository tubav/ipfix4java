package de.fhg.fokus.net.ipfix.mojo.mgen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixIeSemantics;
import de.fhg.fokus.net.ipfix.api.IpfixIeUnits;
import de.fhg.fokus.net.ipfix.mojo.mgen.tmpl.IpfixIeTmpl.Tokens;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixIeModelGenerator implements IpfixModelGenerator {
	// -- sys --
	private final Logger logger = LoggerFactory.getLogger(getClass());
	// -- constants --
	private final static String IANA_REGISTRY_ID = "ipfix-information-element-definitions";
	// -- model --
	private final TextTemplate txtTemplate;
	private final IanaRegistry registry;
	private final String targetPackage;
	private final File targetFolder;

	public IpfixIeModelGenerator(TextTemplate txtTemplate,
			IanaRegistry ianaRegistry, String targetPackage, File prefixFolder ) {
		this.txtTemplate = txtTemplate;
		this.targetPackage = targetPackage;
		this.targetFolder = new File(prefixFolder,targetPackage.replaceAll("\\.", File.separator) );

		this.registry = IanaRegistry.find(ianaRegistry, IANA_REGISTRY_ID, 10);
		if (registry == null) {
			logger.debug("Registry ID {} was not found!", IANA_REGISTRY_ID);
			return;
		}
		logger.debug("Found registry: {}", registry);
	}

	private static String getClassNameForIe(IanaRecord record) {
		return String.format("IpfixIe%s%s", Character.toUpperCase(record.name.charAt(0)),
				record.name.substring(1));
	}
	private static String getCodecName( IanaRecord record ){
		return String.format("IpfixIeCodec%s%s", Character.toUpperCase(record.dataType.charAt(0)),
				record.dataType.substring(1));

	}
	private static String getClassDocumentation( IanaRecord record ){
		return String.format("<pre>\n%s\n</pre>", record.toString());
	}
	private static String getImports( IanaRecord record ){
		return "";
	}
//	private static String getEnterpriseNumber( IanaRecord record ){
//		return record.enterpriseNumber==null?0:record.enterpriseNumber;
//	}
	@Override
	public void generate() {
		// -- creating destination folder
		if( !targetFolder.canRead() ){
			targetFolder.mkdirs();
		}

		// -- processing records --
		if (registry.records == null || registry.records.size() == 0) {
			logger.warn("No records in registry found.");
			return;
		}
		Map<String, String> replacements = new HashMap<String, String>();
		// -- common replacements --
		replacements.put(Tokens.TARGET_PACKAGE+"", targetPackage);

		for (IanaRecord record : registry.records) {
			//		IanaRecord record = registry.records.get(0);
			if (record != null && record.isValid()) {
				record.cleanup();
				logger.debug(record + "");
				// -- record replacements --
				String ieClassName = getClassNameForIe(record);
				replacements.put(Tokens.CLASS_DOCUMENTATION+"",getClassDocumentation(record));
				replacements.put(Tokens.IE_ID+"",record.elementId);
				replacements.put(Tokens.IE_CLASS_NAME+"", ieClassName);
				replacements.put(Tokens.IE_NAME+"", record.name);
				replacements.put(Tokens.IE_CODEC+"", getCodecName(record));
				replacements.put(Tokens.IE_DATA_TYPES+"", record.dataType.toUpperCase());
				replacements.put(Tokens.IE_SEMANTICS+"", IpfixIeSemantics.getFromModelName(record.dataTypeSemantics)+"");
				replacements.put(Tokens.IE_STATUS+"", record.status.toUpperCase());
				replacements.put(Tokens.IE_UNITS+"", IpfixIeUnits.getFromModelName(record.units)+"");
				replacements.put(Tokens.IMPORTS+"", getImports(record));
				replacements.put(Tokens.IE_ENTERPRISE_NUMBER+"",record.enterpriseNumber+"");


				try {
					txtTemplate.generateFile(replacements, new File(targetFolder,ieClassName+".java" ));
				} catch (IOException e) {
					logger.error("Could not generate: {}, {}",ieClassName, e.getMessage());
				}

				// String.format("IpfixIe",record.name.to) );
			}

		}

	}

}
