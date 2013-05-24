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
            doc = Jsoup.connect(url).userAgent(dummy[dummyNum]).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3").header("Accept-Encoding", "gzip, deflate").header("Cookie", "BAIDUID=31E93DADDE2FC23D2A1E25067B93BC32:FG=1; TIEBA_USERTYPE=51214246ee74a6c2730a7145; wise_device=0; bdshare_firstime=1354523435739; TIEBAUID=cb23caae14130a0d384a57f1; BDUT=tvlx31E93DADDE2FC23D2A1E25067B93BC3213baff187790; TB_OFRS=; BAIDUVERIFY=44AF71ADA5D170DAF890D893CF2A27B4E682AAA8D9382536B12DF3D826083BAECCD5F0DF5E0DDFD1F79FC7C829BB04BD66967E2F974F329590433A21073DAFDA4300:1369368624:43dfcf2f4a3b7027").header("Connection", "keep-alive").timeout(10000).get();
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
