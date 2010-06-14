package de.fhg.fokus.net.ipfix.mojo.mgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;

import com.thoughtworks.xstream.XStream;

import de.fhg.fokus.net.ipfix.mojo.mgen.tmpl.IpfixIeTmpl;

/**
 * 
 * @author FhG-FOKUS NETwork Research
 * 
 * 
 * @goal generate-ipfix-model
 * @phase generate-sources
 * 
 */
public class IpfixModelGeneratorMojo extends AbstractMojo {
	// -- parameters --
	/**
	 * @parameter expression="${generate-ipfix-model.targetPackage}"
	 *            default-value="de.fhg.fokus.net.ipfix.model"
	 */
	private String targetPackage;
	/**
	 * @parameter expression="${generate-ipfix-model.modelDir}"
	 *            default-value="model/"
	 * 
	 */
	private File modelDir;
	/**
	 * @parameter expression="${project.build.directory}"
	 * @required
	 * 
	 */
	private File buildDir;
	private File targetModelDir;
	// private org.apache.maven.plugin. mavenProject;

	// -- model --
	private final List<IpfixModelGenerator> modelGenerators = new ArrayList<IpfixModelGenerator>();
	private final XStream xstream;
	private TextTemplate txtTemplateIpfixIe;

	public IpfixModelGeneratorMojo() {
		// -- setup xml reader --
		xstream = new XStream();
		xstream.alias("registry", IanaRegistry.class);
		xstream.alias("record", IanaRecord.class);
		xstream.alias("xref", String.class);
		xstream.addImplicitCollection(IanaRegistry.class, "records",
				IanaRecord.class);
		xstream.addImplicitCollection(IanaRegistry.class, "children",
				IanaRegistry.class);
		xstream.addImplicitCollection(IanaRecord.class, "xrefs");
		xstream.aliasAttribute(IanaRegistry.class, "id", "id");
		// -- setup text templates --
		try {
			StringWriter writer = new StringWriter();
			IOUtil.copy(IpfixIeTmpl.class.getResourceAsStream(IpfixIeTmpl.FILE.getName()),
					writer);
			txtTemplateIpfixIe = new TextTemplate(writer.toString());
		} catch (IOException e) {
			throw new RuntimeException("Error loading template:"+IpfixIeTmpl.FILE.toString() );
		}

	}

	private void checkPreconditions() throws MojoFailureException {
		if (txtTemplateIpfixIe == null) {
			throw new MojoFailureException(
			"Could not initialize ipfix ie txt template file!");
		}
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		checkPreconditions();
		loadModels();
		setupGeneratedSourcesDir();
	}

	private final FilenameFilter xmlFilenameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".xml");
		}
	};

	/**
	 * Search for ipfix xml model files. Format
	 * http://www.iana.org/assignments/ipfix/ipfix.xml
	 * 
	 * @throws MojoFailureException
	 */
	private void loadModels() throws MojoFailureException {
		getLog().info("== LOAD IPFIX MODELS ==");

		if (!modelDir.exists()) {
			throw new MojoFailureException(
					"IPFIX model directory does not exist!: "
					+ modelDir.getAbsolutePath());
		}
		getLog().info("Searching models in " + modelDir.getAbsolutePath());
		for (File file : modelDir.listFiles(xmlFilenameFilter)) {
			if (file.isFile() && file.canRead()) {
				getLog().info("Found model: " + file.getName());
				try {
					FileInputStream fis = new FileInputStream(file);
					IanaRegistry registry = (IanaRegistry) xstream.fromXML(fis);
					modelGenerators.add(new IpfixIeModelGenerator(
							txtTemplateIpfixIe, registry, targetPackage, targetModelDir));
				} catch (Exception e) {
					e.printStackTrace();
					getLog().error(e.getMessage());
				}
			}
		}
	}

	private void setupGeneratedSourcesDir() {
		getLog().info("== SETUP GENERATED SOURCES ==");
		// -- setup dirs --
		targetModelDir = new File(buildDir, String.format(
				"generated-sources%s%s", File.separator, targetPackage.replace(
						".", File.separator)));
		if (targetModelDir.mkdirs()) {
			getLog().info("Creating " + targetModelDir);
		}
		getLog().info("TARGET_MODEL_DIR: " + targetModelDir);

	}

	public static void main(String[] args) {
		// xstream quick test
		XStream xstream = new XStream();
		xstream.alias("registry", IanaRegistry.class);
		xstream.alias("record", IanaRecord.class);
		xstream.addImplicitCollection(IanaRegistry.class, "records",
				IanaRecord.class);
		IanaRegistry reg = new IanaRegistry();
		reg.id = "1";
		reg.records = new ArrayList<IanaRecord>();
		reg.records.add(new IanaRecord("test"));
		reg.records.add(new IanaRecord("test"));

		System.out.println(xstream.toXML(reg));

	}
}
