package com.hpdb.extraction.pageAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.filters.Writer;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class HtmlToXml {
	private static Logger logger = LoggerFactory.getLogger(HtmlToXml.class);
	URL url;

	public HtmlToXml(String _url) throws MalformedURLException {
		url = new URL(_url);
		generateFolder();
	}

	public Document getSourceNode() throws Exception, Error {
		DOMParser parser = new DOMParser();
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		InputStream inputs = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(inputs, getPageEncoding());
		InputSource source = new InputSource(isr);
		parser.parse(source);
		Document document = parser.getDocument();
		return document;
	}

	public void genXmlFile() throws Exception, Error {
		File file = null;

		String outputfile = "SourcePage.xml";
		Document document = getSourceNode();
		file = new File(System.getProperty("user.dir") + "\\" + outputfile);
		if (file.exists())
			file.delete();
		logger.info("generate " + file.getCanonicalPath() + " successfully!");

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(document);
		java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
		StreamResult result = new StreamResult(fos);
		Properties props = new Properties();
		props.setProperty("encoding", "UTF-8");
		props.setProperty("method", "xml");
		props.setProperty("omit-xml-declaration", "yes");

		transformer.setOutputProperties(props);

		transformer.transform(source, result);
		fos.close();

	}

	public String getPageEncoding() {
		String pageEncoding = null;
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
		} catch (Exception e) { // Report any errors that arise
			System.err.println(e);
			System.err.println("Usage:   java   HttpClient   <URL>   [<filename>]");
		}
		String htmlcode = sb.toString();
		// 解析html源码，取出<meta />区域，并取出charset
		String strbegin = "<meta";
		String strend = ">";
		String strtmp;
		int begin = htmlcode.indexOf(strbegin);
		int end = -1;
		int inttmp;
		while (begin > -1) {
			end = htmlcode.substring(begin).indexOf(strend);
			if (begin > -1 && end > -1) {
				strtmp = htmlcode.substring(begin, begin + end).toLowerCase();
				inttmp = strtmp.indexOf("charset");
				if (inttmp > -1) {
					pageEncoding = strtmp.substring(inttmp + 7, end).replace("=", "").replace("/", "").replace("\"", "")
							.replace("\'", "").replace(" ", "");
					return pageEncoding;
				}
			}
			htmlcode = htmlcode.substring(begin);
			begin = htmlcode.indexOf(strbegin);
		}
		return pageEncoding;

	}

	public void generateFolder() {
		String oldWorkingDirectory = "";
		String newWorkingDirectory = "";
		String outputFolder = "";

		outputFolder = generateFolderName();
		if (!new File(outputFolder).mkdir()) {
			System.err.println("创建目录错误（Something goes wrong during directory creation!）");
		} else {
			oldWorkingDirectory = System.getProperty("user.dir");
			newWorkingDirectory += oldWorkingDirectory + "/" + outputFolder + "/";
			System.setProperty("user.dir", newWorkingDirectory);
		}

	}

	public String generateFolderName() {
		String outputFolder = "";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		outputFolder += sdf.format(cal.getTime());
		outputFolder += "_";
		outputFolder += url.getHost().replaceAll("\\.", "_").replaceAll("/", "_");

		return outputFolder;
	}

}
