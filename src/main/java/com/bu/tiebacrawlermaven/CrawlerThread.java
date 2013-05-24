/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bu.tiebacrawlermaven;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author fan0114
 */
public class CrawlerThread extends Thread {

    public final String pathname;
    private final String url;
    private final String[] dummy;
    private final int dummyNum;

    public CrawlerThread(String url, String pathname, String[] dummy, int dummyNum) {
        this.url = url;
        this.pathname = pathname;
        this.dummy = dummy;
        this.dummyNum = dummyNum;
    }

    @Override
    public void run() {
        try {
            craw(this.url);
        } catch (IOException ex) {
            Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void craw(String url) throws IOException, InterruptedException {

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        ClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(cm);

        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22").header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3").header("Accept-Encoding", "gzip, deflate").header("Cookie", dummy[dummyNum]).header("Connection", "keep-alive").timeout(10000).get();
            System.out.println("URI: " + doc.baseUri());
            while (doc.baseUri().contains("ressafe")) {
                System.out.println("REDO!!");
                Thread.sleep(1000);
                doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22").header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3").header("Accept-Encoding", "gzip, deflate").header("Cookie", dummy[dummyNum]).header("Connection", "keep-alive").header("Referer",doc.baseUri()).timeout(10000).get();
                System.out.println("URI: " + doc.baseUri());
            }
        } catch (IOException ex) {
            System.err.println("ERROR connect to " + url);
            Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        String title = doc.title();
        System.out.println(title);

//        File dir = new File(this.pathname + title + "/");
//        dir.mkdir();
        Elements images = doc.getElementsByClass("BDE_Image");

        GetThread[] threads = new GetThread[200];
        int count = 0;
        for (int i = 0; i < images.size(); i++) {
            Element image = images.get(i);
            String linkHref = image.attr("src");
            String linkText = image.text();
            System.out.println(linkHref);
//            System.out.println(linkText);

            File file = new File(this.pathname + title + "_" + i + ".jpg");
            if (file.createNewFile()) {
                HttpGet httpget = new HttpGet(linkHref);
                threads[count] = new GetThread(httpClient, httpget, file);
                count++;
            }
        }

        // start the threads
        for (int j = 0; j < count; j++) {
            threads[j].start();
        }

        // join the threads
        for (int j = 0; j < count; j++) {
            threads[j].join();
        }


    }
}
