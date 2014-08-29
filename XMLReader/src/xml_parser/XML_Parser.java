package xml_parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import string_operations.StrOps;

public class XML_Parser {
	ArrayList<String> opening_sequences = new ArrayList<String>();
	ArrayList<String> closing_sequences = new ArrayList<String>();
	XMLTree tree;
	private int ID;
	
	
	/**
	 * XML_Parser constructor. Takes no arguments. Sets up initial values for XML parser
	 */
	public XML_Parser(){
		opening_sequences.add("<");
		closing_sequences.add(">");
		opening_sequences.add("\"");
		closing_sequences.add("\"");
		opening_sequences.add("\'");
		closing_sequences.add("\'");
		opening_sequences.add(">");
		closing_sequences.add("<");
	}
	
	/**
	 * Initializes tree walk and returns the root list of the tree
	 * @return 
	 */
	public ArrayList<XMLNode> startTreeWalk(){
		if(tree == null)
			return null;
		else
			return tree.startTreeWalk();
	}
	
	public void setWalkNode(XMLNode node){
		if(tree != null)
			tree.setWalkNode(node);
	}
	
	public ArrayList<ArrayList<XMLNode>> getNodeData(){
		if(tree != null){
			return tree.getNodeData();
		}
		return null;
	}
	
	public String getLeaf(XMLNode node){
		for(int ii = 0; ii < node.children.size(); ii++){
			if(node.children.get(ii).node_type == 1)
				return node.children.get(ii).text;
		}
		return null;
	}
	
	public ArrayList<ArrayList<String>> getAttributes(XMLNode node){
		if(node.node_type == 0){
			ArrayList<String> attribute = new ArrayList<String>();
			ArrayList<String> value = new ArrayList<String>();
			for(int ii = 0; ii < node.children.size(); ii++){
				if(node.children.get(ii).node_type == 3){
					attribute.add(node.children.get(ii).text);
					ArrayList<String> leaf = node.children.get(ii).getLeaves(node.children.get(ii));
					if(leaf.size() > 0)
						value.add(leaf.get(0));
					else
						value.add("");
				}
			}
			ArrayList<ArrayList<String>> pack = new ArrayList<ArrayList<String>>();
			pack.add(attribute);
			pack.add(value);
			return pack;
		}
		return null;
	}
	
	
	/**
	 * Parses the given XML File. The results of the parsing are loaded into a XMLTree variable.
	 * @param XMLFile - path to the XML file.
	 */
	public void parseXML(String XMLFile){
		tree = new XMLTree();
		String file = readFile(XMLFile);
		int index = 0;
		ID = 0;
		
		String line = StrOps.getNextSection(file, opening_sequences, closing_sequences, index);
		while(line != null){
			index = updateIndex(file, line, index);
			parseSection(line, tree);
			line = StrOps.getNextSection(file, opening_sequences, closing_sequences, index);
		}
/*
		System.out.println("\n\nSEARCH TEST:\n");
		ArrayList<String> search_parameters = new ArrayList<String>();
		ArrayList<ArrayList<String>> parameter_values = new ArrayList<ArrayList<String>>();
		ArrayList<XMLNode> nodes = search("TEST", search_parameters, parameter_values);
		for(int ii = 0; ii < nodes.size(); ii++){
			ArrayList<String> leaves = nodes.get(ii).getLeaves(nodes.get(ii));
			System.out.println(nodes.get(ii).text + ":");
			for(int kk = 0; kk < leaves.size(); kk++){
				System.out.println(leaves.get(kk));
			}
			System.out.println("\n");
		}

		search_parameters.add("has_parent");
		parameter_values.add(new ArrayList<String>());
		parameter_values.get(0).add("String");
		nodes = search("TEST", search_parameters, parameter_values);
		for(int ii = 0; ii < nodes.size(); ii++){
			ArrayList<String> leaves = nodes.get(ii).getLeaves(nodes.get(ii));
			System.out.println(nodes.get(ii).text + ":");
			for(int kk = 0; kk < leaves.size(); kk++){
				System.out.println(leaves.get(kk));
			}
			System.out.println("\n");
		}
		*/
		
	}

	/**
	 * Searches the parsed XML tree given a text string and a series of search parameters.
	 * <p>
	 *	Search parameters can be:
	 *		has_parent - search for all nodes that have a parent given in the parameter_values
	 *			if parameter_values contain just the name, then the parent can be anywhere in the heirarchy
	 *			if there are additional values, the parent has to be at that position away from the child.
	 *			For example, if the parameter_values entry contains:    "parent_name", "2", "3", "4"
	 *			Then the 2nd, 3rd, OR 4th parent has to be identified as "parent_name" (0 indicates the child, 1 is the direct parent, etc.)
	 *			If you wish to have "parent_name" as both the 2nd and 3rd entry, for example, you will have to use multiple search_parameters
	 *		is_root    - checks if a given node is the root or not. The value for this parameter specifies if the result of the check should be true or false to pass
	 *			If no value is provided, true is assumed.
	 *		is_type	   - checks what type the node is. The value is the type desired. If more than one value, any of the values will pass the node
	 *			possible values: 'tag', 'leaf', 'comment', 'attribute'
	 *		has_parent_with_leaf - expansion on has_parent. Same idea, except 2nd parameter is the value of the leaf. If any of the leaves of the parent match the value the node passes
	 *			position from child not implemented	
	 *		has_parent_with_node_with_leaf - expansion on  on has_parent_with_leaf - looks for a second node, that is a child of the parent that has a given leaf
	 * </p>
	 * @param node_name - text of node to search for
	 * @param search_parameters - parameters as described above
	 * @param parameter_values - values for parameters as defined above
	 * @return List of XMLNodes matching the search parameters
	 */
	public ArrayList<XMLNode> search(String node_name, ArrayList<String> search_parameters, ArrayList<ArrayList<String>> parameter_values){
		ArrayList<XMLNode> nodes = new ArrayList<XMLNode>();
		
		if(tree != null){
			// first get all nodes matching node_name
			nodes = tree.findNodes(node_name);

			//now process search parameters
			for(int ii = 0; ii < search_parameters.size(); ii++){
				String param = search_parameters.get(ii);
				ArrayList<XMLNode> new_nodes = new ArrayList<XMLNode>();
				for(int jj = 0; jj < nodes.size(); jj++){
					XMLNode curNode = nodes.get(jj);					
					if(param.equals("has_parent")){
						ArrayList<String> values = parameter_values.get(ii);
						if(values.size() > 0){
							String parent_name = values.get(0);
							ArrayList<Integer> parents = curNode.hasParent(parent_name);
							boolean pass = false;
							if(values.size() > 1){
								for(int kk = 1; kk < values.size(); kk++){
									int value = Integer.parseInt(values.get(kk));
									if(parents.contains(value))
										pass = true;
								}
							} else{
								if(parents.size() > 0)
									pass = true;
							}
							if(pass)
								new_nodes.add(curNode);
						} else
							System.out.println("[ERROR] XML_Parser.search error :: search_parameter has_parent does not provide any values.");
					} else if(param.equals("is_root")){
						ArrayList<String> values = parameter_values.get(ii);
						boolean condition = true;
						if(values.size() > 0)
							condition = Boolean.parseBoolean(values.get(0));
						if(curNode.isRoot() == condition)
							new_nodes.add(curNode);
					} else if(param.equals("is_type")){
						ArrayList<String> values = parameter_values.get(ii);
						ArrayList<Integer> type_values = new ArrayList<Integer>();
						if(values.size() > 0){
							for(int kk = 0; kk< values.size(); kk ++ ){
								if(values.get(kk).equals("tag"))
									type_values.add(0);
								else if(values.get(kk).equals("leaf"))
									type_values.add(1);
								else if(values.get(kk).equals("comment"))
									type_values.add(2);
								else if(values.get(kk).equals("attribute"))
									type_values.add(3);
							}
							if(type_values.contains(curNode.node_type))
								new_nodes.add(curNode);
						} else
							System.out.println("[ERROR] XML_Parser.search error :: search_parameter is_type does not provide any values.");
					} else if(param.equals("has_parent_with_leaf")){
						ArrayList<String> values = parameter_values.get(ii);
						if(values.size() > 1){
							String parent_name = values.get(0);
							String leaf_name = values.get(1);
							XMLNode par = curNode.getParent(parent_name, curNode);
							if(par != null){
								ArrayList<String> leaves = par.getLeaves(par);
								if(leaves.contains(leaf_name))
									new_nodes.add(curNode);
							}
						} else{
							System.out.println("[ERROR] XML_Parser.search error :: search_parameter has_parent_with_leaf does not provide enough values.");
						}
					} else if(param.equals("has_parent_with_node_with_leaf")){
						ArrayList<String> values = parameter_values.get(ii);
						if(values.size() > 2){
							String parent_name = values.get(0);
							String child_name = values.get(1);
							String leaf_name = values.get(2);
							XMLNode par = curNode.getParent(parent_name, curNode);
							if(par != null){
								XMLNode chi = par.getChild(child_name, par);
								if(chi != null){
									ArrayList<String> leaves = chi.getLeaves(chi);
									if(leaves.contains(leaf_name))
										new_nodes.add(curNode);
								}
							}
						} else{
							System.out.println("[ERROR] XML_Parser.search error :: search_parameter has_parent_with_child_with_leaf does not provide enough values.");
						}
					} else{
						System.out.println("[ERROR] XML_Parser.search error :: unidentified search parameter.");
					}
				}
				nodes = new_nodes;
			}
		}
		return nodes;
	}
	
	/**
	 * Same as search, but returns the leaves of found nodes.
	 * @param node_name
	 * @param search_parameters
	 * @param parameter_values
	 * @return
	 */
	public ArrayList<ArrayList<String>> searchForLeaves(String node_name, ArrayList<String> search_parameters, ArrayList<ArrayList<String>> parameter_values){
		ArrayList<XMLNode> nodes = search(node_name, search_parameters, parameter_values);
		ArrayList<ArrayList<String>> leaves = new ArrayList<ArrayList<String>>();
		for(int ii = 0; ii < nodes.size(); ii++){
			leaves.add(new ArrayList<String>());
			for(int jj = 0; jj < nodes.get(ii).children.size(); jj++){
				if(nodes.get(ii).children.get(jj).node_type == 1)
					leaves.get(ii).add(nodes.get(ii).children.get(jj).text);
			}
		}
		return leaves;
	}

	/**
	 * Removes all instances of a node with text as <name>.
	 * @param name - text that marks the node for removal.
	 */
	public void removeAllOfNode(String name){
		tree.removeAllOfNode(name);
	}

	/**
	 * Parses a piece of the XML file to put it in a tag or leaf.
	 * @param line - line that has been delimited
	 * @param tree - XMLTree containing tree version of the XML file
	 */
	private void parseSection(String line, XMLTree tree){
		//first identify the line
		if(line.length() > 0){
			char first_char = line.charAt(0), last_char = line.charAt(line.length()-1);
			int type = -1;
			if((first_char == '<') && (last_char == '>'))
				type = 0;
			else if((first_char == '\"') && (last_char == '\"'))
				type = 1;
			else if((first_char == '\'') && (last_char == '\''))
				type = 2;
			else if((first_char == '>') && (last_char == '<'))
				type = 3;
			else
				System.out.println("[ERROR] XML_Parser.parseSection error :: XML file line starts with unknown parameters.");

			line = line.substring(1, line.length()-1);
			if(type == 0){
				//types:
				//<? ?> 0
				//<!-- --> 1
 				//<! > 2
				//< > 3
				//< /> 4
				//</ > 5
				int tag_type = -1;
				if((line.length() >= 2) && (line.charAt(0) == '?') && (line.charAt(line.length()-1) == '?')){
					tag_type = 0;
					line = line.substring(1, line.length()-1);
				}
				else if((line.length() >= 5) && line.substring(0,3).equals("!--") && line.substring(line.length()-2,line.length()).equals("--")){
					tag_type = 1;
					line = line.substring(3, line.length()-2);
				}
				else if((line.length() >= 1) && (line.charAt(0) == '!')){
					tag_type = 2;
					line = line.substring(1, line.length());
				}
				else if((line.length() >= 1) && (line.charAt(line.length()-1) == '/')){
					tag_type = 4;
					line = line.substring(0, line.length()-1);
				}
				else if((line.length() >= 1) && (line.charAt(0) == '/')){
					tag_type = 5;
					line = line.substring(1, line.length());
				}
				else
					tag_type = 3;

				if(tag_type == 0){	//get encoding and version
					ArrayList<String> attributes = StrOps.getAllTextBetweenPatternsIgnoringSections(line, " ", opening_sequences, closing_sequences);
					//first attribute is the tag name - the rest are actual attributes
					//since this is just the encoding and version, we can ignore the actual tag
					boolean version_set = false, encoding_set = false;
					for(int kk = 1; kk < attributes.size(); kk++){
						ArrayList<String> single_attribute = partitionAttribute(attributes.get(kk));
						if(single_attribute.size() == 2 && single_attribute.get(0).equals("version")){
							tree.setVersion(single_attribute.get(1));
							version_set = true;
						} else if(single_attribute.size() == 2 && single_attribute.get(0).equals("encoding")){
							tree.setEncoding(single_attribute.get(1));
							encoding_set = true;
						} else{
							System.out.println("[ERROR] XML_Parser.parseSection error :: XML file contains unidentified version information.");
						}
					}
					
					if(!version_set){
						System.out.println("[ERROR] XML_Parser.parseSection error :: XML file does not contain version information.");
					}
					if(!encoding_set){
						System.out.println("[ERROR] XML_Parser.parseSection error :: XML file does not contain encoding information.");
					}
				}				
				else if(tag_type == 2){ //get CDATA
					tree.setCDATA(line);
				}
				else if(tag_type == 1){	//comment - ignored

				}
				else if(tag_type == 3 || tag_type == 4){	//get regular tag
					ArrayList<String> attributes = StrOps.getAllTextBetweenPatternsIgnoringSections(line, " ", opening_sequences, closing_sequences);
					if(attributes.size() > 0){
						if(!StrOps.trimString(attributes.get(0)).equals("")){
							tree.addNode(new XMLNode(ID++, StrOps.trimString(attributes.get(0)), 0));
						
							if(attributes.size() > 1){
								for(int ii = 1; ii < attributes.size(); ii++){
									ArrayList<String> single_attribute = partitionAttribute(attributes.get(ii));
									if(single_attribute.size() > 0){
										if(!StrOps.trimString(single_attribute.get(0)).equals("")){
											tree.addNode(new XMLNode(ID++, StrOps.trimString(single_attribute.get(0)), 3));
											if(single_attribute.size() > 1){
												if(!StrOps.trimString(single_attribute.get(1)).equals("")){
													tree.addNode(new XMLNode(ID++, StrOps.trimString(single_attribute.get(1)), 1));
													if(!tree.stepUp())
														System.out.println("[ERROR] XML_Parser.parseSection error :: Step up failed");
												}
											}
											if(!tree.stepUp())
												System.out.println("[ERROR] XML_Parser.parseSection error :: Step up failed");
										}
									}
								}
							}
							if(tag_type == 4)
								if(!tree.stepUp())
									System.out.println("[ERROR] XML_Parser.parseSection error :: Step up failed");
						}
					}
					else{
						System.out.println("[ERROR] XML_Parser.parseSection error :: attributes return nothing.");
					}
				}
				else if(tag_type == 5){
					//has no attributes
					if(line.equals(tree.getCurNodeName())){
						if(!tree.stepUp())
							System.out.println("[ERROR] XML_Parser.parseSection error :: Step up failed");
					}
					else{
						System.out.println("[ERROR] XML_Parser.parseSection error :: Closing tag does not match the tag that opened it.");
					}
				}

			}
			else if(type == 1){		//1,2,3 are treated the same
				if(!StrOps.trimString(line).equals("")){
					tree.addNode(new XMLNode(ID++, StrOps.trimString(line), 1));
					if(!tree.stepUp())
						System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: stepUp returned false from a leaf. line = %s ID = %d", line, ID));
				}
			}
			else if(type == 2){
					if(!StrOps.trimString(line).equals("")){
					tree.addNode(new XMLNode(ID++, StrOps.trimString(line), 1));
					if(!tree.stepUp())
						System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: stepUp returned false from a leaf. line = %s ID = %d", line, ID));
				}
			}
			else if(type == 3){
				if(!StrOps.trimString(line).equals("")){
					tree.addNode(new XMLNode(ID++, StrOps.trimString(line), 1));
					if(!tree.stepUp())
						System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: stepUp returned false from a leaf. line = %s ID = %d", line, ID));
				}
			}
			else{
				System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: Invalid type. line = %s ID = %d", line, ID));
			}
		}
	}
	
	/**
	 * Updates the current index after parsing a line
	 * @param file - the XML file in String format
	 * @param line - the line that has been parsed
	 * @param index - the current index
	 * @return - outputs the new index
	 */
	private int updateIndex(String file, String line, int index){
		int new_ind = StrOps.findPatternAfterIndex(file, line, index);
		if(new_ind == -1){
			System.out.println("[ERROR] XML_Parser.updateIndex error :: findPatternAfterIndex returned -1");
			return -1;
		}
		return (new_ind + line.length() - 1);
	}
	
	/**
	 * Reads in the XML file into a String
	 * @param XMLFile - path to the XML file
	 * @return - String containing the XML file data
	 */
	private String readFile(String XMLFile){
		StringBuilder file = new StringBuilder(20000);
		try{
			BufferedReader br = new BufferedReader(new FileReader(XMLFile));
			String line;
			while((line = br.readLine()) != null){
				file.append(line);
				file.append(" ");
			}
			br.close();
		} catch (IOException e){
			System.out.println("[ERROR] XML_Parser.readFile error :: Cannot read XML File: " + XMLFile);
			file = null;
		}
		return file.toString();
	}

	/**
	 * Breaks up the inner contents of an attribute into the attribute and the value of the attribute
	 * @param attribute - attribute and value as a whole string
	 * @return - the parts of the attribute
	 */
	private ArrayList<String> partitionAttribute(String attribute){
		ArrayList<String> parts = new ArrayList<String>();
		int index = StrOps.findPattern(attribute, "=");
		if(index == -1){
			parts.add(attribute);
		} else{
			parts.add(attribute.substring(0,index));
			parts.add(attribute.substring(index+1));
		}
		return parts;
	} 
	
	/**
	 * Returns the ArrayList<XMLNode> of the tree's root list
	 * @return rootList
	 */
	public ArrayList<XMLNode> getRootList(){
		return tree.getRootList();
	}
}
