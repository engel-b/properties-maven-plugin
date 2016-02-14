package com.github.justcoke;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class GeneratorMojoTest extends AbstractMojoTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// MavenProjectStub !

	public void testJustMessage() throws Exception {
		File pom = getTestFile("src/test/resources/unit/generator-mojo/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());
		GeneratorMojo myMojo = (GeneratorMojo) lookupMojo("generate-properties", pom);
		assertNotNull(myMojo);
		myMojo.execute();
	}
}