package com.hpdb.extraction.pageAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

public class AlignDataItem {
	ArrayList<ArrayList<Element>> NewDataItems = new ArrayList<>();

	ArrayList<ArrayList<Element>> DataItems = new ArrayList<>();
	
	ArrayList<Element> nodeParents = new ArrayList<>();

	public ArrayList<ArrayList<Element>> getNewDataItems() {
		return NewDataItems;
	}
	
	public ArrayList<Element> getNodeParents() {
		return nodeParents;
	}

	int maxItems = 0; // 所有数据记录中含有最多数据项的数据项个数

	int maxNum = 0; // 所有数据记录中含有最多数据项的那条记录在集合 中的下标

	public AlignDataItem(ArrayList<ArrayList<Element>> DataItems) {//获得最大长度的数据记录，其节点path将作为数组第零行，即列名
		this.DataItems = DataItems;
		for (int i = 0; i < DataItems.size(); i++) {
			if (DataItems.get(i).size() > maxItems) {
				maxItems = DataItems.get(i).size();
				maxNum = i;
			}
		}
	}

	Object[][] DataItemsArray;

	public void valueToArray() { // 将DataItems中的值传入到数组DataItemsArray
		DataItemsArray = new Object[DataItems.size() + 1][maxItems]; // 利用数组来对齐数据
																		// 第1行是列名
		// 首先给第1行赋值，也就是列名赋值
		for (int n = 0; n < maxItems; n++) {
			DataItemsArray[0][n] = DataItems.get(maxNum).get(n);

		}
		for (int i = 1; i < DataItemsArray.length; i++) { // 从第二行开始
			for (int j = 0, k = 0; j < DataItemsArray[i].length; j++) {//此处的i不管取什么值，DataItemsArray[i].length都一样，即列数
				if (DataItems.get(i - 1).size() > k) { // 因为数组的列数（DataItemsArray[i].length）取得是list中的最大值，所以必须判断以免list越界
					if (DataItems.get(i - 1).get(k).getPath().equals(DataItems.get(maxNum).get(j).getPath())) { // 先判断节点path是否相等
						if (DataItems.get(i - 1).get(k).attributeCount() == DataItems.get(maxNum).get(j)
								.attributeCount()) { // path相等的前提下判断属性个数是否相等
							List<Attribute> attrList1 = DataItems.get(i - 1).get(k).attributes(); // 属性个数相等的前提下判断各个属性名是否都相等
							List<Attribute> attrList2 = DataItems.get(maxNum).get(j).attributes();
							int equalNum = 0;
							for (int a = 0; a < attrList1.size(); a++) {
								if (attrList1.get(a).getName().equals(attrList2.get(a).getName())) {
									equalNum++;
								} else
									break;
							}
							if (equalNum == DataItems.get(i - 1).get(k).attributeCount()) {
								DataItemsArray[i][j] = DataItems.get(i - 1).get(k);
								k++;
							}
						}
					} else // 当DataItems.get(i-1).get(k)的值没有赋给数组的时候，k不自加，而是用当前的值继续与后面的DataItems.get(maxNum).get(j)比较
						continue;
				}
			}
		}

		/*
		 * for(int i=0;i<DataItemsArray.length; i++){ for (int j = 0; j <
		 * DataItemsArray[i].length; j++) {
		 * System.out.println(DataItemsArray[i][j]); } }
		 */

		for (int i = 1; i < DataItemsArray.length; i++) {
			ArrayList<Element> DataItem = new ArrayList<>();
			for (int j = 0; j < DataItemsArray[i].length; j++) {
				DataItem.add((Element) DataItemsArray[i][j]);
			//	System.out.println(DataItemsArray[i][j]);
			}
			NewDataItems.add(DataItem);
		}
		
			for (int j = 0; j < DataItemsArray[0].length; j++) {
				nodeParents.add((Element) DataItemsArray[0][j]);
		}
	}


}
