package com.clearwire.tools.mobile.aiat.fragments;

public class Stat implements Comparable<Stat>{
	String name;
	String value;
	
	public Stat(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Stat another) {
		return name.compareTo(another.getName());
	}
}