package xml_parser;

import java.util.ArrayList;

public class XMLNode {
	public ArrayList<XMLNode> children = new ArrayList<XMLNode>();
	public int ID;
	public String text;
	public XMLNode parent;
	public int node_type;

	public XMLNode(int ID, String text, int node_type){
		this.ID = ID;
		this.text = text;
		this.node_type = node_type;		//0 = tag 1 = leaf 2 = comment
	}

	public int getNumChildren(){
		return children.size();
	}

	public void addChild(XMLNode node){
		children.add(node);
	}

	public void removeChild(int c_ID){
		for(int ii = children.size()-1; ii >= 0; ii--){
			if(children.get(ii).ID == c_ID)
				children.remove(ii);
		}
	}

	public String print(){
		String str = String.format("NODE #%d - %s\n\tChildren: ", ID, text);
		for(int ii = 0; ii < children.size(); ii++){
			str += String.format("%d ", children.get(ii).ID);
		}
		return str;
	}
}
