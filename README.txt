IPFIX File Reader 
=================

Requirements
------------
- maven            http://maven.apache.org/ for building
- slf4j            SLF4J (http://www.slf4j.org/) and a logging configured,
                   ex:  slf4j-api-1.5.11.jar, logback-core-0.9.18.jar,
                        logback-classic-0.9.18.jar
- java 6          

Contents
---------
ipfix-api          Java API for dealing with ipfix messages

ipfix-file-reader  IPFIX File reader

Creating jars
-------------

svn update
mvn package

# and look for them at

ipfix-api/target/
ipfix-file-reader/target


USAGE
-----
PrismBackendIpfixBufferReader prismReader = 
     new PrismBackendIpfixBufferReader( new File("path/to/file.ipfix"));

for( IpfixMessage msg : prismReader ){
   for(IpfixSet set: msg ){
      for(IpfixRecord record: set ){
	logger.info(record+"");
      }
   }
}

See PrismBackendIpfixBufferReaderTest for more information.

HTH
--//--
