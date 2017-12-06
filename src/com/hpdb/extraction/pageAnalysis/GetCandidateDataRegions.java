package com.hpdb.extraction.pageAnalysis;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class GetCandidateDataRegions {
	int deep = 1;
	int currentDeep = 1;
	ArrayList<ArrayList<Element>> CandidateDataRegions = new ArrayList<>();  //定义全局候选数据区域

	public void getRoot() {
		File xmlFile = new File(System.getProperty("user.dir") + "\\" + "SourcePage.xml");
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(xmlFile);
			Element root = document.getRootElement();
			this.searchCandidateDataRegions(root);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void searchCandidateDataRegions(Element node) {
		ArrayList<Element> DR = identifyDR(node);
		if (DR.size() > 0)
			CandidateDataRegions.add(DR);
		List<Element> children = node.elements();
		for (Element child : children) {
			if (!(DR.contains(child)))
				searchCandidateDataRegions(child);
		}
	}

	private ArrayList<Element> identifyDR(Element node) {
		List<Element> children = node.elements();
		ArrayList<Element> DR = new ArrayList<>();
		int childNum = children.size();
		for (int i = 0; i < childNum - 1; i++) {
			if (treeSimilar(children.get(i), children.get(i + 1)) == false)
				continue;

			if (DR.contains(children.get(i)))   //依次将左右相邻且相似的节点子树加入到DR
				DR.add(children.get(i + 1));
			else {
				DR.add(children.get(i));
				DR.add(children.get(i + 1));
			}

		}
		return DR;

	}

	private boolean treeSimilar(Element node1, Element node2) {
		ArrayList<String> s1 = new ArrayList<>();
		ArrayList<String> s2 = new ArrayList<>();
		deep=1;          // deep恢复初始值
		currentDeep=1;
		getTreeTagsAndDeep(node1, s1);
		int s1l = s1.size();
		if (deep < 3)   //如果node子树深度小于3    数据记录节点的层次一般不会少于2层标签
			return false;
		
		deep=1;          // deep恢复初始值
		currentDeep=1;
		getTreeTagsAndDeep(node2, s2);
		int s2l = s2.size();
		if (deep < 3)   //如果node子树深度小于3    数据记录节点的层次一般不会少于2层标签
			return false;
	
		if (s1l / s2l < 0.6) // 如果两棵树的节点个数相差太大 返回false
			return false;
		float diff = (Math.abs(s1l - s2l)) / (Math.max(s1l, s2l));
		if (diff > 0.3)
			return false;
		float similarity = ((Math.max(s1l, s2l)) - levenshteinDistance(s1, s2)) / (Math.max(s1l, s2l));
		if (similarity >= 0.8) // 相似度阈值为0.8
			return true;
		else
			return false;
	}

	private void getTreeTagsAndDeep(Element node, ArrayList<String> s) {
		// TODO Auto-generated method stub
		s.add(node.getName());
		List<Element> listElement = node.elements();
		if (listElement.size() > 0) { // 当前node有孩子时，size大于0
			currentDeep++;
		} else {
			if (currentDeep > deep) {
				deep = currentDeep;
			}
			if(currentDeep>1)
			currentDeep = 2;   // 当深度遍历完一颗子树时，到接下来的子树 ，此时深度应该等于2
		}
		for (Element e : listElement) {
			this.getTreeTagsAndDeep(e, s);

		}
	}

	private int levenshteinDistance(ArrayList<String> s1, ArrayList<String> s2) { //字符串编辑距离
		int s1l = s1.size();
		int s2l = s2.size();
		if (s1l == 0)
			return s2l;
		if (s2l == 0)
			return s1l;
		int c;
		if (s1.get(s1l - 1).equals(s2.get(s2l - 1)))
			c = 0;
		else
			c = 1;
		int min;
		ArrayList<String> news1 = s1;
		ArrayList<String> news2 = s2;
		news1.remove(s1l - 1);
		news2.remove(s2l - 1);
		int change = levenshteinDistance(news1, news2) + c;
		int delete = levenshteinDistance(news1, s2) + 1;
		int insert = levenshteinDistance(s1, news2) + 1;
		min = change;
		if (delete < min)
			min = delete;
		if (insert < min)
			min = insert;
		return min;
	}

	public ArrayList<ArrayList<Element>> getCandidateDataRegions() {
		return CandidateDataRegions;
	}

}
