package de.fhg.fokus.net.ipfix.api;

/**
 * TODO auto generate from
 * http://www.iana.org/assignments/enterprise-numbers
 
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public enum IpfixEnterpriseNumbers {
	/**
	 * <pre>
	 * { Decimal:12325, 
	 *   Organization:"Fraunhofer FOKUS", 
	 *   Contact:"Hartmut Brandt", 
	 *   Email:"brandt&fokus.gmd.de" }
	 * </pre>
	 */
	FRAUNHOFER_FOKUS(12325, "Fraunhofer FOKUS", "Hartmut Brandt",
			"brandt&fokus.gmd.de"),
	/**
	 * <pre>
	 * { Decimal:29305,
	 *   Organization:"IPFIX Reverse Information Element Private Enterprise",
	 *   Contact:"RFC5103 Authors",
	 *   Email:"ipfix-biflow&cert.org" }
	 * </pre>
	 */
	IPFIX_REVERSE_INFORMATION_ELEMENT_PRIVATE_ENTERPRISE(29305,
			"IPFIX Reverse Information Element Private Enterprise",
			"RFC5103 Authors", "ipfix-biflow&cert.org");
	private final int decimal;
	private final String organization;
	private final String contact;
	private final String email;

	private IpfixEnterpriseNumbers(int decimal, String organization,
			String contact, String email) {
		this.decimal = decimal;
		this.organization = organization;
		this.contact = contact;
		this.email = email;
	}

	public int getDecimal() {
		return decimal;
	}

	public String getOrganization() {
		return organization;
	}

	public String getContact() {
		return contact;
	}

	public String getEmail() {
		return email;
	}

}
