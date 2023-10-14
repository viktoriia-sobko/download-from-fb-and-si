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

public class SIAuthorWorksParse {
    @SneakyThrows
    public static void main(String[] args) {
        String startUrl = "http://samlib.ru/s/strannik9000/";

        Document worksPage = Jsoup
                .connect(startUrl)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .get();

        var works = worksPage.select("body > li > dl > dl > dt > li > a");
//            System.out.println(works);
        for (int i = 0; i < works.size(); i++) {
            System.out.println("i = " + i);
            Document chapterPage = Jsoup
                    .connect(startUrl + works.get(i).attr("href"))
                    .userAgent("Mozilla/5.0")
                    .get();
            System.out.println(startUrl + works.get(i).attr("href"));
            System.out.println(works.get(i));

            var chapter = chapterPage.select("body > div > xxx7 > dd");

            if(chapter.size() == 0) {
                chapter = chapterPage.select("body > dd");
            }
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < chapter.size(); j++){
                sb.append(chapter.get(j).text()).append("\n");
            }
            FileWriter fileWriter = new FileWriter(works.get(i).text() + ".txt");
            fileWriter.write(sb.toString());
            fileWriter.flush();
            System.out.println(sb);
        }
    }
}
