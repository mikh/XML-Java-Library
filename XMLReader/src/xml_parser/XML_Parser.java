package xml_parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class XML_Parser {
	
	public void parseXML(String XMLFile){
		String file = readFile(XMLFile);
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
