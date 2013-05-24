package com.bu.tiebacrawlermaven;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class App {

    public static String pathname = "/Users/fan0114/Desktop/tieba/";
    public static String[] dummy;
    public static int dummyNum;

    public static void analyze(String url) throws IOException, InterruptedException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22").header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3").header("Accept-Encoding", "gzip, deflate").header("Cookie", dummy[dummyNum]).header("Connection", "keep-alive").timeout(10000).get();
        String title = doc.title();
        System.out.println(title);
        Elements sublinks = doc.getElementsByClass("j_th_tit");
        CrawlerThread[] threads = new CrawlerThread[sublinks.size()];
        int count = 0;
        for (int i = 0; i < sublinks.size(); i++) {
            Element sublink = sublinks.get(i);
            if (sublink.hasAttr("href")) {
                String suburl = "http://tieba.baidu.com" + sublink.attr("href") + "?see_lz=1";
                threads[count] = new CrawlerThread(suburl, pathname,dummy,dummyNum);
                count++;
            }
        }

        // start the threads
        for (int j = 0; j < count; j++) {
            threads[j].start();
            Thread.sleep(2000);
        }

//        // join the threads
//        for (int j = 0; j < count; j++) {
//            threads[j].join();
//        }
    }

//    public static void downloadImg(String url, final File file) throws IOException {
//        HttpClient httpclient = new DefaultHttpClient();
//        try {
//            HttpGet httpget = new HttpGet(url);
//
//            System.out.println("executing request " + httpget.getURI());
//
//            // Create a response handler
//            ResponseHandler<Boolean> responseHandler = new ResponseHandler<Boolean>() {
//
//                public Boolean handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                    HttpEntity entity = response.getEntity();
//                    if (entity != null) {
//                        InputStream instream = entity.getContent();
//                        try {
//                            // do something useful
//                            FileOutputStream out = new FileOutputStream(file);
//                            byte[] buffer = new byte[1024];
//                            int count = -1;
//                            while ((count = instream.read(buffer)) != -1) {
//                                out.write(buffer, 0, count);
//                            }
//                            out.flush();
//                            out.close();
//                        } catch (Exception e) {
//                        } finally {
//                            instream.close();
//                        }
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            };
//            Boolean responseBody = httpclient.execute(httpget, responseHandler);
////            System.out.println("----------------------------------------");
////            System.out.println(responseBody);
////            System.out.println("----------------------------------------");         
//        } finally {
//            // When HttpClient instance is no longer needed,
//            // shut down the connection manager to ensure
//            // immediate deallocation of all system resources
//            httpclient.getConnectionManager().shutdown();
//        }
//    }
    public static void main(String[] args) throws InterruptedException, IOException {
        dummy=new String[2];
        dummy[0]="TIEBAUID=cb23caae14130a0d384a57f1; TIEBA_USERTYPE=8e3276f7975830f620f52151; wise_device=0; BAIDUID=388C9F5B525D0E843AB723B2C5FE6230:FG=1; BDUT=ymj52AD1D4FFBCA4BBEDB8AC26FD63EB8F0013a2f0adf3a0; bdshare_firstime=1353881763159; interestSmiley=hide";
        dummy[1]="BAIDUID=31E93DADDE2FC23D2A1E25067B93BC32:FG=1; TIEBA_USERTYPE=51214246ee74a6c2730a7145; wise_device=0; bdshare_firstime=1354523435739; TIEBAUID=cb23caae14130a0d384a57f1; BDUT=tvlx31E93DADDE2FC23D2A1E25067B93BC3213baff187790; TB_OFRS=; BAIDUVERIFY=44AF71ADA5D170DAF890D893CF2A27B4E682AAA8D9382536B12DF3D826083BAECCD5F0DF5E0DDFD1F79FC7C829BB04BD66967E2F974F329590433A21073DAFDA4300:1369368624:43dfcf2f4a3b7027";


        System.out.println("Hello World! " + System.getProperty("user.dir"));
        int startPage = 0;
        int maxPages = 10;
        int pageSize = 50;
        pathname = System.getProperty("user.dir") + "/";
        if (args.length >= 2) {
            startPage = Integer.parseInt(args[0]);
            maxPages = Integer.parseInt(args[1]);
        }
        
        String mainURL;
        if (args.length >= 3) {
            mainURL = args[2]+"&tp=0&pn=";
        }else{
            System.err.println("insufficient argument");
            return;
        }
        dummyNum=0;
        if (args.length >= 4) {
            dummyNum = Integer.parseInt(args[3]);
        }
        
        for (int i = startPage; i < startPage + maxPages; i++) {
            String tempURL = mainURL + (i * pageSize);
            System.out.println("TempURL: " + tempURL);
            analyze(tempURL);
        }
//        try {
//            CrawlerThread ct = new CrawlerThread("", pathname);
//            ct.craw(url);
//        } catch (IOException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}
