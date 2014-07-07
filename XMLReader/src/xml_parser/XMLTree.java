package xml_parser;

import java.util.ArrayList;

public class XMLTree {
	private ArrayList<XMLNode> rootList = new ArrayList<XMLNode>();

	private XMLNode currentXMLNode = null;

	private String version;
	private String encoding;
	private String CDATA;


	public void addNode(XMLNode node){
		if(currentXMLNode == null){
			rootList.add(node);
			currentXMLNode = node;
		} else{
			currentXMLNode.addChild(node);
			node.parent = currentXMLNode;
			currentXMLNode = node;
		}
	}

	public boolean stepUp(){
		if(currentXMLNode == null || currentXMLNode.parent == null)
			return false;
		currentXMLNode = currentXMLNode.parent;
		return true;
	}

	public void setVersion(String version){
		this.version = version;
	}
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	public String getVersion(){
		return version;
	}
	public String getEncoding(){
		return encoding;
	}
	public void setCDATA(String CDATA){
		this.CDATA = CDATA;
	}
	public String getCDATA(){
		return CDATA;
	}
}
