package xml_parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import string_operations.StrOps;

public class XML_Parser {
	ArrayList<String> opening_sequences = new ArrayList<String>();
	ArrayList<String> closing_sequences = new ArrayList<String>();
	
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
	
	public void parseXML(String XMLFile){
		XMLTree tree = new XMLTree();
		String file = readFile(XMLFile);
		int index = 0;
		int ID = 0;
		
		String line = StrOps.getNextSection(file, opening_sequences, closing_sequences, index);
		while(line != null){
			System.out.println(line);
			index = updateIndex(file, line, index);
			parseSection(line, tree, ID++);
			line = StrOps.getNextSection(file, opening_sequences, closing_sequences, index);
		}
		
		
	}

	private void parseSection(String line, XMLTree tree, int ID){
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
					attributes.clear();
				}				
				else if(tag_type == 2){ //get CDATA
					tree.setCDATA(line);
				}
				else if(tag_type == 1){	//comment - ignored

				}
				else if(tag_type == 3){	//get regular tag

				}
				else if(tag_type == 4){

				}
				else if(tag_type == 4){
					
				}

			}
			else if(type == 1){		//1,2,3 are treated the same
				tree.addNode(new XMLNode(ID, line, 1));
				if(!tree.stepUp())
					System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: stepUp returned false from a leaf. line = %s ID = %d", line, ID));
			}
			else if(type == 2){
				tree.addNode(new XMLNode(ID, line, 1));
				if(!tree.stepUp())
					System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: stepUp returned false from a leaf. line = %s ID = %d", line, ID));
			}
			else if(type == 3){
				tree.addNode(new XMLNode(ID, line, 1));
				if(!tree.stepUp())
					System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: stepUp returned false from a leaf. line = %s ID = %d", line, ID));
			}
			else{
				System.out.println(String.format("[ERROR] XML_Parser.parseSection error :: Invalid type. line = %s ID = %d", line, ID));
			}
		}
	}
	
	private int updateIndex(String file, String line, int index){
		int new_ind = StrOps.findPatternAfterIndex(file, line, index);
		if(new_ind == -1){
			System.out.println("[ERROR] XML_Parser.updateIndex error :: findPatternAfterIndex returned -1");
			return -1;
		}
		return (new_ind + line.length() - 1);
	}
	
	private String readFile(String XMLFile){
		String file = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader(XMLFile));
			String line;
			while((line = br.readLine()) != null){
				file += line;
				file += " ";
			}
			br.close();
		} catch (IOException e){
			System.out.println("[ERROR] XML_Parser.readFile error :: Cannot read XML File: " + XMLFile);
		}
		return file;
	}
}
