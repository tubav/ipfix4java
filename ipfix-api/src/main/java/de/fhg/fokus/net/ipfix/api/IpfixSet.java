package de.fhg.fokus.net.ipfix.api;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager.Statistics;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;
import de.fhg.fokus.net.ipfix.util.HexDump;

/**
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixSet implements Iterable<Object> {
	// -- sys --
	private static final Logger logger = LoggerFactory.getLogger(IpfixSet.class);
	
	// -- model --
	private final IpfixSetHeader header;
	private ByteBuffer setBuffer;
	private IpfixSetType type;
	private final IpfixMessage msg; // won't be used here, just passed through to delegated decoding
	
	// -- management --
	private final IpfixTemplateManager templateManager;
	private final Statistics stats;
	
	private Iterator<Object> iterator = new Iterator<Object>() {
		@Override
		public void remove() {
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		@Override
		public boolean hasNext() {
			logger.warn("Trying to iterate over an invalid set: {}", IpfixSet.this.toString());
			return false;
		}
	};
	
	public IpfixSet(  IpfixMessage msg
					, IpfixTemplateManager templateManager
					, IpfixSetHeader header
					, ByteBuffer setsBuffer ) {
		// set member
		this.msg = msg;
		this.header = header;
		this.templateManager = templateManager;
		this.stats = templateManager.getStatistics();

		if(this.header.getLength()==0){
			throw new RuntimeException("Set length is 0! At " + this.stats.globalBufferPosition);
		}
		
		// set the set buffer to the current set and skip the source buffer
		// behind this set
		this.setBuffer = ByteBufferUtil.sliceAndSkip(setsBuffer
								, this.header.getLength() - IpfixSetHeader.SIZE_IN_OCTETS);
		stats.setBufferPosition = setsBuffer.position();
		
		// Setting up record iterator
		this.type = IpfixSetType.getSetType(this.header.getSetId());
		switch (type) {
		// -------------------------------------------------------------------
		// Reading data records
		// -------------------------------------------------------------------
		case DATA:
			logger.debug("+- DATA Set received");
			stats.numberOfDataSets++;
			final int setId = this.header.getSetId();
			final IpfixDataRecordReader 
				recordReader = templateManager.getDataRecordReader(setId);
			final IpfixDataRecordSpecifier 
				recordSpecifier = templateManager.getDataRecordSpecifier(setId);
			logger.debug("set id: " + setId);
			logger.debug("reader: " + recordReader);
			logger.debug("record spec: " + recordSpecifier);
			
			iterator = new RecordIterator() {
				@Override
				public boolean hasNext() {
					if (next != null) {
						return true;
					}
					// next is null
					if (setBuffer.hasRemaining()) {
						// there have to be a record specifier for a received template
						if ( null != recordSpecifier ) {
							// TODO: check for type cast
							IpfixDataRecord dataRecord = new IpfixDataRecord(setBuffer, (IpfixTemplateRecord) recordSpecifier);
							//logger.debug(dataRecord.toString());
							if( null != recordReader ) {
								// if there is a registered reader call its 'getRecord' method
								
								// read field record
								// get record from record reader
								next = recordReader.getRecord(IpfixSet.this.msg, dataRecord);
								// fall back to old handling
								if( null == next ) {
									next = recordReader.getRecord(IpfixSet.this.msg, setBuffer);
								}
								stats.numberOfDataRecords++;
							}
							else {
								// otherwise return the plain IpfixDataRecord
								next = dataRecord;
							}
						}
						else {
							IpfixSet.this.msg.incNumberOfunknownSets();
							// Skipping unknown set
							logger.debug("Got unknown set, did the exporter " +
									"send all template records? setid: {}, hexdump:{}",
									setId,
									HexDump.toHexString(setBuffer.slice()));
						}
						// return the state if there is a record
						return null != next;
					} // if (setBuffer.hasRemaining())
					return false;
				}
			};
			break;
			
		// -------------------------------------------------------------------
		// Reading template records
		// -------------------------------------------------------------------
		case TEMPLATE:
			logger.debug("+- TEMPLATE Set received");
			stats.numberOfTemplateSets++;
			iterator = new RecordIterator() {
				
				@Override
				public boolean hasNext() {
					if (next != null) {
						return true;
					}
					if (setBuffer.hasRemaining()) {
						try {
//							// TODO: it might be better to do it this way
//							next = new IpfixTemplateRecord(setBuffer);
//							templateManager.registerTemplateRecord(next);
							next = new IpfixTemplateRecord(
									IpfixSet.this.templateManager, setBuffer);
							return true;
						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
					}
					return false;
				}
			};
			break;
			
		// -------------------------------------------------------------------
		// Reading option template records
		// -------------------------------------------------------------------
		case OPTIONS_TEMPLATE:
			logger.debug("+- OPTION TEMPLSTE Set received");
			stats.numberOfOptionTemplateSets++;
			iterator = new RecordIterator() {
				@Override
				public boolean hasNext() {
					if (next != null) {
						return true;
					}
					if (setBuffer.hasRemaining()) {
						try {
							next = new IpfixOptionsTemplateRecord(
									IpfixSet.this.templateManager, setBuffer);
							return true;
						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
					}
					return false;
				}
			};
			break;
		default:
			break;
		}
	}

	public IpfixSetType getType() {
		return type;
	}

	@Override
	public Iterator<Object> iterator() {
		return iterator;
	}

	public IpfixSetHeader getHeader() {
		return header;
	}

	/**
	 * Generic abstract ipfix record iterator
	 */
	private static abstract class RecordIterator implements Iterator<Object> {
		protected Object last = null, next = null;

		@Override
		public final Object next() {
			if (next == null && !hasNext()) {
				throw new NoSuchElementException();
			}
			last = next;
			next = null;
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"Cannot remove records from set!");
		}
	}

	@Override
	public String toString() {
		return String.format("%s:{id:%d, len:%d}", type.getShortName(), header
				.getSetId(), header.getLength());
	}

}
