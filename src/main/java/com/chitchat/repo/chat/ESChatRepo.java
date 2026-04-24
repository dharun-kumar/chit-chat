package com.chitchat.repo.chat;
import com.chitchat.config.db.*;
import com.chitchat.model.Chat;
import org.elasticsearch.ElasticsearchException;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ESChatRepo implements ChatRepo {
    private static final Logger logger = Logger.getLogger(ESChatRepo.class.getName());

    ConnectES es;

    public void getConnection() {
        es = ConnectES.getInstance();
    }

    static BulkProcessor bulkProcessor;

    private void initBulkProcessor() {
        bulkProcessor = BulkProcessor.builder(
                es.client,
                new BulkProcessor.Listener() {
                    public void beforeBulk(long executionId, BulkRequest request) {
                    }

                    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                        logger.info("Bulk executed: " + response.buildFailureMessage());
                    }

                    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                        logger.log(Level.SEVERE, "Bulk execution failed", failure);
                    }
                })
                .setBulkActions(100)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(3))
                .setConcurrentRequests(1)
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
    }

    public ArrayList<String> getAllUsers(String user) {
        ArrayList<String> results = new ArrayList<>();
        try {
            SearchResponse response = es.client.prepareSearch("chatapp")
                    .setTypes("user")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.boolQuery()
                            .must(matchAllQuery())
                            .mustNot(termQuery("name", user)))
                    .setSize(100)
                    .execute()
                    .actionGet();

            for (SearchHit hit : response.getHits()) {
                Map<String, Object> data = hit.getSource();
                results.add(data.get("name").toString());
            }
        } catch (ElasticsearchException e) {
            logger.log(Level.WARNING, "Failed to fetch users from Elasticsearch", e);
        }
        return results;
    }

    public void sendMessage(String sender, String receiver, String message) {
        try {
            Map<String, Object> json = new HashMap<>();
            json.put("sender", sender);
            json.put("receiver", receiver);
            json.put("message", message);
            json.put("timestamp", Instant.now().toEpochMilli());

            if (bulkProcessor == null) {
                initBulkProcessor();
            }

            bulkProcessor.add(new IndexRequest("chatapp", "chats").source(json));
        } catch (ElasticsearchException e) {
            logger.log(Level.SEVERE, "Failed to send message to Elasticsearch", e);
        }
    }

    public ArrayList<Chat> getChats(String sender, String receiver, Instant lasttime) {
        ArrayList<Chat> chats = new ArrayList<>();
        try {
            SearchResponse response = es.client.prepareSearch("chatapp")
                    .setTypes("chats")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.boolQuery()
                            .must(rangeQuery("timestamp").gt(lasttime.toEpochMilli()))
                            .must(termsQuery("sender", sender, receiver))
                            .must(termsQuery("receiver", sender, receiver)))
                    .addSort(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC))
                    .setSize(1000)
                    .execute()
                    .actionGet();

            for (SearchHit hit : response.getHits()) {
                Map<String, Object> data = hit.getSource();
                String msgSender = data.get("sender").toString();
                String msgReceiver = data.get("receiver").toString();
                String msgText = data.get("message").toString();
                Instant delivertime = Instant.ofEpochMilli(((Number) data.get("timestamp")).longValue());
                chats.add(new Chat(msgSender, msgReceiver, msgText, delivertime));
            }
        } catch (ElasticsearchException e) {
            logger.log(Level.WARNING, "Failed to fetch chats from Elasticsearch", e);
        }
        return chats;
    }
}
