package com.hpdb.extraction.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpdb.extraction.pageAnalysis.HtmlToXml;
import com.hpdb.extraction.pageAnalysis.AlignDataItem;
import com.hpdb.extraction.pageAnalysis.AlignDataItemShow;
import com.hpdb.extraction.pageAnalysis.CreateCandidateDRsXml;
import com.hpdb.extraction.pageAnalysis.ExtractDataItem;
import com.hpdb.extraction.pageAnalysis.GetCandidateDataRegions;
import com.hpdb.extraction.pageAnalysis.GetDataRecords;
import com.hpdb.extraction.view.splash.SplashDialog;
import com.hpdb.extraction.view.xmltree.XMLViewer;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class MainViewFrame extends JPanel {
	private static Logger logger = LoggerFactory.getLogger(MainViewFrame.class);

	private static final String SOFTWARE_TITLE = "基于dom的web信息抽取系统";

	private static final String default_url = "http://search.dangdang.com/?key=%C1%C4%D5%AB&act=input";

	private JTextField UrlTextField = null;

	private JButton UrlLoadButton = null;

	private JButton PageAnalysisButton = null;

	private JButton LocateDataRegionButton = null;

	private JButton ExtractDataRecordsButton = null;

	private JButton AlignDataItemButton = null;

	private JWebBrowser mWebBrowser = null;

	private JPanel webBrowserPanel = null;

	private JFrame mFrame = null;

	private static MainViewFrame mainViewFrame = null;

	private String url = null;

	private Executor executor = null;

	private ScrolledTextFrame messageframe = null;

	private HtmlToXml htmltoxml = null;

	private GetCandidateDataRegions getcandidatedataregions = null;

	private CreateCandidateDRsXml createcandidateDRsXml = null;

	private XMLViewer xmlviewer = null;

	private GetDataRecords getdatarecords = null;

	private ExtractDataItem extractdataitem = null;

	private AlignDataItemShow aligndataitemshow = null;

	private AlignDataItem aligndataitem=null;
	
	public void initFrame(final SplashDialog splashDialog) {
		UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mFrame = new JFrame(SOFTWARE_TITLE);
				mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainViewFrame = new MainViewFrame();
				mainViewFrame.initView(mFrame);
				// 更改应用程序的图标
				mFrame.setIconImage(mFrame.getToolkit().getImage(System.getProperty("user.dir") + "\\file\\icon.png"));
				// 初始化全屏显示
				mFrame.setSize(800, 600);
				mFrame.setLocationRelativeTo(null);
				mainViewFrame.setActionEventListener();
				splashDialog.setVisible(false); // 关闭过渡页
				mFrame.setVisible(true);
			}
		});
		NativeInterface.runEventPump();
	}

	public MainViewFrame() {
		super(new BorderLayout());
		// 初始化浏览器组件
		mWebBrowser = new JWebBrowser();
		webBrowserPanel = new JPanel(new BorderLayout());
		mWebBrowser.navigate(System.getProperty("user.dir") + "\\file\\welcome.html"); // 初始页面
		mWebBrowser.setButtonBarVisible(false);
		mWebBrowser.setMenuBarVisible(false);
		mWebBrowser.setBarsVisible(false);
		mWebBrowser.setStatusBarVisible(false);
		webBrowserPanel.add(mWebBrowser, BorderLayout.CENTER);
		add(webBrowserPanel, BorderLayout.CENTER);
	}

	/**
	 * 初始化布局及视图控件
	 * 
	 * @param jFrame
	 */
	private void initView(JFrame jFrame) {
		JLabel UrlLabel = new JLabel("主题网页URL");
		UrlLabel.setFont(new Font("宋体", Font.PLAIN, 18));
		UrlTextField = new JTextField(default_url);
		UrlTextField.setFont(new Font("宋体", Font.PLAIN, 18));
		UrlLoadButton = new JButton("加载");
		UrlLoadButton.setFont(new Font("宋体", Font.PLAIN, 18));

		PageAnalysisButton = new JButton("解析web页面");
		PageAnalysisButton.setFont(new Font("宋体", Font.PLAIN, 18));
		PageAnalysisButton.setPreferredSize(new Dimension(180, 30));
		LocateDataRegionButton = new JButton("定位数据区域");
		LocateDataRegionButton.setFont(new Font("宋体", Font.PLAIN, 18));
		LocateDataRegionButton.setPreferredSize(new Dimension(180, 30));
		ExtractDataRecordsButton = new JButton("抽取数据记录");
		ExtractDataRecordsButton.setFont(new Font("宋体", Font.PLAIN, 18));
		ExtractDataRecordsButton.setPreferredSize(new Dimension(180, 30));
		AlignDataItemButton = new JButton("对齐数据项");
		AlignDataItemButton.setFont(new Font("宋体", Font.PLAIN, 18));
		AlignDataItemButton.setPreferredSize(new Dimension(180, 30));
		// 顶部
		JPanel topPanel = new JPanel();
		BoxLayout topLayout = new BoxLayout(topPanel, BoxLayout.X_AXIS);
		topPanel.setLayout(topLayout);
		topPanel.add(Box.createHorizontalStrut(10));
		topPanel.add(UrlLabel);
		topPanel.add(Box.createHorizontalStrut(10));
		topPanel.add(UrlTextField);
		topPanel.add(Box.createHorizontalStrut(50));
		topPanel.add(UrlLoadButton);
		topPanel.add(Box.createHorizontalStrut(73));
		topPanel.setBackground(new Color(245, 245, 245));
		// 右侧
		JPanel rightPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Image image = new ImageIcon(System.getProperty("user.dir") + "\\file\\right_background.png").getImage();
				g.drawImage(image, 0, 0, this);
			};
		};
		Box rightBox = Box.createVerticalBox();
		rightBox.add(Box.createVerticalStrut(80));
		// rightBox.add(PageAnalysisButton);
		// rightBox.add(Box.createVerticalStrut(25));
		rightBox.add(LocateDataRegionButton);
		rightBox.add(Box.createVerticalStrut(25));
		rightBox.add(ExtractDataRecordsButton);
		rightBox.add(Box.createVerticalStrut(25));
		rightBox.add(AlignDataItemButton);
		rightBox.add(Box.createVerticalStrut(25));
		rightPanel.add(rightBox);

		jFrame.setLayout(new BorderLayout(3, 3));
		Container container = jFrame.getContentPane();
		container.add(this, BorderLayout.CENTER);
		container.add(topPanel, BorderLayout.NORTH);
		container.add(rightPanel, BorderLayout.EAST);
		container.setBackground(new Color(245, 245, 245));
	}

	/**
	 * 添加事件监听器
	 */
	private void setActionEventListener() {
		this.UrlLoadButton.addActionListener(new LoadUrlHandler());
		// this.PageAnalysisButton.addActionListener(new PageAnalysisHandler());
		this.LocateDataRegionButton.addActionListener(new LocateDataRegionHandler());
		this.ExtractDataRecordsButton.addActionListener(new ExtractDataRecordsHandler());
		this.AlignDataItemButton.addActionListener(new AlignDataItemHandler());
	}

	// 加载Web页面显示事件类
	class LoadUrlHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			url = UrlTextField.getText();
			mWebBrowser.navigate(url);
		}

	}

	
	  //解析Web页面事件类 
/*	class PageAnalysisHandler implements ActionListener{
	  
	  public void actionPerformed(ActionEvent arg0) { 
		  // TODO	  Auto-generated method stub
	  
	  }
	  
	  }*/
	 

	// 定位数据区域事件类
	class LocateDataRegionHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			if (url == null) {
				displayException("请先加载主题网页,定位数据区域！");
			} else {
				executor = Executors.newSingleThreadExecutor(); // 初始化执行者框架
				// 显示可滚动提示信息的窗口
				messageframe = new ScrolledTextFrame("正在定位数据区域,请稍等!  ");
				messageframe.showMessageView();

				Runnable getCandidateDRs = new Runnable() {
					@Override
					public void run() {
						try {
							htmltoxml = new HtmlToXml(url);
							htmltoxml.genXmlFile();
							getcandidatedataregions = new GetCandidateDataRegions();
							getcandidatedataregions.getRoot();
							createcandidateDRsXml = new CreateCandidateDRsXml();
							createcandidateDRsXml.writeXML(getcandidatedataregions.getCandidateDataRegions());

							xmlviewer = new XMLViewer();
							xmlviewer.makeFrame(System.getProperty("user.dir") + "\\" + "CandidateDataRegions.xml");

							getdatarecords = new GetDataRecords();
							getdatarecords.openFile();

							extractdataitem = new ExtractDataItem();
							extractdataitem.setDataRecords(getdatarecords.getRecords());
							extractdataitem.extractDataItem();
						   // extractdataitem.printDataItem();
                            
							aligndataitem = new AlignDataItem(extractdataitem.getAllItemParents());
							aligndataitem.valueToArray();//此处Array是数组的意思
							//aligndataitemshow= new AlignDataItemShow(extractdataitem.getAllItemParents());
							aligndataitemshow = new AlignDataItemShow(aligndataitem.getNewDataItems(),aligndataitem.getNodeParents());
							logger.info("数据区域定位完成！");
							closeWaitMessage();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};
				// 执行者框架执行任务
				executor.execute(getCandidateDRs);
			}

		}

	}

	// 抽取数据记录事件类
	class ExtractDataRecordsHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			if (url == null) {
				displayException("请先加载主题网页,然后再解析页面！");
			} else {
				executor = Executors.newSingleThreadExecutor(); // 初始化执行者框架
				// 显示可滚动提示信息的窗口
				messageframe = new ScrolledTextFrame("正在抽取数据记录,请稍等!  ");
				messageframe.showMessageView();

				Runnable getRecords = new Runnable() {

					@Override
					public void run() {
						try {
							getdatarecords = new GetDataRecords();
							getdatarecords.openFile();
							logger.info("网页数据记录抽取完成！");
							closeWaitMessage();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}; // 执行者框架执行任务
				executor.execute(getRecords);
			}

		}

	}

	// 对齐数据项事件类
	class AlignDataItemHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 关闭显示对话框
	 */
	private void closeWaitMessage() {
		messageframe.frame.setVisible(false);
	}

	/**
	 * 显示异常信息对话框
	 * 
	 * @param mesg
	 */
	public void displayException(String mesg) {
		JDialog dialog = new JDialog();
		dialog.setTitle("显示异常信息");
		JTextArea mTextArea = new JTextArea();
		mTextArea.setEditable(false);
		mTextArea.setFont(new Font("宋体", Font.PLAIN, 16));
		mTextArea.setLineWrap(true);
		mTextArea.setWrapStyleWord(true);
		mTextArea.setText("    " + mesg);
		JScrollPane scrollPane = new JScrollPane(mTextArea);
		dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
		dialog.setSize(300, 150);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
}
