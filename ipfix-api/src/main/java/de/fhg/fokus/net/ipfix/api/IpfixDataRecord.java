/**
 * 
 */
package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixIeCodec.VariableLength;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * @author FhG-FOKUS NETwork Research
 *
 */
public class IpfixDataRecord {
	// -- sys --
	private static final Logger logger = LoggerFactory.getLogger(IpfixDataRecord.class);

	private ByteBuffer buffer;
	private HashMap<IpfixFieldSpecifier, ByteBuffer> map;

	public IpfixDataRecord(ByteBuffer setBuffer,IpfixTemplateRecord templateRecord) {
		// store a slice of the whole field buffer
		this.buffer = setBuffer.slice();
		int sbPosition = setBuffer.position();
		// create a hash map for the available fields
		this.map = new HashMap<IpfixFieldSpecifier, ByteBuffer>(templateRecord.getFieldSpecifiers().size());
		try {
			logger.trace("receive record: {}", templateRecord);
			// traverse field specifier
			for( IpfixFieldSpecifier fs : templateRecord.getFieldSpecifiers()) {
				ByteBuffer tmpBuffer;
				if( IpfixIe.VARIABLE_LENGTH == fs.getIeLength() ) {
					tmpBuffer = VariableLength.getRawByteBuffer(setBuffer);
				}
				else {
					tmpBuffer = ByteBufferUtil.sliceAndSkip(setBuffer, fs.getIeLength());
				}
				map.put(fs, tmpBuffer);
				logger.trace(fs.toString());
			}
			// set limit of local buffer to the end of decoding
			buffer.limit(setBuffer.position()-sbPosition);
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.debug(e.toString());
			e.printStackTrace();
		}
	}
	
	public ByteBuffer getByteBuffer( IpfixFieldSpecifier fs ) {
		return map.get(fs);
	}

	// TODO: a nice descriptive output of the ipfix data record
	@Override
	public String toString() {
		return "IpfixDataRecord [buffer=" + buffer + ", map=" + map + "]";
	}
}
