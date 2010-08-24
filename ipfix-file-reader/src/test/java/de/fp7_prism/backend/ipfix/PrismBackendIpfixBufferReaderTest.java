package de.fp7_prism.backend.ipfix;


import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.IpfixFileReaderTest;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixSet;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager.Statistics;
import de.fhg.fokus.net.ipfix.util.HexDump;
import eu.fp7_prism.backend.ipfix.PrismBackendIpfixBufferReader;
import eu.fp7_prism.backend.ipfix.model.IpfixRecordAsdfRtt;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 *
 */
public class PrismBackendIpfixBufferReaderTest {
	private final static Logger logger = LoggerFactory
	.getLogger(PrismBackendIpfixBufferReaderTest.class);
//	private static String[] filenames = {"nc-corrupted-01.ipfix", "nc-corrupted-02.ipfix", "nc-corrupted-03.ipfix" };
	private static String[] filenames = {"ibc-big-rtt.ipfix"};
//		
//	private static String[] filenames = {
//		"nc.ipfix",
//		"nc-corrupted-01.ipfix",
//		"nc-corrupted-02.ipfix",
//		"nc-corrupted-03.ipfix"
//		
//	};
//	private static String[] expectedResults = {
//		"stats:{msgs:220, data:{nsets:219, nrecs:219}, tmpl:{nsets:10, nrecs:10}, " +
//		"otmpl:{nsets:1, nrecs:1}, pos:{file:8322, set:20}, invalidbytes:0}",
//		"stats:{msgs:220, data:{nsets:219, nrecs:219}, tmpl:{nsets:10, nrecs:10}, " +
//		"otmpl:{nsets:1, nrecs:1}, pos:{file:8331, set:20}, invalidbytes:9}",
//		"stats:{msgs:218, data:{nsets:216, nrecs:216}, tmpl:{nsets:10, nrecs:10}, " +
//		"otmpl:{nsets:1, nrecs:1}, pos:{file:8333, set:20}, invalidbytes:83}",
//		"stats:{msgs:216, data:{nsets:214, nrecs:214}, tmpl:{nsets:10, nrecs:10}, " +
//		"otmpl:{nsets:1, nrecs:1}, pos:{file:8333, set:20}, invalidbytes:155}",
//	};
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// nothing to do yet

	}
	private Statistics readfile( File ipfixFile ) throws IOException{
		logger.info("Reading {}", ipfixFile );
		PrismBackendIpfixBufferReader reader = new PrismBackendIpfixBufferReader(ipfixFile);
		for( IpfixMessage msg : reader ){
			for (IpfixSet set : msg ){
				for(Object record: set ){
					if (record instanceof IpfixRecordAsdfRtt) {
						IpfixRecordAsdfRtt rtt = (IpfixRecordAsdfRtt) record;
						if(rtt.getRoundTripTime().longValue() ==  4206020633l ){
							logger.debug(msg+"");
							logger.debug(record+"");
							logger.debug(HexDump.toHexString(rtt.getByteBuffer()));
						}
					}
				}
			}
		}
		return reader.getFileReader().getStatistics();
	}
	
	@Test
	public void testPrismReader() throws IOException{
//		int i=0;
		for( String fname: filenames ){
			Statistics stat = readfile(IpfixFileReaderTest.getIpfixFile(fname));
			String statstr = stat+"";
			logger.debug(statstr);
			
			
//			Assert.assertEquals(expectedResults[i++], statstr);
		}
	}

}



