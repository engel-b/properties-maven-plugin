package com.github.justcoke;

public class PropertyFileConfiguration {
	private String destinationClass;
	private String destinationPackage;
	private String interfaces;
	private String source;
	private String superClass;

	public String getDestinationClass() {
		return destinationClass;
	}

	public String getDestinationPackage() {
		return destinationPackage;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public String getSource() {
		return source;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setDestinationClass(String destinationClass) {
		this.destinationClass = destinationClass;
	}

	public void setDestinationPackage(String destinationPackage) {
		this.destinationPackage = destinationPackage;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}
}
