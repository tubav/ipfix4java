package de.fhg.fokus.net.ipfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixSet;
import de.fhg.fokus.net.ipfix.record.IpfixRecordSourceIpv4PacketDeltaCount;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixFileReaderTest {
	private final static Logger logger = LoggerFactory
	.getLogger(IpfixFileReaderTest.class);
	private static IpfixFileReader fileReader;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileReader = new IpfixFileReader(getIpfixFile("ok-01.ipfix"));
//		fileReader = new IpfixFileReader(getIpfixFile("rttexport.ipfix"));
//		fileReader = new IpfixFileReader(getIpfixFile("impd4e.ipfix"));

		fileReader.registerDataRecordReader(IpfixRecordSourceIpv4PacketDeltaCount.getReader());

		//		fileReader = new IpfixFileReader(getIpfixFile("ibc-01.ipfix"));
//		fileReader.registerDataRecordReader(IpfixRecordAsdfRtt.getReader());
//		fileReader.registerDataRecordReader(IpfixRecordAsdfAsDelay.getReader());
//		fileReader.registerDataRecordReader(IpfixRecordScope01.getReader());


		//		reader = new IpfixFileReader(getIpfixFile("packetexport.ipfix"));
		//		reader = new IpfixFileReader(getIpfixFile("rttexport.ipfix"));
		//		reader.registerDataRecordReader(reader)


	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIterator() {
		logger.debug("Iterating over messages");
		int i = 0;
		int maxMessages = 10;
		for (IpfixMessage msg : fileReader) {
			i++;
			if(i > maxMessages ){
				return;
			}
			for( IpfixSet set: msg ){
				logger.debug(" +-{}",set);
				for( Object rec: set ){
					logger.debug("   +-{}",rec);
				}
			}
		}
	}

	private static final String IPFIX_FILES_FOLDER = "target" + File.separator
	+ "test-classes" + File.separator + "ipfixfiles";
	/**
	 * Gets an IPFIX file for testing. It looks at IPFIX_FILES_FOLDER for the respective 
	 * ipfix file gunzipping it in the first run if necessary.
	 * 
	 * @param ipfixFilename
	 * @return
	 * @throws IOException
	 */
	public static File getIpfixFile(String ipfixFilename) throws IOException {
		String folderFile = IPFIX_FILES_FOLDER + File.separator + ipfixFilename;
		File ipfixFile = new File(folderFile);
		if (!ipfixFile.exists()) {
			File ipfixFileGz = new File(folderFile + ".gz");
			if (ipfixFileGz.isFile() && ipfixFileGz.canRead()) {
				logger.info("Unzipping " + ipfixFileGz);
				GZIPInputStream zipin = new GZIPInputStream(
						new FileInputStream(ipfixFileGz));
				FileOutputStream out = new FileOutputStream(ipfixFile);
				int length;
				int sChunk = 8192;
				byte[] buffer = new byte[sChunk];
				while ((length = zipin.read(buffer, 0, sChunk)) != -1) {
					out.write(buffer, 0, length);
				}
				out.close();
				zipin.close();
			} else {
				throw new FileNotFoundException(ipfixFileGz.getAbsolutePath());
			}
		}
		return ipfixFile;
	}
	public static void main(String[] args) {
		// quick test
		IpfixFieldSpecifier fs = new IpfixFieldSpecifier(12325).setId(199)
		.setFieldLength(4);
		logger.debug(fs.getUid());
		logger.debug(fs.getBytebuffer()+"");
	}
}
