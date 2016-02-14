package com.github.justcoke.stubs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.codehaus.plexus.PlexusTestCase;

public abstract class AbstractTestMavenProjectStub extends MavenProjectStub {
	private Build build;

	/**
	 * Default
	 */
	public AbstractTestMavenProjectStub() {
		File antTestDir = new File(PlexusTestCase.getBasedir() + "/src/test/resources/unit/" + getProjectPath() + "/");

		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		Model model;

		try {
			File pomFile = new File(antTestDir, "pom.xml");
			// TODO: Once plexus-utils has been bumped to 1.4.4, use
			// ReaderFactory.newXmlReader()
			model = pomReader.read(new InputStreamReader(new FileInputStream(pomFile), "UTF-8"));
			setModel(model);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		setGroupId(model.getGroupId());
		setArtifactId(model.getArtifactId());
		setVersion(model.getVersion());
		setName(model.getName());
		setUrl(model.getUrl());
		setPackaging(model.getPackaging());

		build = new Build();
		Resource resource = new Resource();

		build.setFinalName(model.getArtifactId());
		build.setDirectory(getBasedir().getAbsolutePath() + "/target");

		build.setSourceDirectory(antTestDir + "/src/main/java");
		resource.setDirectory(antTestDir + "/src/main/resources");
		build.setResources(Collections.singletonList(resource));
		build.setOutputDirectory(getBasedir().getAbsolutePath() + "/target/classes");

		build.setTestSourceDirectory(antTestDir + "/src/test/java");
		resource = new Resource();
		resource.setDirectory(antTestDir + "/src/test/resources");
		build.setTestResources(Collections.singletonList(resource));
		build.setTestOutputDirectory(getBasedir().getAbsolutePath() + "/target/test-classes");

		setBuild(build);

		Reporting reporting = new Reporting();

		reporting.setOutputDirectory(getBasedir().getAbsolutePath() + "/target/site");

		getModel().setReporting(reporting);
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getBuild()
	 */
	@Override
	public Build getBuild() {
		return build;
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getBasedir()
	 */
	@Override
	public File getBasedir() {
		File basedir = new File(PlexusTestCase.getBasedir(), "/target/test/unit/" + getProjectPath() + "/");

		if (!basedir.exists()) {
			// noinspection ResultOfMethodCallIgnored
			basedir.mkdirs();
		}

		return basedir;
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getCompileSourceRoots()
	 */
	@Override
	public List<String> getCompileSourceRoots() {
		File src = new File(
				PlexusTestCase.getBasedir() + "/src/test/resources/unit/" + getProjectPath() + "src/main/java");
		return Collections.singletonList(src.getAbsolutePath());
	}

	@Override
	public List<Resource> getResources() {
		Resource resource = new Resource();
		resource.setDirectory(PlexusTestCase.getBasedir() + "/src/test/resources/unit/" + getProjectPath() + "src/main/resources");
		return Collections.singletonList(resource);
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getTestCompileSourceRoots()
	 */
	@Override
	public List getTestCompileSourceRoots() {
		File test = new File(
				PlexusTestCase.getBasedir() + "/src/test/resources/unit/" + getProjectPath() + "src/test/java");
		return Collections.singletonList(test.getAbsolutePath());
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getCompileArtifacts()
	 */
	@Override
	public List getCompileArtifacts() {
		Artifact junit = new DefaultArtifact("junit", "junit", VersionRange.createFromVersion("3.8.2"),
				Artifact.SCOPE_TEST, "jar", null, new DefaultArtifactHandler("jar"), false);
		junit.setFile(new File("junit/junit/3.8.2/junit-3.8.2.jar"));

		return Collections.singletonList(junit);
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getTestArtifacts()
	 */
	@Override
	public List getTestArtifacts() {
		Artifact junit = new DefaultArtifact("junit", "junit", VersionRange.createFromVersion("3.8.2"),
				Artifact.SCOPE_TEST, "jar", null, new DefaultArtifactHandler("jar"), false);
		junit.setFile(new File("junit/junit/3.8.2/junit-3.8.2.jar"));

		return Collections.singletonList(junit);
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getRepositories()
	 */
	@Override
	public List getRepositories() {
		Repository repo = new Repository();
		repo.setId("central");
		repo.setName("central");
		repo.setUrl("http://repo1.maven.org/maven2");

		return Collections.singletonList(repo);
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getProperties()
	 */
	@Override
	public Properties getProperties() {
		return getModel().getProperties();
	}

	/**
	 * @see org.apache.maven.project.MavenProject#getReporting()
	 */
	@Override
	public Reporting getReporting() {
		return getModel().getReporting();
	}

	/**
	 * @return the project path from <code>src/test/resources/unit</code>
	 *         directory
	 */
	public abstract String getProjectPath();
}