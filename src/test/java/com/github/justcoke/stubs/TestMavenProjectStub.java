package com.github.justcoke.stubs;

import java.util.Properties;

public class TestMavenProjectStub extends AbstractTestMavenProjectStub {
	@Override
	public String getProjectPath() {
		return "generator-mojo";
	}

	@Override
	public Properties getProperties() {
		return getModel().getProperties();
	}
}