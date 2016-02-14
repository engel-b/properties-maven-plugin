package com.github.justcoke;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Property implements Comparable<Property> {
	String description;
	String name;
	String value;

	public Property(final String name, final String value, final String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	@Override
	public int compareTo(final Property other) {
		return new CompareToBuilder().append(name, other.name).toComparison();
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
