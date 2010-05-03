package de.fhg.fokus.net.ipfix;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *  @author FhG-FOKUS NETwork Research
 */
public class IpfixCollectorTest {
	private final static Logger logger = LoggerFactory.getLogger(IpfixCollectorTest.class);
	@Test
	public void tcpCollector() throws Exception {
		logger.debug("");
		IpfixCollector collector = new IpfixCollector();
		
		collector.bind(4739);
		Thread.sleep(50000);
		collector.shutdow();
		
		
	}
	
}
