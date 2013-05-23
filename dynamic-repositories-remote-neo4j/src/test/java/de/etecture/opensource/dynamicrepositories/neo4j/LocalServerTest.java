/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.neo4j;

import java.io.IOException;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.ServerBuilder;
/**
 *
 * @author rhk
 */
public class LocalServerTest {

    private static CommunityNeoServer server;

    @BeforeClass
    public static void init() throws IOException {
        server = ServerBuilder.server().build();
        server.start();
        //populateDatabase(server.getDatabase().getGraph());
    }

    @AfterClass
    public static void teardown() {
        server.stop();
    }

    @Test
    public void test() throws InterruptedException {
        RestAPI neo4jServer = new RestAPIFacade("http://localhost:7474/db/data");
        assertThat(neo4jServer.getNodeById(0)).isNotNull();
        assertThat(neo4jServer.getNodeById(0).getId()).isEqualTo(0l);
    }
}
