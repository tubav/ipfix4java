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

	public IpfixMessage( IpfixTemplateManager templateManager, IpfixHeader header, ByteBuffer filebuffer ) {
		this.header = header;
		this.templateManager = templateManager;
		this.stats = templateManager.getStatistics();
		// slicing
		this.messageBuffer = ByteBufferUtil.sliceAndSkip(filebuffer, header.getLength() - IpfixHeader.SIZE_IN_OCTETS);
		stats.fileBufferPosition = filebuffer.position();

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
}
