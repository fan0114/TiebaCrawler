/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bu.tiebacrawlermaven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author fan0114
 */
class GetThread extends Thread {

    private final HttpClient httpClient;
    private final HttpContext context;
    private final HttpGet httpget;
    private final File file;

    public GetThread(HttpClient httpClient, HttpGet httpget, File file) {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.httpget = httpget;
        this.file = file;
    }

    @Override
    public void run() {
        try {
            HttpResponse response = this.httpClient.execute(this.httpget, this.context);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // do something useful with the entity
                InputStream instream = entity.getContent();
                try {
                    // do something useful
                    FileOutputStream out = new FileOutputStream(this.file);
                    byte[] buffer = new byte[1024];
                    int count = -1;
                    while ((count = instream.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                    out.flush();
                    out.close();
                } catch (Exception e) {
                } finally {
                    instream.close();
                }
            }
            // ensure the connection gets released to the manager
            EntityUtils.consume(entity);
        } catch (Exception ex) {
            this.httpget.abort();
        }
    }
}
