package com.User;

import com.Database.*;
import com.Others.*;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashMap;
import java.util.Map;

public class ESUserOperation implements UserOperation
{
    ConnectES es;

    public void getConnection()
    {
        es = ConnectES.getInstance();
    }

    public boolean userExist(String uname)
    {
        SearchResponse response = searchUser(uname);
        return response.getHits().hits().length > 0;
    }

    public boolean userLogin(String uname, String password)
    {
        try {
            SearchResponse response = searchUser(uname);
            Map<String, Object> data = response.getHits().getAt(0).getSource();

            Object[] datum = data.values().toArray();

            if (DataCrypt.decode(datum[0].toString()).equals(password))
                return true;
        }
        catch (Exception e) {
        }
        return false;
    }

    private SearchResponse searchUser(String uname)
    {
        SearchResponse response = es.client.prepareSearch("chatapp")
                .setTypes("user")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("name",uname))
                .setSize(1)
                .execute()
                .actionGet();

        return response;
    }

    public boolean addUser(String uname, String password)
    {
        Map<String, Object> json = new HashMap<>();
        json.put("name",uname);
        json.put("password",DataCrypt.encode(password));
        json.put("sessionkey",null);

        IndexResponse response = es.client.prepareIndex("chatapp", "user").setSource(json).execute().actionGet();

        if(response.getResult().toString().equals("CREATED"))
            return true;
        return false;
    }

    public boolean checkKey(String uname, String sessionkey)
    {
        try {
            SearchResponse response = searchUser(uname);
            Map<String, Object> data = response.getHits().getAt(0).getSource();

            Object[] datum = data.values().toArray();

            if (datum[1].equals(sessionkey))
                return true;
        }
        catch (Exception e) {
        }
        return false;
    }

    public void setKey(String uname, String sessionkey)
    {
        SearchResponse response = searchUser(uname);
        String id = response.getHits().getAt(0).getId();

        UpdateRequest key = new UpdateRequest("chatapp", "user", id).doc("sessionkey", sessionkey);
        UpdateResponse updatekey = es.client.update(key).actionGet();
    }
}
