package xml_parser;

import java.util.ArrayList;

public class XMLTree {
	private ArrayList<XMLNode> rootList = new ArrayList<XMLNode>();

	private XMLNode currentXMLNode = null;

	private String version;
	private String encoding;
	private String CDATA;

	public ArrayList<XMLNode> findNodes(String node_name){
		ArrayList<XMLNode> nodes = new ArrayList<XMLNode>();
		for(int ii = 0; ii < rootList.size(); ii++){
			nodes = recusiveFind(node_name, nodes, rootList.get(ii));
		}
		return nodes;
	}

	private ArrayList<XMLNode> recusiveFind(String node_name, ArrayList<XMLNode> nodes, XMLNode curNode){
		if(curNode.text.equals(node_name))
			nodes.add(curNode);
		for(int ii = 0; ii < curNode.children.size(); ii++){
			nodes = recusiveFind(node_name, nodes, curNode.children.get(ii));
		}
		return nodes;
	}

	public void addNode(XMLNode node){
		if(currentXMLNode == null){
			node.parent = null;
			rootList.add(node);
			currentXMLNode = node;
		} else{
			currentXMLNode.addChild(node);
			node.parent = currentXMLNode;
			currentXMLNode = node;
		}
	}

	public void removeNode(XMLNode node){
		XMLNode parent = node.parent;
		ArrayList<XMLNode> children = node.children;

		if(parent != null){
			parent.removeChild(node.ID);
			for(int ii = 0; ii < children.size(); ii++){
				children.get(ii).parent = parent;
				parent.children.add(children.get(ii));
			}
		} else{
			for(int ii = 0; ii < children.size(); ii++){
				children.get(ii).parent = null;
				rootList.add(children.get(ii));
			}
		}
	}

	public void removeAllOfNode(String name){
		ArrayList<XMLNode> nodes = findNodes(name);
		for(int ii = 0; ii < nodes.size(); ii++)
			removeNode(nodes.get(ii));
	}

	public boolean stepUp(){
		if(currentXMLNode == null)
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

	public String getCurNodeName(){
		return currentXMLNode.text;
	}

	public void print(){
		System.out.println("Printing Tree");
		System.out.println("Version = " + version);
		System.out.println("Encoding = " + encoding);
		System.out.println("CDATA = " + CDATA);

		System.out.println("\nNodes:");
		for(int ii = 0;ii < rootList.size(); ii++){
			rootList.get(ii).printAll();			
		}

	}
}
