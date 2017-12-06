package com.hpdb.extraction.pageAnalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class GetDataRecords {

	ArrayList<Element> Records = new ArrayList<>();

	int NodeCount = 0;

	int currentNodeCount = 0;

	StringBuffer maxText = new StringBuffer();

	public ArrayList<Element> getRecords() {
		return Records;
	}

	public void openFile() {
		File xmlFile = new File(System.getProperty("user.dir") + "\\" + "CandidateDataRegions.xml");
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(xmlFile);
			Element root = document.getRootElement();
			 getDataRecords1(root);
			//getDataRecords2(root);
			 printText();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getDataRecords1(Element node) {
		List<Element> parents = node.elements();
		for (Element parent : parents) {
			List<Element> candidateRecords = parent.elements();
			currentNodeCount = 0;
			getNodeCount(parent);
			if (candidateRecords.size() > Records.size() && currentNodeCount > NodeCount) { // 找到记录条数最多且子节点最多的区域为数据区域
				Records = (ArrayList<Element>) candidateRecords;
				NodeCount = currentNodeCount;
			}
		}
	}

	private void getDataRecords2(Element node) {
		List<Element> parents = node.elements();
		for (Element parent : parents) {
			List<Element> candidateRecords = parent.elements();
			StringBuffer currentText = new StringBuffer(); // 找到字符总长度最大的数据区域
			getText(parent, currentText);
			if(currentText.length()>maxText.length()){
				Records = (ArrayList<Element>) candidateRecords;
				maxText=currentText;
			}
		}
	}

	private void getNodeCount(Element node) {
		// TODO Auto-generated method stub
		currentNodeCount++;
		List<Element> listElement = node.elements();
		for (Element e : listElement) {
			this.getNodeCount(e);

		}
	}

	private void printText() {
		// TODO Auto-generated method stub

		for (int i = 0; i < Records.size(); i++) {
			StringBuffer text = new StringBuffer();
			
		//	String text = new String();
			//String text1="";
	
			getText(Records.get(i), text);
			System.out.println(text);
			System.out.println();
		}
	}

	private void getText(Element node, StringBuffer text) {
		text.append(node.getTextTrim());  //string +=
		// text+=node.getTextTrim();
		List<Element> listElement = node.elements();
		for (Element e : listElement) {
			this.getText(e, text);

		}
	}

}
