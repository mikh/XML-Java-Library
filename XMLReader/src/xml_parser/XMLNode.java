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
		this.node_type = node_type;		//0 = tag 1 = leaf 2 = comment 3 = attribute
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

	public void printAll(){
		String str = String.format("NODE #%d - %s\n\tChildren: ", ID, text);
		for(int ii = 0; ii < children.size(); ii++){
			str += String.format("%d ", children.get(ii).ID);
		}
		System.out.println(str + "\n");
		for(int ii = 0; ii < children.size(); ii++){
			children.get(ii).printAll();
		}
	}

	public ArrayList<Integer> hasParent(String name){
		ArrayList<Integer> parent_index = new ArrayList<Integer>();
		XMLNode parent_node = parent;
		int index = 1;
		while(parent_node != null){
			if(parent_node.text.equals(name))
				parent_index.add(index++);
			parent_node = parent_node.parent;
		}
		return parent_index;
	}

	public boolean isRoot(){
		if(parent == null)
			return true;
		return false;
	}

	public XMLNode getParent(String name, XMLNode start_node){
		XMLNode parent_node = start_node.parent;
		if(parent_node != null){
			 if(parent_node.text.equals(name))
			 	return parent_node;
			 else
			 	return getParent(name, parent_node);
		} else
			return null;
	}

	public XMLNode getChild(String name, XMLNode start_node){
		for(int ii = 0; ii < start_node.children.size(); ii++){
			XMLNode child = start_node.children.get(ii);
			if(child.text.equals(name))
				return child;
			else{
				child = getChild(name, child);
				if(child != null)
					return child;
			}
		}
		return null;
	}

	public ArrayList<String> getLeaves(XMLNode node){
		ArrayList<String> leaves = new ArrayList<String>();
		for(int ii = 0; ii < node.children.size(); ii++){
			XMLNode child = node.children.get(ii);
			if(child.node_type == 1)
				leaves.add(child.text);
		}
		return leaves;
	}
}
