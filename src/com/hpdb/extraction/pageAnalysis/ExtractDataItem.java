package com.hpdb.extraction.pageAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

public class ExtractDataItem {
	ArrayList<ArrayList<Element>> AllItemParents = new ArrayList<>(); // 定义保存所有数据项的父节点
	ArrayList<Element> DataRecords = new ArrayList<>();

	public void setDataRecords(ArrayList<Element> dataRecords) { // 获得数据记录
		DataRecords = dataRecords;
	}

	public void extractDataItem() {
		for (Element records : DataRecords) {
			ArrayList<Element> ItemParents = new ArrayList<>();
			getNodeHasTextchild(records, ItemParents);
			AllItemParents.add(ItemParents);
		}

	}

	// 对数据记录的DOM子树进行深度遍历。如果节点含有文本值，就将它作为一个数据项，其值就是该文本。
	// 否则，若节点不含文本值，则对每一个孩子节点继续进行深度遍历。
	private void getNodeHasTextchild(Element node, ArrayList<Element> itemParents) {
		// TODO Auto-generated method stub
		List<Element> listElement = node.elements();
		if (node.getTextTrim().length() > 0) {
			itemParents.add(node);
		} else {
			for (Element e : listElement) {
				if (!itemParents.contains(e.getParent())) {
					this.getNodeHasTextchild(e, itemParents);
				}
			}
		}
	}

	public ArrayList<ArrayList<Element>> getAllItemParents() {
		return AllItemParents;
	}

	public void printDataItem() {
		for (ArrayList<Element> itemParent : AllItemParents)
			for (Element node : itemParent) {
				System.out.println(node.getName());
				List<Attribute> listAttr = node.attributes();
				for (Attribute attr : listAttr) {
					String name = attr.getName();
					String value = attr.getValue();
					System.out.println("属性名称： " + name + "		属性值： " + value);
				}
				System.out.println(node.getTextTrim());

			}
	}
}
