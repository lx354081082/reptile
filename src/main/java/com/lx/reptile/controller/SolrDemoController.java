package com.lx.reptile.controller;

import com.lx.reptile.pojo.DouyuBarrage;
import com.lx.reptile.service.DouyuBarrageService;
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
    @Autowired
    private DouyuBarrageService douyuBarrageService;

    @RequestMapping("/add")
    public Object add() throws IOException, SolrServerException {
        for (DouyuBarrage douyuBarrage : douyuBarrageService.get(0, 10000)) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.setField("b_id", douyuBarrage.getId());
            doc.setField("b_txt", douyuBarrage.getTxt());
            doc.setField("b_uid", douyuBarrage.getUid());
            doc.setField("b_rid", douyuBarrage.getRoomid());
            solrClient.add(doc);
        }
        solrClient.commit();
        return "true";
    }

}
