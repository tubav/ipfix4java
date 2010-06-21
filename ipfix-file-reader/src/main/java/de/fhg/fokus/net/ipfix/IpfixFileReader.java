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

import de.fhg.fokus.net.ipfix.api.Ipfix;
import de.fhg.fokus.net.ipfix.api.IpfixDataRecordReader;
import de.fhg.fokus.net.ipfix.api.IpfixDataRecordSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixDefaultTemplateManager;
import de.fhg.fokus.net.ipfix.api.IpfixFieldSpecifier;
import de.fhg.fokus.net.ipfix.api.IpfixHeader;
import de.fhg.fokus.net.ipfix.api.IpfixIe;
import de.fhg.fokus.net.ipfix.api.IpfixMessage;
import de.fhg.fokus.net.ipfix.api.IpfixOptionsTemplateRecord;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateRecord;
import de.fhg.fokus.net.ipfix.api.IpfixTemplateManager.Statistics;
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
public class IpfixFileReader implements Iterable<IpfixMessage> {
	// -- sys --
	private final Logger logger = LoggerFactory.getLogger(getClass());
	// -- constants --
	// -- model --
	private final File file;
	private final RandomAccessFile raf;
	private final FileChannel fileChannel;
	private final MappedByteBuffer fileBuffer;
	private boolean autoDispose = true;
	// -- management --
	private final IpfixDefaultTemplateManager templateManager = new IpfixDefaultTemplateManager();
	private final Statistics stats = templateManager.getStatistics();
	
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

			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				while (byteBuffer.hasRemaining()) {
					try {
						if (!IpfixMessage.align(byteBuffer )) {
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
						next = new IpfixMessage(IpfixFileReader.this.templateManager, hdr,
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
	public String toString() {
		return String.format("%s:{ %s }", file.getName(), stats);
	}
	public void registerDataRecordReader(IpfixDataRecordReader reader) {
		templateManager.registerDataRecordReader(reader);
	}
	public Statistics getStatistics() {
		return stats;
	}


}
