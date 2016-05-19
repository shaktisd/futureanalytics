package org.demo.nlp;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String value;
	private List<Node> children = new ArrayList<Node>();
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}
	
	

}
