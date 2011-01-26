package eu.fp7_prism.backend.ipfix;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import de.fhg.fokus.net.ipfix.IpfixFileReader;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import eu.fp7_prism.backend.ipfix.model.IpfixRecordAsdfAsDelay;
import eu.fp7_prism.backend.ipfix.model.IpfixRecordAsdfRtt;
import eu.fp7_prism.backend.ipfix.model.IpfixRecordTransactionOptions;

/**
 * IPFIX Buffer Reader. It's a simple wrapper to {@link IpfixFileReader} with the respective PRISM
 * {@link IpfixIe}s pre-registered. 
 *  
 * @author FhG-FOKUS NETwork Research
 * 
 */

public class PrismBackendIpfixBufferReader implements Iterable<IpfixMessage> {
	// -- sys --
//	private final Logger logger = LoggerFactory.getLogger(getClass());
	// -- model --
	private IpfixFileReader fileReader;
	/**
	 * @param bufferFile
	 * @throws IOException 
	 */
	public  PrismBackendIpfixBufferReader(File bufferFile ) throws IOException{
		this.fileReader = new IpfixFileReader(bufferFile);
		this.fileReader.registerDataRecordReader(IpfixRecordAsdfRtt.getReader());
		this.fileReader.registerDataRecordReader(IpfixRecordAsdfAsDelay.getReader());
		this.fileReader.registerDataRecordReader(IpfixRecordTransactionOptions.getReader());
		
		// just for testing
//		this.fileReader.registerDataRecordReader(IpfixRecordSourceIpv4PacketDeltaCount.getReader());

	}

	@Override
	public Iterator<IpfixMessage> iterator() {
		return this.fileReader.iterator();
	}
	@Override
	public String toString() {
		return fileReader.toString();
	}

	public IpfixFileReader getFileReader() {
		return fileReader;
	}
	
	
}
