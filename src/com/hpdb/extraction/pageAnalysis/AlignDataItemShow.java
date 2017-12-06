package com.hpdb.extraction.pageAnalysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.dom4j.Element;

public class AlignDataItemShow extends JFrame {
	private JFrame mFrame;

	private JTable mTable;

	// int maxItem = 0; // 所有数据记录中含有最多数据项的数据项个数

	// int maxNum = 0; // 所有数据记录中含有最多数据项的那条记录的下标

	int colNum = 0;

	public AlignDataItemShow() {
		super();
	}

	public AlignDataItemShow(ArrayList<ArrayList<Element>> DataItems, ArrayList<Element> colNames) {

		/*
		 * for (int i = 0; i < DataItems.size(); i++) { if
		 * (DataItems.get(i).size() > maxItem) { maxItem =
		 * DataItems.get(i).size(); maxNum = i; } }
		 */
		colNum = DataItems.get(0).size();
		mFrame = new AlignDataItemShow();
		mFrame.setTitle("DataItemsTable");
		// 更改应用程序的图标
		mFrame.setIconImage(mFrame.getToolkit().getImage(System.getProperty("user.dir") + "..\\file\\icon.png"));
		mTable = new JTable(new MyTableModel(DataItems, colNames));
		mTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		mTable.setFont(new Font("宋体", Font.PLAIN, 16));
		// 获得表头
		JTableHeader tableH = mTable.getTableHeader();
		// 设置表头的背景色
		tableH.setBackground(new Color(211, 211, 211));
		// 设置表头的文字颜色
		tableH.setForeground(new Color(255, 0, 0));
		tableH.setFont(new Font("宋体", Font.BOLD, 18));
		// 设置行高,列宽

		tableH.setReorderingAllowed(true);
		tableH.setResizingAllowed(true);

		mTable.setRowHeight(30);
		mTable.setColumnModel(this.setColumnWidth(mTable, new int[] { 300, 300, 600 }));
		mTable.addMouseListener(new TableMouseAdapter());

		JScrollPane scrollPane = new JScrollPane(mTable); // 这样才能显示列名
		mTable.setFillsViewportHeight(true);
		scrollPane.setAutoscrolls(true);
		mFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		mFrame.setSize(1200, 1200);
		mFrame.pack();
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.setLocationRelativeTo(null);
		mFrame.setVisible(true);
	}

	/**
	 * 设置列宽
	 * 
	 * @param table
	 * @param width
	 * @return
	 */
	public TableColumnModel setColumnWidth(JTable table, int[] width) {
		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < width.length; i++) {
			TableColumn column = columns.getColumn(i);
			column.setPreferredWidth(width[i]);
		}
		return columns;
	}

	public void display() {
		mFrame.setVisible(true);
	}

	class MyTableModel extends AbstractTableModel {
		private ArrayList<ArrayList<Element>> DataItems = new ArrayList<>();
		ArrayList<Element> colNames = new ArrayList<>();

		public MyTableModel(ArrayList<ArrayList<Element>> DataItems, ArrayList<Element> colNames) {
			this.DataItems = DataItems;
			this.colNames = colNames;
		}

		private String[] columnNames = new String[colNum]; // 初始表头

		private void initCoumnNames() {
			for (int i = 0; i < colNum; i++) {
				columnNames[i] = colNames.get(i).getPath();
			}
		}

		/*
		 * private void initCoumnNames() { //数据项最多的数据记录的数据项path作为columnName for
		 * (int i = 0; i < columnNames.length; i++) { columnNames[i] =
		 * DataItems.get(maxNum).get(i).getPath(); } }
		 */
		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return DataItems.size();
		}

		public String getColumnName(int col) { // 在此显示表头
			initCoumnNames();
			return columnNames[col];
		}

		/**
		 * 设置显示的数据
		 */
		public Object getValueAt(int row, int col) { // 此时的get即为设置的意思
			ArrayList<Element> DataItem = DataItems.get(row);
			String value = "";
			// if (DataItem.size() - col > 0) // 保证col的值不会超出DataItem的最大下标值
			if (DataItem.get(col) != null) {
				if (DataItem.get(col).elements().size() == 0) // 如果文本节点没有孩子数据项的值就为节点的文本值
					value = DataItem.get(col).getTextTrim();
				else {
					StringBuffer value2 = new StringBuffer();
					value2 = getTreeText(DataItem.get(col), value2); // 否则，数据项的值应为节点以及其子树的文本值之和
					return value2;
				}
			}
			return value;
		}

		private StringBuffer getTreeText(Element node, StringBuffer value) {
			// TODO Auto-generated method stub
			value.append(node.getTextTrim());
			List<Element> listElement = node.elements();
			for (Element e : listElement) {
				this.getTreeText(e, value);

			}
			return value;
		}

		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	/**
	 * 监听鼠标事件
	 * 
	 * @author Belief
	 */
	class TableMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			// 双击鼠标左键
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getClickCount() == 2) {
					String message = mTable.getModel().getValueAt(mTable.getSelectedRow(), mTable.getSelectedColumn())
							.toString();
					JDialog dialog = new JDialog();
					dialog.setTitle("显示表格详细内容");
					JTextArea mTextArea = new JTextArea();
					mTextArea.setEditable(false);
					mTextArea.setFont(new Font("宋体", Font.PLAIN, 16));
					mTextArea.setLineWrap(true);
					mTextArea.setWrapStyleWord(true);
					mTextArea.setText("    " + message);
					JScrollPane scrollPane = new JScrollPane(mTextArea);
					dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
					dialog.setSize(300, 150);
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}
				if (e.getClickCount() == 3) {
					String message = mTable.getModel().getColumnName(mTable.getSelectedColumn()).toString();
					JDialog dialog = new JDialog();
					dialog.setTitle("显示列名详细内容");
					JTextArea mTextArea = new JTextArea();
					mTextArea.setEditable(false);
					mTextArea.setFont(new Font("宋体", Font.PLAIN, 16));
					mTextArea.setLineWrap(true);
					mTextArea.setWrapStyleWord(true);
					mTextArea.setText("    " + message);
					JScrollPane scrollPane = new JScrollPane(mTextArea);
					dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
					dialog.setSize(300, 150);
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}

			}
		}
	}

	/**
	 * 只关闭当前窗口
	 */
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.dispose();
		} else {
			super.processWindowEvent(e);
		}
	}
}
