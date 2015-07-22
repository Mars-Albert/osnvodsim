package osnvodsim.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

    // private String[] paths;
    private String filePath;
    private Document doc;
    private Element root;

    public XMLParser(String paths) {

        filePath = paths;
        try {
            buildDocument();
            //	printXML();
        } catch (Exception e) {

        }
        // testXML();

    }

	/*
     * private void testXML() { NodeList nodes = root.getChildNodes(), nodesI,
	 * nodesII; Node node;
	 * 
	 * System.out.println("<" + root.getNodeName() + ">" +
	 * root.getChildNodes().getLength());
	 * 
	 * for (int i = 0; i < nodes.getLength(); i++) { node = nodes.item(i);
	 * System.out.println("<" + node.getNodeName() + ">" +
	 * node.getChildNodes().getLength());
	 * 
	 * if (node.getNodeType() == Node.ELEMENT_NODE) ; else if
	 * (node.getNodeType() == Node.TEXT_NODE) //
	 * System.out.print("text:"+node.getTextContent()); System.out.print("text:"
	 * + node.getChildNodes().getLength()); }
	 * 
	 * }
	 */

    private void printXML() { // 最多显示深度为4

        NodeList nodes = root.getChildNodes(), nodesI, nodesII, nodesIII;
        Node node, nodeI, nodeII, nodeIII;

        System.out.println("config file: " + filePath
                + "\n---------------------------------------------------");

        System.out.println("<" + root.getNodeName() + ">");
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                System.out.println("\t<" + node.getNodeName() + "> ");

                nodesI = node.getChildNodes(); // !!!

                for (int j = 0; j < nodesI.getLength(); j++) {

                    nodeI = nodesI.item(j);
                    if (nodeI.getNodeType() == Node.ELEMENT_NODE)

                    {

                        System.out.print("\t\t<" + nodeI.getNodeName() + ">");
                        if (nodeI.getChildNodes().getLength() == 1)
                            System.out.print(nodeI.getFirstChild()
                                    .getNodeValue());
                        else {

                            System.out.println("");
                            nodesII = nodeI.getChildNodes();

                            for (int k = 0; k < nodesII.getLength(); k++) {

                                nodeII = nodesII.item(k);

                                if (nodeII.getNodeType() == Node.ELEMENT_NODE) {

                                    System.out.print("\t\t\t<"
                                            + nodeII.getNodeName() + ">");
                                    if (nodeII.getChildNodes().getLength() == 1)
                                        System.out.print(nodeII.getFirstChild()
                                                .getNodeValue());
                                    else {
                                        System.out.println("");
                                        nodesIII = nodeII.getChildNodes();
                                        for (int l = 0; l < nodesIII
                                                .getLength(); l++) {
                                            nodeIII = nodesIII.item(l);

                                            if (nodeIII.getNodeType() == Node.ELEMENT_NODE)

                                                System.out
                                                        .println("\t\t\t\t<"
                                                                + nodeIII
                                                                .getNodeName()
                                                                + ">"
                                                                + nodeIII
                                                                .getFirstChild()
                                                                .getNodeValue()
                                                                + "</"
                                                                + nodeIII
                                                                .getNodeName()
                                                                + ">");

                                        }
                                        System.out.print("\t\t\t");
                                    }
                                    System.out.println("</"
                                            + nodeII.getNodeName() + ">");

                                }

                            }

                            System.out.print("\t\t");
                        }
                        System.out.println("</" + nodeI.getNodeName() + ">");

                    }
                }

                System.out.println("\t<" + node.getNodeName() + "> ");

            }

        }

        System.out.println("</" + root.getNodeName() + ">");
        System.out
                .println("---------------------------------------------------");
    }

    private void buildDocument() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.parse(new File(filePath));

            root = doc.getDocumentElement();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("construct documentbuilder error" + e);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            System.err.println("parse xml file error" + e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("read xml error" + e);
        }

    }

    public boolean containsElement(String tagName) {
        if (doc.getElementsByTagName(tagName).getLength() == 0)
            return false;
        else
            return true;
    }

    public String getValue(String tagName) {

        if (!containsElement(tagName))
            return null;

        NodeList nds = doc.getElementsByTagName(tagName);
        Node nd;
        int cnt = 1;
        String str = null;

        for (int i = 0; i < nds.getLength(); i++) {
            nd = nds.item(i);

            if (nd.getNodeType() == Node.ELEMENT_NODE) {

                if (cnt > 0)
                    cnt--;
                else {
                    System.out.println(tagName + ": invalid single value!");
                    return null;
                }

                if (nd.getChildNodes().getLength() == 1) {
                    // System.out.println(tagName+": "+nd.getFirstChild().getNodeValue());
                    str = nd.getFirstChild().getNodeValue();
                } else {
                    System.out.println(tagName + " is not a leaf node!");
                    return null;
                }
            }

        }
        // System.out.println(tagName+": "+str);
        return str;

    }

    public String[] getValues(String tagName) {

        if (!containsElement(tagName))
            return null;

        NodeList nds = doc.getElementsByTagName(tagName);
        Node nd;
        String tmp[];
        int size = 0;

        for (int i = 0; i < nds.getLength(); i++) {
            nd = nds.item(i);
            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                size++;
            }

        }
        tmp = new String[size];

        for (int i = 0; i < nds.getLength(); i++) {
            nd = nds.item(i);
            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                //	 	System.out.println(nd.getTextContent().trim());
                tmp[i] = nd.getTextContent().trim();
            }


        }

        return tmp;


    }


    public Map<String, String> getValueGroup(String tagName) // 获取一组值，参数为 父标签名
    {

        if (!containsElement(tagName))
            return null;

        NodeList nds = doc.getElementsByTagName(tagName);
        Node nd, ndt;
        boolean valid = false;

        Map<String, String> hm = new HashMap<String, String>();

        for (int i = 0; i < nds.getLength(); i++) {
            nd = nds.item(i);
            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                ndt = nd.getFirstChild();

                while (ndt != null) {
                    if (ndt.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(ndt.getNodeName() + ": "
                                + ndt.getFirstChild().getNodeValue());

                        hm.put(ndt.getNodeName(), ndt.getFirstChild()
                                .getNodeValue());

                        valid = true;
                    }
                    ndt = ndt.getNextSibling();
                }

            }

        }
        if (!valid)
            System.out
                    .println("invalid tagname to get group values!,can't be a leaf node"
                            + tagName);

        return hm;

    }

    public void search(String tagName, String tagValue) // get all information
    // where <tagName>'s
    // value==tagValue
    {
        if (!containsElement(tagName))
            return;

        NodeList nds = doc.getElementsByTagName(tagName);
        Node nd, ndt;

        for (int i = 0; i < nds.getLength(); i++) {
            nd = nds.item(i);

            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                if (nd.getChildNodes().getLength() == 1) {

                    if (nd.getFirstChild().getNodeValue().equals(tagValue)) {
                        ndt = nd.getNextSibling();
                        System.out.println(nd.getNodeName() + " :"
                                + nd.getTextContent());
                        while (ndt != null) {
                            if (ndt.getNodeType() == Node.ELEMENT_NODE)
                                System.out.println(ndt.getNodeName() + " :"
                                        + ndt.getTextContent());
                            ndt = ndt.getNextSibling();
                        }
                        break;
                    }

                } else
                    System.out.println(tagName + " is not a leaf node!");

            }
            if (i == nds.getLength() - 1)
                System.out.println("not found!");

        }

    }

	/*
	 * public void getValues(String tagName,String tagValue) //get all
	 * information where <tagName>'s value==tagValue {
	 * if(!containsElement(tagName)) return;
	 * 
	 * NodeList nds=doc.getElementsByTagName(tagName),nds2; Node nd,ndp,ndt;
	 * 
	 * for(int i=0;i<nds.getLength();i++) { nd=nds.item(i);
	 * 
	 * if(nd.getNodeType()==Node.ELEMENT_NODE) {
	 * if(nd.getChildNodes().getLength()==1) {
	 * 
	 * if(nd.getFirstChild().getNodeValue().equals(tagValue)) {
	 * 
	 * ndp=nd.getParentNode(); nds2=ndp.getChildNodes();
	 * 
	 * System.out.println(ndp.getNodeName()); for(int
	 * j=0;j<nds2.getLength();j++) { ndt=nds2.item(j);
	 * if(ndt.getNodeType()==Node.ELEMENT_NODE)
	 * 
	 * System.out.println(ndt.getNodeName()+": "+ndt.getFirstChild().getNodeValue
	 * ());
	 * 
	 * }
	 * 
	 * 
	 * break;
	 * 
	 * }
	 * 
	 * 
	 * } else System.out.println(tagName+" is not a leaf node!");
	 * 
	 * 
	 * } if(i==nds.getLength()-1) System.out.println("not found!");
	 * 
	 * }
	 * 
	 * 
	 * }
	 */

	/*
	 * public int getInt(String tagName) {
	 * 
	 * String str=getValues(tagName); if(str==null) return -1;
	 * 
	 * int tmp=0; try { tmp=Integer.parseInt(str); } catch(NumberFormatException
	 * e) { System.err.println("can't cast to an integer!"); //
	 * e.printStackTrace(); }
	 * 
	 * 
	 * return tmp; } public long getLong(String tagName) {
	 * 
	 * String str=getValues(tagName); if(str==null) return -1;
	 * 
	 * long tmp=0; try { tmp=Long.parseLong(str); } catch(NumberFormatException
	 * e) { System.err.println("can't cast to a long!"); // e.printStackTrace();
	 * }
	 * 
	 * return tmp;
	 * 
	 * 
	 * } public double getDouble(String tagName) {
	 * 
	 * String str=getValues(tagName); if(str==null) return -1;
	 * 
	 * double tmp=0.0; try { tmp=Double.parseDouble(str); }
	 * catch(NumberFormatException e) {
	 * System.err.println("can't cast to a double!"); // e.printStackTrace(); }
	 * return tmp;
	 * 
	 * }
	 */

}
