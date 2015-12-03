package org.yiwan.webcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.xml.utils.DefaultErrorHandler;
import org.testng.Assert;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kenny Wang
 * 
 */
public class Helper {

	public static String toDate(String date, String fromFormat, String toFormat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
		try {
			Date d = dateFormat.parse(date);
			return (new SimpleDateFormat(toFormat)).format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static boolean isDate(String date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				I18N.DATE_PATTERN_EN_US);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void assertFileExists(String filepath) {
		assertFileExists(filepath, true, Property.FILE_ACCESSABLE);
	}

	/**
	 * @param filepath
	 * @param exist
	 * @param timeout
	 */
	public static void assertFileExists(String filepath, Boolean exist,
			int timeout) {
		File file = new File(filepath);
		long curtime = System.currentTimeMillis();
		while (System.currentTimeMillis() - curtime <= timeout * 1000) {
			if (file.exists())
				return;
		}
		Assert.fail("fail to get file " + filepath + " in " + timeout
				+ " seconds");
	}

	public static void deleteFile(String filepath) {
		File file = new File(filepath);
		file.delete();
	}

	public static String randomize() {
		// SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		// return df.format(new Date());
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * @param xmlfile
	 * @param xsdfile
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void validateXml(File xmlfile, File xsdfile) throws Exception {
		final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
		final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);

		SAXParser parser = factory.newSAXParser();
		parser.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
		parser.setProperty(SCHEMA_SOURCE, xsdfile);

		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(new DefaultHandler());
		xmlReader.setErrorHandler(new DefaultErrorHandler());
		xmlReader.parse(xmlfile.getAbsolutePath());
	}

	/**
	 * @param source
	 * @param text
	 * @return
	 */
	public static String getTestReportStyle(String source, String text) {
		return "<a href = 'javascript:void(0)' onclick=\"window.open ('"
				+ source
				+ "','newwindow','height=600,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')\">"
				+ text + "</a>";
	}

	/**
	 * read xml and xls feed mapping rule from a file
	 * 
	 * @param file
	 * @return
	 */
	public static Map<String, Map<String, String>> getFeedMapping(String file) {
		FileInputStream isr = null;
		Reader r = null;
		try {
			isr = new FileInputStream(file);
			r = new InputStreamReader(isr, "utf-8");
			Properties props = new Properties();
			props.load(r);
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Set<Entry<Object, Object>> entrySet = props.entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				if (!entry.getKey().toString().startsWith("#")) {
					Map<String, String> m = new HashMap<String, String>();
					String s = ((String) entry.getValue()).trim();
					s = s.substring(1, s.length() - 1);
					String[] kvs = s.split(",");
					for (String kv : kvs) {
						m.put(kv.substring(0, kv.indexOf('=')).trim(), kv
								.substring(kv.indexOf('=') + 1).trim());
						map.put(((String) entry.getKey()).trim(), m);
					}
				}
			}
			return map;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * get test case logger so as to generate test case log one by one
	 * 
	 * @param claz
	 * @return
	 */
	public static Logger getTestCaseLogger(Class<?> claz) {
		Logger logger = Logger.getLogger(claz);

		logger.removeAllAppenders();
		logger.setLevel(Level.DEBUG);
		logger.setAdditivity(true);

		FileAppender appender = new RollingFileAppender();
		appender.setName(claz.getSimpleName());
		PatternLayout layout = new PatternLayout();
		String conversionPattern = "%d %-5p [%c] (%t:%x) %m%n";
		layout.setConversionPattern(conversionPattern);
		appender.setLayout(layout);
		appender.setFile("target/logs/" + claz.getSimpleName() + ".log");
		appender.setEncoding("UTF-8");
		appender.setAppend(false);
		appender.activateOptions();
		logger.addAppender(appender);

		return logger;
	}
}