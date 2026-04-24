package com.chitchat.config.db;
import com.chitchat.config.AppConfig;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ConnectES {
    private static final Logger logger = Logger.getLogger(ConnectES.class.getName());

    public TransportClient client;

    private ConnectES() {
        try {
            String host = AppConfig.get("es.host");
            int port = Integer.parseInt(AppConfig.get("es.port"));
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Failed to resolve Elasticsearch host", e);
            throw new RuntimeException("Failed to connect to Elasticsearch", e);
        }
    }

    private static ConnectES es;

    public static ConnectES getInstance() {
        if (es == null) {
            synchronized (ConnectES.class) {
                if (es == null) {
                    es = new ConnectES();
                }
            }
        }
        return es;
    }
}
