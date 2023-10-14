package org.example;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FBAuthorWorksParse {
    @SneakyThrows
    public static void main(String[] args) {
        String startUrl = "https://ficbook.net";

        Document authorPage = Jsoup
                .connect(startUrl + "/authors/2520")
                .userAgent("Chrome/33.0.1750.152 Safari/537.36")
                .get();

        var numberOfWorksElement = authorPage.select("body > div.container > div.col-lg-16.main-container > " +
                "div.book-container > div.book-inner > main#main.clearfix > " +
                "div.main-holder > div.row > div.col-xs-16.col-sm-5.col-md-4.sidebar-sticky > " +
                "nav.sidebar-nav > ul.sidebar-nav-list > li > a > span.counter");
        int numberOfWorks = Integer.parseInt(numberOfWorksElement.get(0).text());
        int numberOfPages = numberOfWorks / 20 + (numberOfWorks % 20 == 0 ? 0 : 1);
        Thread.sleep(2000);
//        System.out.println(numberOfPages);

        for(int i = 2; i <= numberOfPages; i++) {
            Document worksPage = Jsoup
                    .connect(startUrl + "/authors/2520/profile/works?p=" + i)
                    .userAgent("Mozilla/5.0")
                    .get();

            var works = worksPage.select("body > div.container > div.col-lg-16.main-container > " +
                    "div.book-container > div.book-inner > main#main.clearfix > " +
                    "div.main-holder > div.row > div.col-xs-16.col-sm-11.col-md-12 > " +
                    "section#content.profile-container > section > article[class*=fanfic-inline] > " +
                    "div.js-toggle-description > h3.fanfic-inline-title > a.visit-link");
            Thread.sleep(2000);
//            System.out.println(works);
            for (int j = 1; j < works.size(); j++) {
                System.out.println("i = " + i + "  j = " + j);
                Document chaptersPage = Jsoup
                        .connect(startUrl + works.get(j).attr("href"))
                        .userAgent("Mozilla/5.0")
                        .get();
                System.out.println(startUrl + works.get(j).attr("href"));
                System.out.println(works.get(j).text());

                var chapters = chaptersPage.select("body > div.container > div.col-lg-16.main-container > " +
                        "div.book-container > div.book-inner > main#main.clearfix > " +
                        "div > section.chapter-info > div > article.article.mb-15 > " +
                        "ul.list-unstyled.list-of-fanfic-parts.clearfix > li.part > " +
                        "a[class^=part-link]");
                Thread.sleep(2000);

                StringBuilder sb = new StringBuilder();
                if (chapters.size() == 0) {
                    sb.append(getChapterFromUrl(new URL(startUrl + works.get(j).attr("href")))).append("\n\n");
//                    System.out.println(sb);

                }
                else {
                    var namesOfChapter = chapters.select("div.part-title.word-break > h3");
                    for(int k = 0; k < chapters.size(); k++) {
                        sb.append(namesOfChapter.get(k).text()).append("\n\n");
                        System.out.println(namesOfChapter.get(k).text());
                        sb.append(getChapterFromUrl(new URL(startUrl + chapters.get(k).attr("href")))).append("\n\n");
                        System.out.println(sb);
                    }
                }
                FileWriter fileWriter = new FileWriter(works.get(j).text() + ".txt");
                System.out.println(works.get(j).text() + ".txt");
                fileWriter.write(sb.toString());
                fileWriter.flush();
//                System.out.println(chapters.attr("href"));
//                for(int k = 0; k < chapters.size(); k++) {
//                    Document oneChapterPage = Jsoup
//                            .connect(startUrl + "s")
//                            .userAgent("Mozilla/5.0")
//                            .get();
//
//                }
            }
        }
    }

    private static String getChapterFromUrl(URL characterUrl) throws IOException, InterruptedException {

        URLConnection uc = characterUrl.openConnection();
        uc.addRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(uc.getInputStream()));

        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine).append("br2n");
        in.close();

        Document characterPage = Jsoup.parse(sb.toString());

        Elements character = characterPage.select("body > div.container > div.col-lg-16.main-container > " +
                "div.book-container > div.book-inner > main#main.clearfix > " +
                "div > section.chapter-info > div > article.article.mb-15 > " +
                "div#content.js-part-text.part_text.clearfix.js-public-beta-text.js-bookmark-area");
        Thread.sleep(2000);

        return character.text().replaceAll("br2n", "\n");
    }

}
