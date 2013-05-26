/*
 * This file is part of the ETECTURE Open Source Community Projects.
 *
 * Copyright (c) 2013 by:
 *
 * ETECTURE GmbH
 * Darmstädter Landstraße 112
 * 60598 Frankfurt
 * Germany
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.etecture.opensource.neo4j;

import de.etecture.opensource.neo4j.CypherResult;
import de.etecture.opensource.neo4j.Neo4jRestService;
import de.etecture.opensource.neo4j.Node;
import de.etecture.opensource.neo4j.Relationship;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import static org.fest.assertions.Assertions.assertThat;
import org.fest.assertions.MapAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * tests the {@link Neo4jRestService}.
 *
 * @author rhk
 */
@RunWith(Arquillian.class)
public class Neo4jRestServiceIT {

    @EJB
    Neo4jRestService qe;

    @Deployment(order = 2, name = "test-candidate")
    public static WebArchive createTestArchive() {
        WebArchive wa = ShrinkWrap.create(WebArchive.class, "sample.war");
        wa.addClasses(Neo4jRestService.class);
        wa.addAsWebInfResource("META-INF/beans.xml");
        wa.addAsWebInfResource("ejb-jar.xml");
        for (File libFile : new File("target/libs").listFiles()) {
            wa.addAsLibrary(libFile, libFile.getName());
        }
        System.out.println("------------------------------- sample.war --------------------------------");
        System.out.println(wa.toString(true));
        System.out.println("---------------------------------------------------------------------------");
        return wa;
    }

    private CypherResult executeCypherQuery(String query, Map<String, Object> params) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(query);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        CypherResult result = qe.executeCypherQuery(query, params);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(result);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return result;
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void simpleQueryTest() throws Exception {
        CypherResult result = executeCypherQuery("START n=node(0) RETURN count(n)", Collections.<String, Object>emptyMap());
        assertThat(result).isNotNull();
        assertThat(result.getRowCount()).isEqualTo(1);
        assertThat(result.getValue(0, "count(n)")).isNotNull().isEqualTo(1L);
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void nodeQueryTest() throws Exception {
        CypherResult result = executeCypherQuery("start n=node:node_auto_index(name=\"Keanu Reeves\") return n", Collections.<String, Object>emptyMap());
        assertThat(result).isNotNull();
        assertThat(result.getRowCount()).isEqualTo(1);
        Object cell = result.getValue(0, "n");

        assertThat(cell).isNotNull().isInstanceOf(Node.class);
        Node node = (Node) cell;

        assertThat(node.getPropertyValue("name")).isInstanceOf(String.class).isEqualTo("Keanu Reeves");
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void relationshipQueryTest() throws Exception {
        CypherResult result = executeCypherQuery("start n=node:node_auto_index(name=\"Keanu Reeves\") match n-[r:ACTS_IN]->m return r, ID(n), ID(m)", Collections.<String, Object>emptyMap());

        assertThat(result).isNotNull();
        assertThat(result.getColumnCount()).isEqualTo(3);
        assertThat(result.getRowCount()).isEqualTo(3);
        int n = ((Long) result.getValue(0, "ID(n)")).intValue();
        int m = ((Long) result.getValue(0, "ID(m)")).intValue();
        Object cell = result.getValue(0, "r");

        assertThat(cell).isNotNull().isInstanceOf(Relationship.class);
        Relationship rel = (Relationship) cell;

        assertThat(rel.getType()).isEqualTo("ACTS_IN");
        assertThat(rel.getPropertyValue("role")).isInstanceOf(String.class).isEqualTo("Neo");
        assertThat(rel.getStartId()).isEqualTo(n);
        assertThat(rel.getEndId()).isEqualTo(m);

    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void multipleColumnsQueryTest() throws Exception {
        CypherResult result = executeCypherQuery("start n=node:node_auto_index(name={actorname}) \n"
                + "match n-[r:ACTS_IN]->m \n"
                + "return n.name as `actor.name`, m.title as `movie.title`, m.year as `movie.year`, r.role as `role.name`", Collections.<String, Object>singletonMap("actorname", "Keanu Reeves"));

        assertThat(result)
                .isNotNull();
        assertThat(result.getRowCount()).isEqualTo(3);
        Map<String, Object> row = result.getRowData(1);

        assertThat(row)
                .isNotNull();
        assertThat(row.keySet()).doesNotHaveDuplicates().containsOnly("actor.name", "movie.title", "movie.year", "role.name");
        assertThat(row)
                .includes(
                MapAssert.entry("actor.name", "Keanu Reeves"),
                MapAssert.entry("movie.title", "The Matrix Reloaded"),
                MapAssert.entry("movie.year", "2003-05-07"),
                MapAssert.entry("role.name", "Neo"));

    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void aggregateQueryTest() throws Exception {
        CypherResult result = executeCypherQuery("start n=node:node_auto_index(name={actorname}) "
                + "match n-[ACTS_IN]->m \n"
                + "return n.name as `actor.name`, collect(m.title) as `movies.title`", Collections.<String, Object>singletonMap("actorname", "Keanu Reeves"));

        assertThat(result)
                .isNotNull();
        assertThat(result.getRowCount()).isEqualTo(1);
        Map<String, Object> row = result.getRowData(0);

        assertThat(row)
                .isNotNull().includes(MapAssert.entry("actor.name", "Keanu Reeves"));
        assertThat(row.containsKey("movies.title"));
        assertThat(row.get("movies.title")).isNotNull().isInstanceOf(List.class);
        List aggregateResult = (List) row.get("movies.title");

        assertThat(aggregateResult)
                .hasSize(3).doesNotHaveDuplicates().containsOnly("The Matrix", "The Matrix Reloaded", "The Matrix Revolutions");
    }
}
