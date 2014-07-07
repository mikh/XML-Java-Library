package test;

import xml_parser.XML_Parser;

public class Test {
	public static void main(String[] args){
		System.out.println("Starting test");
		XML_Parser xmlp = new XML_Parser();
		xmlp.parseXML("example.xml");
		System.out.println("Test Complete");
	}
}
