package com.lx.reptile.controller;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.UUID;

@Controller
@ResponseBody
@RequestMapping("/solr")
public class SolrDemoController {
    @Autowired
    private SolrClient solrClient;

    @RequestMapping("/add")
    public Object add() throws IOException, SolrServerException {
        String uuid = UUID.randomUUID().toString();
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", uuid);
        doc.setField("content_ik", "我是中国人, 我爱中国");
        solrClient.add(doc);
        solrClient.commit();
        return uuid;
    }
}
