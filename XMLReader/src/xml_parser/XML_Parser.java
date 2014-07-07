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
		String file = readFile(XMLFile);
		int index = 0;
		
		String line = StrOps.getNextSection(file, opening_sequences, closing_sequences, index);
		while(line != null){
			System.out.println(line);
			index = updateIndex(file, line, index);
			line = StrOps.getNextSection(file, opening_sequences, closing_sequences, index);
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
