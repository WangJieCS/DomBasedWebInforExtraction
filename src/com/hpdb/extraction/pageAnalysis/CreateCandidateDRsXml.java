package com.hpdb.extraction.pageAnalysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class CreateCandidateDRsXml {
	int deep = 1;
	int currentDeep = 1;
	ArrayList<ArrayList<Element>> CandidateDataRegions = new ArrayList<>();

	public void writeXML(ArrayList<ArrayList<Element>> candidateDataRegions) {
		CandidateDataRegions = candidateDataRegions;

		Element root = DocumentHelper.createElement("CandidateDataRegions");
		Document document = DocumentHelper.createDocument(root);
		int i = 1;
		for (ArrayList<Element> cdrs : CandidateDataRegions) {
			for (Element cdr : cdrs) {
				if (cdr.getParent().getParent() != null) {
					Element parent = cdr.getParent(); // 之所以添加父节点是因为出现了数据记录漏掉的情况，理论上是不需要添加父节点的！！！
					parent.getParent().remove(parent); // 节点有父节点的情况下，不可以再被其他节点add，所以，先让候选区域节点的父节点删除自己
					root.add(parent);
				} else {
					cdr.getParent().remove(cdr);
					root.add(cdr);

				}
			}

		}

		// 由于root添加的是候选数据区域数据记录的父节点，所以还需要进行一次父节点的所有子节点相似度筛选
		// 重复之前的步骤！！！！
		/*List<Element> parents = root.elements();     //行不通，就跟之前会漏掉数据记录一样，这里还是会漏掉！！（造成unSimNum>2）导致真正数据区域的parent被删除
		for (Element parent : parents) {
			if(!filterDataRecordsParent(parent))
				root.remove(parent);
		}*/

		try {
			Writer osWrite = new OutputStreamWriter(
					new FileOutputStream(System.getProperty("user.dir") + "\\" + "CandidateDataRegions.xml"));
			XMLWriter writer = new XMLWriter(osWrite);
			writer.write(document);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*private boolean filterDataRecordsParent(Element node) {
		List<Element> children = node.elements();
		int childNum = children.size();
		int unSimNum = 0;
		for (int i = 0; i < childNum - 1; i++) {
			if (treeSimilar(children.get(i), children.get(i + 1)) == false)
				unSimNum++;
		}
		if (unSimNum>2)  //不相似的子树（数据记录）大于2，则认为不是数据区域
			return false;
		
		return true;
	}

	private boolean treeSimilar(Element node1, Element node2) {
		ArrayList<String> s1 = new ArrayList<>();
		ArrayList<String> s2 = new ArrayList<>();
		getTreeTagsAndDeep(node1, s1);
		int s1l = s1.size();
		getTreeTagsAndDeep(node2, s2);
		int s2l = s2.size();
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
		for (Element e : listElement) {
			this.getTreeTagsAndDeep(e, s);

		}
	}

	private int levenshteinDistance(ArrayList<String> s1, ArrayList<String> s2) {
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
	}*/
}
