package ru.rinattzak.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.rinattzak.exception.CurrencyRateParsingException;
import ru.rinattzak.model.CurrencyRate;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CurrencyRateParserXml implements CurrencyRateParser {
    @Override
    public List<CurrencyRate> parse(String rateAsString) {
        var rates = new ArrayList<CurrencyRate>();
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            var db = dbf.newDocumentBuilder();

            try (var reader = new StringReader(rateAsString)) {
                Document doc = db.parse(new InputSource(reader));
                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("Valute");

                for (var valuteIdx = 0; valuteIdx < nodeList.getLength(); valuteIdx++) {
                    var node = nodeList.item(valuteIdx);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        var element = (Element) node;

                        var rate = CurrencyRate.builder()
                                .numCode(element.getElementsByTagName("NumCode").item(0).getTextContent())
                                .charCode(element.getElementsByTagName("CharCode").item(0).getTextContent())
                                .nominal(element.getElementsByTagName("Nominal").item(0).getTextContent())
                                .name(element.getElementsByTagName("Name").item(0).getTextContent())
                                .value(element.getElementsByTagName("Value").item(0).getTextContent())
                                .build();
                        rates.add(rate);
                    }
                }
            }
        } catch (Exception e) {
            log.error("xml parsing error =(, xml:{}", rateAsString, e);
            throw new CurrencyRateParsingException(e);
        }
        return rates;
    }
}
