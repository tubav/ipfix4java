package de.fhg.fokus.net.ipfix;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixMessageListener;
import de.fhg.fokus.net.ipfix.api.IpfixRecord;
import de.fhg.fokus.net.ipfix.api.IpfixSet;
import de.fhg.fokus.net.ipfix.record.IpfixRecordSourceIpv4PacketDeltaCount;

/**
 * 
 *  @author FhG-FOKUS NETwork Research
 */
public class IpfixCollectorTest {
	private final static Logger logger = LoggerFactory.getLogger(IpfixCollectorTest.class);
	@Test
	public void tcpCollector() throws Exception {
		int sleep = 50;
		logger.debug("--- IPFIX Collector test ---");
		logger.debug("Shutting down in {}s",sleep);
		
		IpfixCollector ic = new IpfixCollector();
		ic.registerDataRecordReader(IpfixRecordSourceIpv4PacketDeltaCount.getReader());
		ic.addMessageListener(new IpfixMessageListener() {
			@Override
			public void onMessage(IpfixMessage msg) {
				logger.debug(msg+"");
				for(IpfixSet set: msg){
					for(IpfixRecord rec: set){
						logger.debug(rec+"");
					}
				}
			}
		});
		
		ic.bind(4739);
		Thread.sleep(sleep*1000);
		ic.shutdow();
		
		
	}
	
}
