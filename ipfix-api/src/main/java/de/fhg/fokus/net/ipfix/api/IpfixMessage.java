package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager.Statistics;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public final class IpfixMessage implements Iterable<IpfixSet> {
	// -- sys --
	private final static  Logger logger = LoggerFactory.getLogger(IpfixMessage.class);
	// -- model --
	private IpfixHeader header;
	private ByteBuffer messageBuffer;
	// -- management --
	private final IpfixTemplateManager templateManager;
	private final Statistics stats;

	public IpfixMessage( IpfixTemplateManager templateManager, IpfixHeader header, ByteBuffer messageBuffer ) {
		this.header = header;
		this.templateManager = templateManager;
		this.stats = templateManager.getStatistics();
		// slicing
		this.messageBuffer = ByteBufferUtil.sliceAndSkip(messageBuffer, header.getLength() - IpfixHeader.SIZE_IN_OCTETS);
		stats.globalBufferPosition = messageBuffer.position();

	}

	public IpfixHeader getHeader() {
		return header;
	}

	public void setHeader(IpfixHeader header) {
		this.header = header;
	}

	public Iterator<IpfixSet> iterator() {
		return new Iterator<IpfixSet>() {

			private final ByteBuffer iMessageBuffer= messageBuffer.slice();
			private IpfixSetHeader currentSetHeader;
			private IpfixSet last=null, next = null;
			public boolean hasNext() {
				if( next !=null ){
					return true;
				}
				if(iMessageBuffer.hasRemaining()){
					try {
						currentSetHeader = new IpfixSetHeader(iMessageBuffer);
						next = new IpfixSet(templateManager, currentSetHeader, iMessageBuffer);
						return true;
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
				return false;
			}

			public IpfixSet next() {
				if(next==null && !hasNext()){
					throw new NoSuchElementException();
				}
				last=next;
				next = null;
				return last;
			}

			public void remove() {
				throw new UnsupportedOperationException("Cannot remove sets!");
			}

		};
	}

	@Override
	public String toString() {
		return String.format("msg:[ %s ]", header.toString());
	}
	/**
	 * Align byte buffer containing IPFIX messages. It skips invalid data positioning
	 * the buffer in a valid message. The current implementation uses two finger 
	 * alignment.
	 * 
	 */
	public static boolean align(ByteBuffer byteBuffer ) {
		// two fingers alignment
		int pos = byteBuffer.position();
		int msg1_version = ByteBufferUtil.getUnsignedShort(byteBuffer,
				pos + IpfixHeader.IDX_VERSION);
		// first finger
		if (msg1_version == Ipfix.VERSION) {
			int msg1_length = ByteBufferUtil.getUnsignedShort(byteBuffer,
					pos + IpfixHeader.IDX_LENGTH);
			if( msg1_length==0){
				logger
				.warn("Strange, message length is 0! I'll skip this "
						+ "and to continue searching for a valid ipfix header.");
				return false;
			}
			int limit = byteBuffer.limit();
			if (msg1_length > limit) {
				logger
				.warn("Message length bigger than available data, I'll skip this "
						+ "and to continue searching for a valid ipfix header.");
				return false;
			}
			if (byteBuffer.limit() > pos + msg1_length
					+ IpfixHeader.SIZE_IN_OCTETS) {
				// second finger
				return ByteBufferUtil.getUnsignedShort(byteBuffer, pos
						+ msg1_length + IpfixHeader.IDX_VERSION) == Ipfix.VERSION;
			}
			return true;
		}

		return false;

	}

}
