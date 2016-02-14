package com.github.justcoke;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.IllegalCharsetNameException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

@Mojo(name = "generate-properties", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresOnline = false, requiresProject = true, threadSafe = false)
public class GeneratorMojo extends AbstractMojo {

	@Parameter(property = "encoding", defaultValue = "UTF-8", required = true)
	protected String encoding;

	@Parameter(property = "failOnError", defaultValue = "true", required = true)
	protected boolean failOnError;

	@Parameter(property = "verbose", defaultValue = "false", required = true)
	protected boolean verbose;

	@Parameter(property = "propertyFiles", required = true)
	protected List<PropertyFileConfiguration> propertyFiles;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	private static Pattern pattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

	private String targetFolder = "/generated-sources/properties";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		logConfiguration();

		targetFolder = project.getBuild().getDirectory() + targetFolder;

		for (PropertyFileConfiguration propertyFileConfiguration : propertyFiles) {
			processPropertyFileConfiguration(propertyFileConfiguration);
		}
	}

	private void processPropertyFileConfiguration(final PropertyFileConfiguration propertyFileConfiguration) {
		try {
			getLog().info(MessageFormat.format("Source: {0}", propertyFileConfiguration.getSource()));
			getLog().info(MessageFormat.format("Destination: {0}/{1}/{2}", targetFolder, propertyFileConfiguration.getDestinationPackage(), propertyFileConfiguration.getDestinationClass()));
			normalize(propertyFileConfiguration);

			final List<Property> propertyKeys = processPropertyFile(propertyFileConfiguration);

			Collections.sort(propertyKeys);
			generatePropertyClass(propertyFileConfiguration, propertyKeys);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Property> processPropertyFile(PropertyFileConfiguration propertyFileConfiguration) throws FileNotFoundException, IOException {
		int countSucceed = 0;
		int countFailed = 0;
		List<Property> propertyKeys = new ArrayList<Property>();

		getLog().info(MessageFormat.format("Processing {0}", propertyFileConfiguration.getSource()));

		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(propertyFileConfiguration.getSource())));

		for (Map.Entry propertyEntry : properties.entrySet()) {
			String unconverted = (String) propertyEntry.getKey();
			String converted = convertKey(unconverted);

			if (!(pattern.matcher(converted).matches())) {
				countFailed += 1;
				String errorMessage = MessageFormat.format("Property \"{0}\" kann nicht konvertiert werden", unconverted);
				getLog().error(errorMessage);
				if (this.failOnError) {
					throw new IllegalCharsetNameException(errorMessage);
				}
			} else {
				countSucceed += 1;
				propertyKeys.add(new Property(converted, unconverted, propertyEntry.getValue().toString()));
			}
		}
		getLog().info(MessageFormat.format("Finished. Properties succeed={0}, failed={1}", countSucceed, countFailed));
		return propertyKeys;
	}

	private void normalize(PropertyFileConfiguration propertyFileConfiguration) throws FileNotFoundException {
		propertyFileConfiguration.setDestinationPackage(propertyFileConfiguration.getDestinationPackage().replace("/", ".").replace("\\", "."));
	}

	private String convertKey(final String unconverted) {
		String converted = unconverted.replace('.', '_').replace('-', '_').toUpperCase();

		return converted;
	}

	private void logConfiguration() {
		getLog().info("-------------------------------------------------------");
		getLog().info(" Generating property-classes");
		getLog().info("-------------------------------------------------------");
		getLog().info("encoding: " + this.encoding);
		getLog().info("failOnError: " + this.failOnError);
		project.getBasedir();
	}

	private void logOutput(final String logOutput) {
		if (verbose) {
			System.out.println(logOutput);
		}
	}

	private Writer getOutputFileStream(final File file) throws IOException {
		if (file.exists()) {
			file.delete();
		}

		file.getParentFile().mkdirs();
		file.createNewFile();

		return new OutputStreamWriter(new FileOutputStream(file), encoding);
	}

	private void generatePropertyClass(PropertyFileConfiguration propertyFileConfiguration, List<Property> properties) {
		try {
			Properties props = new Properties();
			URL url = super.getClass().getClassLoader().getResource("velocity.properties");
			props.load(url.openStream());

			VelocityEngine ve = new VelocityEngine(props);
			ve.init();

			VelocityContext vc = new VelocityContext();

			vc.put("packagename", propertyFileConfiguration.getDestinationPackage());
			vc.put("classname", propertyFileConfiguration.getDestinationClass());
			vc.put("superClass", propertyFileConfiguration.getSuperClass());
			vc.put("interfaces", propertyFileConfiguration.getInterfaces());
			vc.put("properties", properties);

			Template vt = ve.getTemplate("Properties.vm", encoding);

			String completePath = MessageFormat.format("{0}/{1}/{2}s.java", targetFolder, propertyFileConfiguration.getDestinationPackage().replace(".", "/"),
					propertyFileConfiguration.getDestinationClass());
			getLog().info(completePath);
			Writer writer = getOutputFileStream(new File(completePath));

			System.out.println("applying velocity template: " + vt.getName());

			vt.merge(vc, writer);

			writer.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}