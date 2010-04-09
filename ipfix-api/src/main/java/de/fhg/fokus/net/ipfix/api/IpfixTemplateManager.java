package de.fhg.fokus.net.ipfix.api;

/**
 * Manages IPFIX template state.
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public interface IpfixTemplateManager {
	public class Statistics {
		public long numberOfMessages = 0;
		public long numberOfDataSets = 0;
		public long numberOfDataRecords = 0;
		public long numberOfTemplateSets = 0;
		public long numberOfTemplateRecords = 0;
		public long numberOfOptionTemplateSets = 0;
		public long numberOfOptionTemplateRecords = 0;
		public long fileBufferPosition = 0;
		public long invalidBytes = 0;
		public int setBufferPosition = 0;

		@Override
		public String toString() {
			return String
					.format(
							"stats:{msgs:%d, data:{nsets:%d, nrecs:%d}, tmpl:{nsets:%d, nrecs:%d}, otmpl:{nsets:%d, nrecs:%d}, pos:{file:%d, set:%d}, invalidbytes:%d}",
							numberOfMessages, numberOfDataSets,
							numberOfDataRecords, numberOfTemplateSets,
							numberOfTemplateRecords,
							numberOfOptionTemplateSets,
							numberOfOptionTemplateRecords,
							fileBufferPosition,
							setBufferPosition,
							invalidBytes
					);
		}
	}

	/**
	 * Register template record so that subsequently data records can be
	 * decoded.
	 * 
	 * @param ipfixTemplateRecord
	 */
	public void registerTemplateRecord(IpfixTemplateRecord ipfixTemplateRecord);

	/**
	 * Register option template record so that subsequently records can be
	 * decoded.
	 * 
	 * @param ipfixOptionsTemplateRecord
	 */
	public void registerOptionsTemplateRecord(
			IpfixOptionsTemplateRecord ipfixOptionsTemplateRecord);

	/**
	 * Data record reader are responsible for interpreting IPFIX data sets.
	 * Information elements are also registered by this call;
	 * 
	 * @param reader
	 */
	public void registerDataRecordReader(IpfixDataRecordReader reader);

	/**
	 * 
	 * @param templateId
	 * @return the respective ipfix data record reader or null if setId is
	 *         unknown
	 */
	public IpfixDataRecordReader getDataRecordReader(int setId);

	public IpfixDataRecordSpecifier getDataRecordSpecifier(int setId);

	/**
	 * 
	 * @param fieldSpecifier
	 * @return information element
	 */
	public IpfixIe getInformationElement(IpfixFieldSpecifier fieldSpecifier);

	public Statistics getStatistics();

}
