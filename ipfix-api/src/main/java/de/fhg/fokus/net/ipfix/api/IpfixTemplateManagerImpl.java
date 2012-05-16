package de.fhg.fokus.net.ipfix.api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IPFIX Template Manager implementation
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 */
public class IpfixTemplateManagerImpl implements IpfixTemplateManager {
	// -- sys --
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Statistics stats;
	
	// -- management --
	// ie map
	private final Map<IpfixFieldSpecifier, IpfixIe> mapFieldIe;

	// map set id to user record reader
	private final Map<Integer, IpfixDataRecordReader> mapRecordReader;

	// map set id to received template records
	private final Map<Integer, IpfixDataRecordSpecifier> mapRcvdTemlateRecord;

	// store user record reader
	private final Set<IpfixDataRecordReader> listRecordReader;

	// store parent template manager
	private final IpfixTemplateManagerImpl parentTM;
	
	// count number of template manager instance
	private int numberOfInstances = 0;

	public IpfixTemplateManagerImpl() {
		parentTM = null;
		stats = new Statistics();

		mapFieldIe = new ConcurrentHashMap<IpfixFieldSpecifier, IpfixIe>();
		// map set id to user record reader
		mapRecordReader = new ConcurrentHashMap<Integer, IpfixDataRecordReader>();
		// map set id to received template records
		mapRcvdTemlateRecord = new ConcurrentHashMap<Integer, IpfixDataRecordSpecifier>();
		// store user record reader
		listRecordReader = new CopyOnWriteArraySet<IpfixDataRecordReader>();
	}
	
	/**
	 * used for getInstance method
	 * 
	 * @param stats
	 * @param mapFieldIe
	 * @param listRecordReaders
	 */
	private IpfixTemplateManagerImpl( IpfixTemplateManagerImpl tm ) {
		this.parentTM = tm;
		this.stats = tm.stats;
		this.mapFieldIe = tm.mapFieldIe;
		this.listRecordReader = tm.listRecordReader;
		
		// create new template id maps for new connections
		// map set id to user record reader
		mapRecordReader = new ConcurrentHashMap<Integer, IpfixDataRecordReader>();
		// map set id to received template records
		mapRcvdTemlateRecord = new ConcurrentHashMap<Integer, IpfixDataRecordSpecifier>();
		
		this.parentTM.incrementTmCounter();
	}
	
	private synchronized void incrementTmCounter() {
		++this.numberOfInstances;
	}
	
	private synchronized void decrementTMCounter() {
		--this.numberOfInstances;
	}
	
	protected void finalize( ) throws Throwable {
		if( null != this.parentTM) {
			this.parentTM.decrementTMCounter();
		}
		super.finalize( );
	}
	
	/**
	 * a new class that shares {@link Statistics}, Map<IpfixFieldSpecifier, IpfixIe> and
	 * Set<IpfixDataRecordReader>
	 * 
	 * @return {@link IpfixTemplateManagerImpl}
	 */
	@Override
	public IpfixTemplateManager getInstance() {
		IpfixTemplateManager tm = new IpfixTemplateManagerImpl(this);
		logger.debug("[IpfixTemplateManager] new instance: number of childs ({})", 
				this.numberOfInstances);
		return tm;
	}
	

	@Override
	public IpfixDataRecordSpecifier getDataRecordSpecifier(int setId) {
		return mapRcvdTemlateRecord.get(setId);
	}

	@Override
	public IpfixDataRecordReader getDataRecordReader(int setId) {
		IpfixDataRecordReader reader = mapRecordReader.get(setId);
		// logger.debug("Getting data record reader:{} setId:{}",reader,setId);
		return reader;
	}

	@Override
	public IpfixIe getInformationElement(IpfixFieldSpecifier fieldSpecifier) {
		return mapFieldIe.get(fieldSpecifier);
	}

	@Override
	public Statistics getStatistics() {
		return stats;
	}

	@Override
	// register the user data record reader
	// to mapTemplateUidRecordReader
	// put template ie's to mapFieldIe
	public void registerDataRecordReader(IpfixDataRecordReader reader) {
		logger.debug( "registering data record reader: {} IE's"
					, reader.getTemplate().getInformationElements().length);
		
		// put the template in a list (or the reader)
		if( listRecordReader.add(reader) ) {
			for (IpfixIe ie : reader.getTemplate().getInformationElements()) {
				logger.debug(" +-registering ie: {}, {}"
							, ie.getName()
							, ie.getFieldSpecifier());
				if( null != mapFieldIe.put(ie.getFieldSpecifier(), ie) ) {
					logger.trace(" +--ie already exist in ie collection ({})", mapFieldIe.size());
				}
			}
		}
		else {
			logger.info(" + record reader already exist: {}", reader.getTemplate().getUid());
		}
	}

	@Override
	// register received template records
	// and connect them to user record reader
	// TODO: you must rewrite this
	public void registerTemplateRecord(IpfixTemplateRecord templateRecord) {
		stats.numberOfTemplateRecords++;

		// add received template record to template map
		mapRcvdTemlateRecord.put(templateRecord.getTemplateId(), templateRecord);

		// get the user record reader for the received template (with best fit)
		IpfixDataRecordReader reader = getRecordReader(templateRecord);

		if (reader == null) {
			logger.debug("No IPFIX data record reader was found for {}.", templateRecord.getUid());
		}
		else {
			logger.debug("registering ipfix template -> reader: {}, record: {}", reader, templateRecord);
			mapRecordReader.put(templateRecord.getTemplateId(), reader);
		}

	}

	private IpfixDataRecordReader getRecordReader(IpfixTemplateRecord templateRecord) {
		IpfixDataRecordReader foundReader = null;
		int matchedIEs = 0;
		// find record reader for the given template record
		// with its included ie's
		// compared over its field specifier (IpfixFieldSpecifier - Comparable)
		for( IpfixDataRecordReader reader : listRecordReader ) {
			int tmpIEs = 0;
			logger.debug(" +-process record Reader: {}", reader);
			
			// look up record reader for the given field specifier
			for( IpfixFieldSpecifier fs : templateRecord.getFieldSpecifiers()) {
				if( true == reader.getTemplate().hasFieldSpecifier(fs) ) {
					logger.trace( "  +- found ie: {}", fs );
					++tmpIEs;
				}
			}
			
			// make best fit for matched ie's
			if( tmpIEs > matchedIEs ) {
				foundReader = reader;
				matchedIEs = tmpIEs;
			}
			
			// stop searching if all ie's have been found
			// TODO: this cause reader having more ie's than received record
			if( matchedIEs == templateRecord.getFieldSpecifiers().size() ) break;
		}
		if( foundReader.getTemplate().getInformationElements().length > matchedIEs ) {
			logger.warn("reader expect more information elements: {} > {}"
						, foundReader.getTemplate().getInformationElements().length
						, matchedIEs);
			logger.warn("+ reader: {} - {}", foundReader, foundReader.getTemplate().getUid());
			logger.warn("+ record: {}", templateRecord);
		}
		if( templateRecord.getFieldCount() > matchedIEs ) {
			logger.warn("record has more information elements");
			logger.warn("+ reader: {} - {}", foundReader, foundReader.getTemplate().getUid());
			logger.warn("+ record: {}", templateRecord);
		}
		return foundReader;
	}

	// TODO unify this
	@Override
	public void registerOptionsTemplateRecord(IpfixOptionsTemplateRecord templateRecord) {
		stats.numberOfOptionTemplateRecords++;
		
		// add received options template record
		mapRcvdTemlateRecord.put(templateRecord.getTemplateId(), templateRecord);

		IpfixDataRecordReader reader = getRecordReader(templateRecord);
		if (reader == null) {
			logger.debug("No IPFIX (options) data record reader was found for {}.", templateRecord.getUid());
		}
		else {
			logger.debug("registering ipfix options template record: {}, reader:{}", templateRecord, reader);
			mapRecordReader.put(templateRecord.getTemplateId(), reader);
		}
	}

	private IpfixDataRecordReader getRecordReader(IpfixOptionsTemplateRecord templateRecord) {
		// TODO Auto-generated method stub
		return null;
	}

}
