package com.tencent.tools.wx;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlParseUtil {

	/**
	 * XML格式字符串转换为Map
	 *
	 * @param strXML
	 *            XML字符串
	 * @return XML数据转换后的Map
	 * @throws Exception
	 */
	// TODO 存在inputData则不使用输入流的形式
	public static Map<String, String> xmlToMap(HttpServletRequest request, String inputData) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		InputStream is = request.getInputStream();// 从request中，获取输入流
		Document doc = null;
		System.out.println("inputdata: " + inputData);
		if (inputData != null && !inputData.equals(""))
			doc = DocumentHelper.parseText(inputData);
		else {
			// 从dom4j的jar包中，拿到SAXReader对象。
			SAXReader reader = new SAXReader();
			doc = reader.read(is);// 从reader对象中,读取输入流
		}
		Element root = doc.getRootElement();// 获取XML文档的根元素
		List<Element> list = root.elements();// 获得根元素下的所有子节点
		for (Element e : list) {
			map.put(e.getName(), e.getText());// 遍历list对象，并将结果保存到集合中
		}
		is.close();
		return map;

	}

	/**
	 * XML格式字符串转换为Map
	 *
	 * @param strXML
	 *            XML字符串
	 * @return XML数据转换后的Map
	 * @throws Exception
	 */
	public static Map<String, String> xmlToMap(String strXML) throws Exception {
		try {
			Map<String, String> data = new HashMap<String, String>();
			DocumentBuilder documentBuilder = XmlUtil.newDocumentBuilder();
			InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
			org.w3c.dom.Document doc = documentBuilder.parse(stream);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getDocumentElement().getChildNodes();
			for (int idx = 0; idx < nodeList.getLength(); ++idx) {
				Node node = nodeList.item(idx);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					org.w3c.dom.Element element = (org.w3c.dom.Element) node;
					data.put(element.getNodeName(), element.getTextContent());
				}
			}
			try {
				stream.close();
			} catch (Exception ex) {
				// do nothing
			}
			return data;
		} catch (Exception ex) {
			throw ex;
		}

	}

	/**
	 * 将Map转换为XML格式的字符串
	 *
	 * @param data
	 *            Map类型数据
	 * @return XML格式的字符串
	 * @throws Exception
	 */
	// public static String mapToXml(Map<String, String> data) throws Exception
	// {
	// org.w3c.dom.Document document = XmlUtil.newDocument();
	// org.w3c.dom.Element root = document.createElement("xml");
	// document.appendChild(root);
	// for (String key : data.keySet()) {
	// String value = data.get(key);
	// if (value == null) {
	// value = "";
	// }
	// value = value.trim();
	// org.w3c.dom.Element filed = document.createElement(key);
	// filed.appendChild(document.createTextNode(value));
	// root.appendChild(filed);
	// }
	// TransformerFactory tf = TransformerFactory.newInstance();
	// Transformer transformer = tf.newTransformer();
	// DOMSource source = new DOMSource(document);
	// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	// StringWriter writer = new StringWriter();
	// StreamResult result = new StreamResult(writer);
	// transformer.transform(source, result);
	// String output = writer.getBuffer().toString(); // .replaceAll("\n|\r",
	// // "");
	// try {
	// writer.close();
	// } catch (Exception ex) {
	// }
	// return output;
	// }

	/**
	 * (多层)map转换为xml格式字符串
	 *
	 * @param map
	 *            需要转换为xml的map
	 * @param isCDATA
	 *            是否加入CDATA标识符 true:加入 false:不加入
	 * @return xml字符串
	 */
	public static String mapToXml(Map<String, Object> map, boolean isCDATA) {
		String parentName = "xml";
		Document doc = DocumentHelper.createDocument();
		doc.addElement(parentName);
		String xml = recursionMapToXml(doc.getRootElement(), parentName, map, isCDATA);
		return xml;
	}

	/**
	 * multilayerMapToXml核心方法，递归调用
	 *
	 * @param element
	 *            节点元素
	 * @param parentName
	 *            根元素属性名
	 * @param map
	 *            需要转换为xml的map
	 * @param isCDATA
	 *            是否加入CDATA标识符 true:加入 false:不加入
	 * @return xml字符串
	 */
	@SuppressWarnings("unchecked")
	private static String recursionMapToXml(Element element, String parentName, Map<String, Object> map,
			boolean isCDATA) {
		Element xmlElement = element.addElement(parentName);
		for (String key : map.keySet()) {
			Object obj = map.get(key);
			if (obj instanceof Map) {
				recursionMapToXml(xmlElement, key, (Map<String, Object>) obj, isCDATA);
			} else {
				String value = obj == null ? "" : obj.toString();
				if (isCDATA) {
					xmlElement.addElement(key).addCDATA(value);
				} else {
					xmlElement.addElement(key).addText(value);
				}
			}
		}
		return xmlElement.asXML();
	}
}
