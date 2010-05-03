package de.fhg.fokus.net.ipfix;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixDataRecordSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixHeader;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixOptionsTemplateRecord;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateRecord;
import de.fhg.fokus.net.ipfix.util.ByteBufferUtil;

/**
 * IPFIX file reader implementation (on going). It uses memory mapped files and
 * is currently limited to files of size Integer.MAX_VALUE ( 2^31 -1) bytes.
 * 
 * TODO use IpfixDefaultTemplateManager
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixFileReader implements Iterable<IpfixMessage>,
IpfixTemplateManager {
	// -- sys --
	private final Logger logger = LoggerFactory.getLogger(getClass());
	// -- constants --
	private final static short IPFIX_SUPPORTED_VERSION = 0x000a;
	// -- model --
	private final File file;
	private final RandomAccessFile raf;
	private final FileChannel fileChannel;
	private final MappedByteBuffer fileBuffer;
	private final Statistics stats = new Statistics();
	private boolean autoDispose = true;
	// -- management --
	private final Map<String, IpfixIe> mapFieldIe = new ConcurrentHashMap<String, IpfixIe>();
	private final Map<Integer, IpfixDataRecordReader> mapSetIdRecordReader = new ConcurrentHashMap<Integer, IpfixDataRecordReader>();
	private final Map<String, IpfixDataRecordReader> mapTemplateUidRecordReader = new ConcurrentHashMap<String, IpfixDataRecordReader>();
	private final Map<Integer, IpfixDataRecordSpecifier> mapSetId2DataRecordSpecifier = new ConcurrentHashMap<Integer, IpfixDataRecordSpecifier>();

	public IpfixFileReader(File file) throws IOException {
		this.file = file;
		this.raf = new RandomAccessFile(file, "r");
		this.fileChannel = this.raf.getChannel();
		this.fileBuffer = this.fileChannel.map(MapMode.READ_ONLY, 0, file
				.length());
		logger.debug("File length: {}", file.length());
	}
	/**
	 * Auto dispose resources (e.g. close underlying file) after finishing iterating over messages;
	 * 
	 * @param autoDispose
	 * @return
	 */
	public IpfixFileReader setAutoDispose( boolean autoDispose ){
		this.autoDispose = autoDispose;
		return this;
	}
	/**
	 * Close file and release any resources used by the reader
	 */
	public void dispose(){
		try {
			logger.debug("Releasing resources, file: {} ",file);
			this.raf.close();
		} catch (IOException e) {
			logger.error("Could not release resources!");
			logger.equals(e.toString());
		}
	}
	public File getFile() {
		return file;
	}

	/**
	 * Iterate over IPFIX messages in file.
	 */
	public Iterator<IpfixMessage> iterator() {

		return new Iterator<IpfixMessage>() {
			// iterators are independent
			private final ByteBuffer byteBuffer = fileBuffer.slice();
			private IpfixMessage next = null, last = null;

			/**
			 * Align byte buffer and skipping invalid data.
			 * 
			 */
			private boolean aligned() {
				// two fingers alignment
				int pos = byteBuffer.position();
				int msg1_version = ByteBufferUtil.getUnsignedShort(byteBuffer,
						pos + IpfixHeader.IDX_VERSION);
				// first finger
				if (msg1_version == IPFIX_SUPPORTED_VERSION) {
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
								+ msg1_length + IpfixHeader.IDX_VERSION) == IPFIX_SUPPORTED_VERSION;
					}
					return true;
				}

				return false;

			}

			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				while (byteBuffer.hasRemaining()) {
					try {
						if (!aligned()) {
							byte b = byteBuffer.get();
							stats.invalidBytes++;
							logger
							.warn(
									"Invalid data, skipping byte: 0x{}:0x{} ",
									Integer.toHexString(byteBuffer
											.position() - 1), Integer
											.toHexString(0xff & b));
							continue;
						}
						IpfixHeader hdr = new IpfixHeader(byteBuffer);
						next = new IpfixMessage(IpfixFileReader.this, hdr,
								byteBuffer);

					} catch (Exception e) {
						byte b = byteBuffer.get();
						stats.invalidBytes++;
						logger
						.warn(
								"Invalid/malformed data, skipping byte: 0x{}:0x{} ",
								Integer.toHexString(byteBuffer
										.position() - 1), Integer
										.toHexString(0xff & b));
						continue;
					}
					return true;
				}
				if(autoDispose){
					dispose();
				}
				return false;
			}

			public IpfixMessage next() {
				if (next == null && !hasNext()) {
					throw new NoSuchElementException();
				}
				last = next;
				next = null;
				stats.numberOfMessages++;
				return last;
			}

			public void remove() {
				throw new UnsupportedOperationException(
				"Cannot modify IPFIX file!");
			}

		};
	}

	@Override
	public IpfixDataRecordReader getDataRecordReader(int setId) {
		IpfixDataRecordReader reader = mapSetIdRecordReader.get(setId);
		// logger.debug("Getting data record reader:{} setId:{}",reader,setId);
		return reader;
	}

	@Override
	public void registerDataRecordReader(IpfixDataRecordReader reader) {
		logger.debug("registering data record reader: {}", reader.getTemplate()
				.getUid());
		for (IpfixIe ie : reader.getTemplate().getInformationElements()) {
			logger.debug(" +-registering ie: {}, {}", ie.getName(), ie
					.getFieldSpecifier());
			mapFieldIe.put(ie.getFieldSpecifier().toString(), ie);
		}
		mapTemplateUidRecordReader.put(reader.getTemplate().getUid(), reader);

	}

	@Override
	public IpfixIe getInformationElement(IpfixFieldSpecifier fieldSpecifier) {
		return mapFieldIe.get(fieldSpecifier.toString());
	}

	@Override
	public void registerTemplateRecord(IpfixTemplateRecord ipfixTemplateRecord) {
		stats.numberOfTemplateRecords++;
		IpfixDataRecordReader reader = mapTemplateUidRecordReader
		.get(ipfixTemplateRecord.getUid());
		int setId = ipfixTemplateRecord.getTemplateId();
		mapSetId2DataRecordSpecifier.put(setId, ipfixTemplateRecord);
		if (reader == null) {
			logger.debug("No IPFIX data record reader was found for {}.",
					ipfixTemplateRecord.getUid());
			return;
		}
		logger.debug("registering ipfix template record: {}, reader:{}",
				ipfixTemplateRecord, reader);
		mapSetIdRecordReader.put(setId, reader);

	}

	// TODO unify this
	@Override
	public void registerOptionsTemplateRecord(
			IpfixOptionsTemplateRecord ipfixOptionsTemplateRecord) {
		stats.numberOfOptionTemplateRecords++;
		int setId = ipfixOptionsTemplateRecord.getTemplateId();
		IpfixDataRecordReader reader = mapTemplateUidRecordReader
		.get(ipfixOptionsTemplateRecord.getUid());
		mapSetId2DataRecordSpecifier.put(setId, ipfixOptionsTemplateRecord);
		if (reader == null) {
			logger.debug(
					"No IPFIX (options) data record reader was found for {}.",
					ipfixOptionsTemplateRecord.getUid());
			return;
		}
		logger.debug(
				"registering ipfix options template record: {}, reader:{}",
				ipfixOptionsTemplateRecord, reader);
		mapSetIdRecordReader.put(setId, reader);
	}

	@Override
	public IpfixDataRecordSpecifier getDataRecordSpecifier(int setId) {
		return mapSetId2DataRecordSpecifier.get(setId);
	}

	@Override
	public String toString() {
		return String.format("%s:{ %s }", file.getName(), stats);
	}

	@Override
	public Statistics getStatistics() {
		return stats;
	}

}
