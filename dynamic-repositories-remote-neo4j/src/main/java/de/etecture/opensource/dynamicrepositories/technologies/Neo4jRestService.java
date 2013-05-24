package de.etecture.opensource.dynamicrepositories.technologies;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import static org.codehaus.jackson.JsonToken.END_ARRAY;
import static org.codehaus.jackson.JsonToken.END_OBJECT;
import static org.codehaus.jackson.JsonToken.START_ARRAY;
import static org.codehaus.jackson.JsonToken.START_OBJECT;
import static org.codehaus.jackson.JsonToken.VALUE_FALSE;
import static org.codehaus.jackson.JsonToken.VALUE_NULL;
import static org.codehaus.jackson.JsonToken.VALUE_NUMBER_FLOAT;
import static org.codehaus.jackson.JsonToken.VALUE_NUMBER_INT;
import static org.codehaus.jackson.JsonToken.VALUE_STRING;
import static org.codehaus.jackson.JsonToken.VALUE_TRUE;

/**
 *
 * @author rhk
 */
@Singleton
public class Neo4jRestService {

    @Resource(name = "neo4j-server-url")
    String neo4jURL;
    private Client client = Client.create();
    private WebResource clientResource;

    @PostConstruct
    void init() {
        if (neo4jURL == null || neo4jURL.trim().length() == 0) {
            throw new IllegalArgumentException("Env-Entry 'neo4j-server-url' must be specified in the format http://host:port/db/data!");
        }
        client.setFollowRedirects(true);
        clientResource = client.resource(neo4jURL);
    }

    private Object readObject(JsonParser p) throws IOException {
        switch (p.getCurrentToken()) {
            case VALUE_FALSE:
                return false;
            case VALUE_TRUE:
                return true;
            case VALUE_NULL:
                return null;
            case VALUE_STRING:
                return p.getText();
            case VALUE_NUMBER_INT:
                return p.getIntValue();
            case VALUE_NUMBER_FLOAT:
                return p.getFloatValue();
            default:
                throw new IOException(String.format("cannot handle: %s at: %s", p.getCurrentToken().name(), p.getCurrentLocation()));
        }
    }

    private Map readMap(JsonParser p) throws IOException {
        Map map = new HashMap();
        outer:
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String key = p.getCurrentName();
            switch (p.nextToken()) {
                case START_OBJECT:
                    map.put(key, readMap(p));
                    break;
                case START_ARRAY:
                    map.put(key, readArray(p));
                    break;
                case END_ARRAY:
                case END_OBJECT:
                    break outer;
                default:
                    map.put(key, readObject(p));
            }
        }
        return map;
    }

    private List readArray(JsonParser p) throws IOException {
        List array = new ArrayList();

        outer:
        while (p.nextToken() != JsonToken.END_ARRAY) {
            switch (p.getCurrentToken()) {
                case START_OBJECT:
                    array.add(readMap(p));
                    break;
                case START_ARRAY:
                    array.add(readArray(p));
                    break;
                case END_ARRAY:
                case END_OBJECT:
                    break outer;
                default:
                    array.add(readObject(p));
            }

        }
        return array;
    }

    public boolean createNodeIndex(String name, Map<String, Object> config) throws Exception {
        ClientResponse response = clientResource.path("index/node").accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, buildIndexRequest(name, config));
        return response.getClientResponseStatus() == ClientResponse.Status.CREATED;
    }

    public List<Map<String, Object>> executeCypherQuery(String query, Map<String, Object> params) throws Exception {
        ClientResponse response = clientResource.path("cypher").accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, buildCypherRequest(query, params));

        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            return parseCypherResponse(response.getEntityInputStream());
        } else {
            throw parseCypherError(response);
        }
    }

    private String buildIndexRequest(String indexName, Map<String, Object> config) throws IOException {
        StringWriter sw = new StringWriter();
        try (JsonGenerator g = new JsonFactory().createJsonGenerator(sw)) {
            g.writeStartObject();// index object
            g.writeStringField("name", indexName);
            if (config != null && !config.isEmpty()) {
                g.writeFieldName("config");

                g.writeStartObject();// config object
                for (Map.Entry<String, Object> e : config.entrySet()) {
                    g.writeObjectField(e.getKey(), e.getValue());
                }
                g.writeEndObject();// config object
            }
            g.writeEndObject();
        }
        sw.flush();
        final String queryJson = sw.toString();
        return queryJson;
    }

    private String buildCypherRequest(String query, Map<String, Object> params) throws IOException {
        StringWriter sw = new StringWriter();
        try (JsonGenerator g = new JsonFactory().createJsonGenerator(sw)) {
            g.writeStartObject();// cypher object
            g.writeStringField("query", query);
            g.writeFieldName("params");

            g.writeStartObject();// params object
            for (Map.Entry<String, Object> e : params.entrySet()) {
                g.writeObjectField(e.getKey(), e.getValue());
            }
            g.writeEndObject();// params object

            g.writeEndObject();
        }
        sw.flush();
        final String queryJson = sw.toString();
        return queryJson;
    }

    private List<Map<String, Object>> parseCypherResponse(InputStream eis) throws IOException, IllegalStateException {
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        try (JsonParser p = new JsonFactory().createJsonParser(eis)) {
            p.nextToken(); // response object
            while (p.nextToken() != JsonToken.END_OBJECT) {
                if (p.getCurrentToken() == JsonToken.FIELD_NAME) {
                    switch (p.getCurrentName()) {
                        case "columns":
                            if (p.nextToken() == JsonToken.START_ARRAY) {
                                while (p.nextToken() != JsonToken.END_ARRAY) {
                                    columns.add(p.getText());
                                }
                            }
                            break;
                        case "data":
                            if (p.nextToken() == JsonToken.START_ARRAY) {
                                for (Object rowData : readArray(p)) {
                                    if (rowData instanceof List) {
                                        List cellList = (List) rowData;
                                        Map<String, Object> row = new HashMap<>();
                                        for (ListIterator<String> it = columns.listIterator(); it.hasNext();) {
                                            int index = it.nextIndex();
                                            String columnName = it.next();
                                            row.put(columnName, cellList.get(index));
                                        }
                                        rows.add(row);
                                    } else {
                                        throw new IllegalStateException(String.format("don't know, how to get cell data from: %s", rowData));
                                    }
                                }
                            }
                            break;
                        default:
                            throw new IllegalStateException(String.format("don't know, how to handle: %s", p.getCurrentName()));
                    }
                }
            }
        }
        return rows;
    }

    private Exception parseCypherError(ClientResponse response) throws IOException {
        String message = "";
        String exception = "";
        try (InputStream eis = response.getEntityInputStream(); JsonParser p = new JsonFactory().createJsonParser(eis)) {
            p.nextToken(); // response object
            while (p.nextToken() != JsonToken.END_OBJECT) {
                if (p.getCurrentToken() == JsonToken.FIELD_NAME) {
                    switch (p.getCurrentName()) {
                        case "message":
                            if (p.nextToken() == JsonToken.VALUE_STRING) {
                                message = p.getText();
                            }
                            break;
                        case "exception":
                            if (p.nextToken() == JsonToken.VALUE_STRING) {
                                exception = p.getText();
                            }
                            break;
                        default:
                    }
                }
            }
        }
        return new Exception(String.format("Cypher-Exception: %s(%s)", exception, message));
    }
}
