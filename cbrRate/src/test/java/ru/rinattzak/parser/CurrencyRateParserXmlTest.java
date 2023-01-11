package ru.rinattzak.parser;

import org.junit.jupiter.api.Test;
import ru.rinattzak.model.CurrencyRate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CurrencyRateParserXmlTest {

    @Test
   void parseTest() throws URISyntaxException, IOException {
       CurrencyRateParserXml parser = new CurrencyRateParserXml();
       URI uri = ClassLoader.getSystemResource("XML_daily.xml").toURI();
       String ratesXml = Files.readString(Paths.get(uri), Charset.forName("windows-1251"));

       List<CurrencyRate> currencyRateList = parser.parse(ratesXml);

        assertThat(currencyRateList.size()).isEqualTo(34);
        assertThat(currencyRateList.contains(getBelRubRate())).isTrue();
        assertThat(currencyRateList.contains(getUSDRate())).isTrue();
        assertThat(currencyRateList.contains(getJPYRate())).isTrue();
   }

   CurrencyRate getUSDRate() {
        return CurrencyRate.builder()
                .numCode("840")
                .charCode("USD")
                .nominal("1")
                .name("Доллар США")
                .value("69,6094")
                .build();
   }

    CurrencyRate getJPYRate() {
        return CurrencyRate.builder()
                .numCode("392")
                .charCode("JPY")
                .nominal("100")
                .name("Японских иен")
                .value("52,8144")
                .build();
    }

    CurrencyRate getBelRubRate() {
        return CurrencyRate.builder()
                .numCode("933")
                .charCode("BYN")
                .nominal("1")
                .name("Белорусский рубль")
                .value("25,9176")
                .build();
    }

}
