package com.chitchat.repo.user;
import com.chitchat.config.db.*;
import com.chitchat.config.security.DataCrypt;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ESUserRepo implements UserRepo {
    private static final Logger logger = Logger.getLogger(ESUserRepo.class.getName());

    ConnectES es;

    public void getConnection() {
        es = ConnectES.getInstance();
    }

    public boolean userExist(String uname) {
        try {
            SearchResponse response = searchUser(uname);
            return response.getHits().hits().length > 0;
        } catch (ElasticsearchException e) {
            logger.log(Level.WARNING, "Failed to check user existence in Elasticsearch", e);
            return false;
        }
    }

    public boolean userLogin(String uname, String password) {
        try {
            SearchResponse response = searchUser(uname);
            Map<String, Object> data = response.getHits().getAt(0).getSource();
            String storedHash = data.get("password").toString();
            return DataCrypt.checkPassword(password, storedHash);
        } catch (ElasticsearchException e) {
            logger.log(Level.WARNING, "Elasticsearch error during login for user: " + uname, e);
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "User not found in Elasticsearch: " + uname, e);
        }
        return false;
    }

    private SearchResponse searchUser(String uname) {
        return es.client.prepareSearch("chatapp")
                .setTypes("user")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("name", uname))
                .setSize(1)
                .execute()
                .actionGet();
    }

    public boolean addUser(String uname, String password) {
        try {
            Map<String, Object> json = new HashMap<>();
            json.put("name", uname);
            json.put("password", DataCrypt.hashPassword(password));
            json.put("sessionkey", null);

            IndexResponse response = es.client.prepareIndex("chatapp", "user").setSource(json).execute().actionGet();
            return response.getResult().toString().equals("CREATED");
        } catch (ElasticsearchException e) {
            logger.log(Level.SEVERE, "Failed to add user to Elasticsearch: " + uname, e);
            return false;
        }
    }

    public boolean checkKey(String uname, String sessionkey) {
        try {
            SearchResponse response = searchUser(uname);
            Map<String, Object> data = response.getHits().getAt(0).getSource();
            return sessionkey.equals(data.get("sessionkey"));
        } catch (ElasticsearchException e) {
            logger.log(Level.WARNING, "Elasticsearch error during session key check for user: " + uname, e);
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "User not found when checking session key: " + uname, e);
        }
        return false;
    }

    public void setKey(String uname, String sessionkey) {
        try {
            SearchResponse response = searchUser(uname);
            String id = response.getHits().getAt(0).getId();
            UpdateRequest key = new UpdateRequest("chatapp", "user", id).doc("sessionkey", sessionkey);
            es.client.update(key).actionGet();
        } catch (ElasticsearchException e) {
            logger.log(Level.SEVERE, "Failed to set session key for user: " + uname, e);
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "User not found when setting session key: " + uname, e);
        }
    }
}
