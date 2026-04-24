package com.Chat;

import com.Database.*;
import com.Others.*;

import org.elasticsearch.action.index.IndexRequest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.SearchHit;

import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.*;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;

import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ESChatOperation implements ChatOperation
{
    ConnectES es;

    public void getConnection()
    {
        es = ConnectES.getInstance();
    }

    static BulkProcessor bulkProcessor;

    private void initBulkProcessor()
    {
        bulkProcessor = BulkProcessor.builder(
                es.client,
                new BulkProcessor.Listener() {
                    public void beforeBulk(long executionId, BulkRequest request) {
                    }

                    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                        System.out.println(response);
                    }

                    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                        System.out.println(failure);
                    }
                })
                .setBulkActions(100)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(3))
                .setConcurrentRequests(1) // no of cores in machine
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
    }

    public ArrayList<String> getAllUsers(String user)
    {
        ArrayList<String> results = new ArrayList<>();

        SearchResponse response = es.client.prepareSearch("chatapp")
                .setTypes("user")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery()
                            .must(matchAllQuery())
                            .mustNot(termQuery("name",user)))
                .setSize(100)
                .execute()
                .actionGet();

        for(SearchHit hit : response.getHits())
        {
            Map<String, Object> data = hit.getSource();

            Object[] datum = data.values().toArray();
            results.add(datum[2].toString());
        }
        return results;
    }

    public void sendMessage(String sender, String receiver, String message)
    {
        Map<String, Object> json = new HashMap<>();
        json.put("sender",sender);
        json.put("receiver",receiver);
        json.put("message",DataCrypt.encode(message));
        json.put("timestamp",getCurrentTime());

        if(bulkProcessor == null)
            initBulkProcessor();

        bulkProcessor.add(new IndexRequest("chatapp", "chats").source(json));
    }

    public ArrayList<String[]> getChats(String sender, String receiver, String lasttime)
    {
        ArrayList<String[]> chats = new ArrayList<>();

        SearchResponse response = es.client.prepareSearch("chatapp")
                .setTypes("chats")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.boolQuery()
                        .must(rangeQuery("timestamp").gt(lasttime))
                        .must(termsQuery("sender",sender,receiver))
                        .must(termsQuery("receiver",sender,receiver)))
                .addSort(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC))
                .setSize(1000)
                .execute()
                .actionGet();

        for(SearchHit hit : response.getHits())
        {
            Map<String, Object> data = hit.getSource();
            Object[] datum = data.values().toArray();

            String[] chat = new String[4];
            chat[0] = datum[0].toString();
            chat[1] = datum[1].toString();
            chat[2] = DataCrypt.decode(datum[2].toString());
            chat[3] = datum[3].toString();

            chats.add(chat);
        }
        return chats;
    }

    private String getCurrentTime()
    {
        LocalDateTime currenttime = LocalDateTime.now();
        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return form.format(currenttime);
    }

     /*public void sendMessage(String sender, String receiver, String message)
    {
        Map<String, Object> json = new HashMap<>();
        json.put("sender",sender);
        json.put("receiver",receiver);
        json.put("message",DataCrypt.encode(message));
        json.put("timestamp",getCurrentTime());

        IndexResponse response = es.client.prepareIndex("chatapp", "chats").setSource(json).execute().actionGet();
    }*/
}
