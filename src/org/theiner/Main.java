package org.theiner;

import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2DOM;
import org.ccil.cowan.tagsoup.Parser;
import org.theiner.data.Film;
import org.theiner.data.Serie;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36";

    public static void main(String[] args) {
        //String HTML = getHtmlFromUrl("http://www.kinox.to/aGET/MirrorByEpisode/?Addr=Heroes&SeriesID=2340&Season=5&Episode=1");

        List<Film> filme = getFilme();
        List<Serie> serien = getSerien();


        System.out.println("---------------- Filme ------------------");
        for(Film film : filme) {
            Document doc = getDocumentFromUrl("http://www.kinox.to/Stream/" + film.toQueryString());
            if(doc!=null) {
                Element element = doc.getElementById("HosterList");

                NodeList liElements = element.getElementsByTagName("li");

                String currentDateStr = getMaxDateFromElements(liElements);

                // Vergleichen mit vorherigem Datum
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                Date currentDate = null;
                Date lastDate = null;
                try {
                    currentDate = sdf.parse(currentDateStr);
                    lastDate = sdf.parse(film.getLastDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(lastDate!=null && currentDate.after(lastDate)) {
                    System.out.println();
                    System.out.println(film.getName() + ": " + currentDateStr);
                } else {
                    System.out.print(".");
                }
            } else {
                System.out.print(".");
            }
        }

        System.out.println();
        System.out.println("---------------- Serien ------------------");
        for(Serie serie : serien) {
            String queryString = serie.toQueryString();

            String HTML = getHtmlFromUrl("http://www.kinox.to/aGET/MirrorByEpisode/" + queryString);
            Document serienDoc = getDocumentFromHTML("<html><body>" + HTML + "</body></html>");

            Element element = serienDoc.getElementById("HosterList");

            if (element != null) {
                NodeList liElements = element.getElementsByTagName("li");

                String currentDateStr = getMaxDateFromElements(liElements);

                System.out.println();
                System.out.println(serie.toString() + ": " + currentDateStr);
            } else {
                System.out.print(".");
            }
        }

    }

    private static String getMaxDateFromElements(NodeList liElements) {
        Pattern pattern = Pattern.compile("Vom\\:\\s(\\d{2}\\.\\d{2}\\.\\d{4})");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date currentDate = new Date(100, 0, 1);
        Date heute = new Date();

        for (int i = 0; i < liElements.getLength(); i++) {
            Element linode = (Element) liElements.item(i);
            String textToParse = linode.getTextContent();

            Matcher matcher = pattern.matcher(textToParse);
            while (matcher.find()) {
                try {
                    Date myDate = sdf.parse(matcher.group(1));
                    if (myDate.before(heute)) {
                        // kinox = 10 Tage draufrechnen!
                        myDate.setTime(myDate.getTime() + 10 * 24 * 60 * 60 * 1000);
                    }

                    if (myDate.after(currentDate))
                        currentDate = myDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return sdf.format(currentDate);
    }

    private static List<Serie> getSerien() {
        List<Serie> result = new ArrayList<>();

        Serie twd = new Serie();
        twd.setName("The Walking Dead");
        twd.setAddr("The_Walking_Dead-1");
        twd.setSeriesID(10437);
        twd.setSeason(6);
        twd.setEpisode(15);
        result.add(twd);

        Serie shameless = new Serie();
        shameless.setName("Shameless");
        shameless.setAddr("Shameless-2");
        shameless.setSeriesID(39278);
        shameless.setSeason(6);
        shameless.setEpisode(1);
        result.add(shameless);

        Serie sn_en = new Serie();
        sn_en.setName("Supernatural (en)");
        sn_en.setAddr("Supernatural_german_subbed");
        sn_en.setSeriesID(27249);
        sn_en.setSeason(11);
        sn_en.setEpisode(16);
        result.add(sn_en);

        Serie sn_de = new Serie();
        sn_de.setName("Supernatural (de)");
        sn_de.setAddr("Supernatural");
        sn_de.setSeriesID(2375);
        sn_de.setSeason(11);
        sn_de.setEpisode(1);
        result.add(sn_de);

        Serie vikings = new Serie();
        vikings.setName("Vikings");
        vikings.setAddr("Vikings-1");
        vikings.setSeriesID(46277);
        vikings.setSeason(4);
        vikings.setEpisode(1);
        result.add(vikings);

        Serie devious = new Serie();
        devious.setName("Devious Maids");
        devious.setAddr("Devious_Maids-1");
        devious.setSeriesID(47777);
        devious.setSeason(4);
        devious.setEpisode(1);
        result.add(devious);

        Serie feartwd = new Serie();
        feartwd.setName("Fear The Walking Dead");
        feartwd.setAddr("Fear_the_Walking_Dead-1");
        feartwd.setSeriesID(55976);
        feartwd.setSeason(2);
        feartwd.setEpisode(1);
        result.add(feartwd);

        Serie tmithc = new Serie();
        tmithc.setName("The Man in the High Castle");
        tmithc.setAddr("The_Man_in_the_High_Castle");
        tmithc.setSeriesID(64040);
        tmithc.setSeason(2);
        tmithc.setEpisode(1);
        result.add(tmithc);

        Serie bcs = new Serie();
        bcs.setName("Better Call Saul");
        bcs.setAddr("Better_Call_Saul");
        bcs.setSeriesID(54593);
        bcs.setSeason(2);
        bcs.setEpisode(7);
        result.add(bcs);

        Serie ol = new Serie();
        ol.setName("Outlander");
        ol.setAddr("Outlander-3");
        ol.setSeriesID(54425);
        ol.setSeason(2);
        ol.setEpisode(1);
        result.add(ol);

        Serie ahs = new Serie();
        ahs.setName("American Horror Story");
        ahs.setAddr("American_Horror_Story-Die_dunkle_Seite_in_dir-1");
        ahs.setSeriesID(37361);
        ahs.setSeason(6);
        ahs.setEpisode(1);
        result.add(ahs);

        Serie tbbt = new Serie();
        tbbt.setName("The Big Bang Theory");
        tbbt.setAddr("The_Big_Bang_Theory_german_subbed");
        tbbt.setSeriesID(27242);
        tbbt.setSeason(9);
        tbbt.setEpisode(19);
        result.add(tbbt);

        return result;
    }

    private static List<Film> getFilme() {
        List<Film> result = new ArrayList<>();

        Film testament = new Film();
        testament.setName("Das brandneue Testament");
        testament.setAddr("Das_brandneue_Testament");
        testament.setLastDate("25.02.2016");
        result.add(testament);

        Film deadpool = new Film();
        deadpool.setName("Deadpool");
        deadpool.setAddr("Deadpool");
        deadpool.setLastDate("12.03.2016");
        result.add(deadpool);

        Film zoomania = new Film();
        zoomania.setName("Zoomania");
        zoomania.setAddr("Zoomania");
        zoomania.setLastDate("20.03.2016");
        result.add(zoomania);

        Film allegiant = new Film();
        allegiant.setName("Die Bestimmung-Allegiant Part 1");
        allegiant.setAddr("Die_Bestimmung-Allegiant_Part_1");
        allegiant.setLastDate("23.03.2016");
        result.add(allegiant);

        return result;
    }

    private static Document getDocumentFromHTML(String html) {
        Parser p = new Parser();
        SAX2DOM sax2dom;
        Document doc  = null;

        try {

            p.setFeature(Parser.namespacesFeature, false);
            p.setFeature(Parser.namespacePrefixesFeature, false);
            sax2dom = new SAX2DOM(true);
            p.setContentHandler(sax2dom);
            p.parse(new InputSource(new InputStreamReader(new ByteArrayInputStream(html.getBytes(Charset.defaultCharset())))));

            doc = (Document) sax2dom.getDOM();
        } catch (Exception e) {
            // TODO handle exception
        }


        return doc;
    }

    private static Document getDocumentFromUrl(String strUrl) {
        Parser p = new Parser();
        SAX2DOM sax2dom;
        Document doc  = null;

        try {

            URL url = new URL(strUrl);
            URLConnection con = url.openConnection();

            // force server to mimic specific Browser
            con.setRequestProperty("User-Agent", userAgent);

            con.setReadTimeout(15000);
            con.connect();

            p.setFeature(Parser.namespacesFeature, false);
            p.setFeature(Parser.namespacePrefixesFeature, false);
            sax2dom = new SAX2DOM(true);
            p.setContentHandler(sax2dom);
            p.parse(new InputSource(new InputStreamReader(con.getInputStream())));

            doc = (Document) sax2dom.getDOM();
        } catch (Exception e) {
            // TODO handle exception
        }


        return doc;
    }

    private static String getHtmlFromUrl(String strUrl) {
        URL url;
        BufferedReader reader = null;
        StringBuilder sb;
        String returnValue = "";

        try {
            url = new URL(strUrl);
            URLConnection con = url.openConnection();

            // force server to mimic specific Browser
            con.setRequestProperty("User-Agent", userAgent);

            con.setReadTimeout(15000);
            con.connect();

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            sb = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            returnValue = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnValue;
    }
}
