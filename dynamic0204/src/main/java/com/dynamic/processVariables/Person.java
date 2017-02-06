package com.dynamic.processVariables;

import java.io.Serializable;

public class Person implements Serializable {

	private static final long serialVersionUID = 7626677901372845770L;

	private Integer id;
	
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + "]";
	}

	
	
	
	
	
}
