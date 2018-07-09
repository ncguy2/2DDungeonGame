package net.ncguy.ability.loader;

import net.ncguy.ability.Ability;
import net.ncguy.ability.AbilityLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.ncguy.ability.AbilityLevel.Cantrip;
import static org.w3c.dom.Node.ELEMENT_NODE;

public class AbilityXmlLoader {

    public final String xml;

    public AbilityXmlLoader(String xml) {
        this.xml = xml;
    }

    public List<Ability> Parse() {
        List<Ability> abilities = new ArrayList<>();

        try {
            ParseImpl(abilities);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return abilities;
    }

    public void ParseImpl(List<Ability> abilities) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xml)));


        NodeList childNodes = doc.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if(node.getNodeType() == ELEMENT_NODE)
                ParseNode((Element) node, abilities);
        }
    }

    public void ParseNode(Element element, List<Ability> abilities) {
        String tagName = element.getLocalName();
        if(tagName.equalsIgnoreCase("AbilitySet"))
            ParseAbilitySetTag(element, abilities);
        else if(tagName.equalsIgnoreCase("AbilityGroup"))
            ParseAbilityGroupTag(element, abilities);
        else if(tagName.equalsIgnoreCase("Ability"))
            ParseAbilityTag(element, abilities);
    }

    public void ParseAbilitySetTag(Element root, List<Ability> abilities) {
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if(node.getNodeType() == ELEMENT_NODE)
                ParseNode((Element) node, abilities);
        }
    }

    public void ParseAbilityGroupTag(Element root, List<Ability> abilities) {
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if(node.getNodeType() == ELEMENT_NODE)
                ParseNode((Element) node, abilities);
        }
    }

    public void ParseAbilityTag(Element root, List<Ability> abilities) {
        Ability ability = new Ability();

        ability.name = GetTagContents(root, "Name", 0).orElse("Unknown");
        ability.icon = GetTagContents(root, "Icon", 0).orElse("Unknown");
        ability.level = GetTagContents(root, "Level", 0).map(AbilityLevel::valueOf).orElse(Cantrip);
        ability.scriptPath = GetTagContents(root, "Script", 0).orElse("");
        // TODO add loading for cost and requirements

        abilities.add(ability);
    }

    Optional<String> GetTagContents(Element parent, String tagName) {
        return GetTagContents(parent, tagName, 0);
    }
    Optional<String> GetTagContents(Element parent, String tagName, int idx) {
        NodeList list = parent.getElementsByTagName(tagName);
        if(list.getLength() == 0 || list.getLength() <= idx)
            return Optional.empty();

        Node node = list.item(idx);
        if(node == null)
            return Optional.empty();

        return Optional.ofNullable(node.getTextContent());
    }

//    public void ParseImpl(List<Ability> abilityList) throws FileNotFoundException, XMLStreamException {
//        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
//        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));
//        while(reader.hasNext())
//            Handle(reader, reader.nextEvent(), abilityList);
//    }
//
//    public void Handle(XMLEventReader reader, XMLEvent event, List<Ability> abilityList) {
//        if(event.isStartElement()) {
//            String localPart = event.asStartElement()
//                    .getName()
//                    .getLocalPart();
//            if(handlers.containsKey(localPart)) {
//                handlers.get(localPart).Parse(this, reader, event);
//            }
//        }
//    }

}
