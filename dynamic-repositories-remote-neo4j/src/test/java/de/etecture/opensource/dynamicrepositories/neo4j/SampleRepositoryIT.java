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
package de.etecture.opensource.dynamicrepositories.neo4j;

import de.etecture.opensource.dynamicrepositories.extension.RepositoryExtension;
import de.etecture.opensource.dynamicrepositories.technologies.Neo4jRestService;
import de.etecture.opensource.dynamicrepositories.technologies.RemoteNeo4jQueryExecutor;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.inject.spi.Extension;
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
 * tests the dynamic repository implementation.
 *
 * @author rhk
 */
@RunWith(Arquillian.class)
public class SampleRepositoryIT {

    @EJB
    Neo4jRestService qe;

    @Deployment(order = 2, name = "test-candidate")
    public static WebArchive createTestArchive() {
        WebArchive wa = ShrinkWrap.create(WebArchive.class, "sample.war");
        wa.addClasses(Person.class, Address.class, SampleRepository.class, Neo4jRestService.class, RemoteNeo4jQueryExecutor.class);
        wa.addAsWebInfResource("META-INF/beans.xml");
        wa.addAsWebInfResource("ejb-jar.xml");
        wa.addAsServiceProvider(Extension.class, RepositoryExtension.class);
        for (File libFile : new File("target/libs").listFiles()) {
            wa.addAsLibrary(libFile, libFile.getName());
        }
        System.out.println("------------------------------- sample.war --------------------------------");
        System.out.println(wa.toString(true));
        System.out.println("---------------------------------------------------------------------------");
        return wa;
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void bootstrapTest() throws Exception {
        List<Map<String, Object>> result = qe.executeCypherQuery("START n=node(0) RETURN count(n)", Collections.<String, Object>emptyMap());
        assertThat(result).isNotNull().hasSize(1);
        Map<String, Object> row = result.get(0);
        assertThat(row).isNotNull().includes(MapAssert.entry("count(n)", 1));

        result = qe.executeCypherQuery("start n=node:node_auto_index(name=\"Keanu Reeves\") return n", Collections.<String, Object>emptyMap());
        assertThat(result).isNotNull().hasSize(1);
        row = result.get(0);
        assertThat(row).isNotNull();
        assertThat(row.containsKey("n")).isTrue();
        Object cell = row.get("n");
        assertThat(cell).isNotNull().isInstanceOf(Map.class);
        Map node = (Map) cell;
        assertThat(node.containsKey("data")).isTrue();
        Object nodeData = node.get("data");
        assertThat(nodeData).isNotNull().isInstanceOf(Map.class);
        assertThat((Map) nodeData).includes(MapAssert.entry("name", "Keanu Reeves"));

        result = qe.executeCypherQuery("start n=node:node_auto_index(name={actorname}) \n"
                + "match n-[r:ACTS_IN]->m \n"
                + "return n.name as `actor.name`, m.title as `movie.title`, m.year as `movie.year`, r.role as `role.name`", Collections.<String, Object>singletonMap("actorname", "Keanu Reeves"));

        assertThat(result).isNotNull().hasSize(3);
        row = result.get(1);
        assertThat(row).isNotNull();
        assertThat(row.keySet()).doesNotHaveDuplicates().containsOnly("actor.name", "movie.title", "movie.year", "role.name");
        assertThat(row).includes(
                MapAssert.entry("actor.name", "Keanu Reeves"),
                MapAssert.entry("movie.title", "The Matrix Reloaded"),
                MapAssert.entry("movie.year", "2003-05-07"),
                MapAssert.entry("role.name", "Neo"));

        result = qe.executeCypherQuery("start n=node:node_auto_index(name={actorname}) "
                + "match n-[ACTS_IN]->m \n"
                + "return n.name as `actor.name`, collect(m.title) as `movies.title`", Collections.<String, Object>singletonMap("actorname", "Keanu Reeves"));
        assertThat(result).isNotNull().hasSize(1);
        row = result.get(0);
        assertThat(row).isNotNull().includes(MapAssert.entry("actor.name", "Keanu Reeves"));
        assertThat(row.containsKey("movies.title"));
        assertThat(row.get("movies.title")).isNotNull().isInstanceOf(List.class);
        List aggregateResult = (List) row.get("movies.title");
        assertThat(aggregateResult).hasSize(3).doesNotHaveDuplicates().containsOnly("The Matrix", "The Matrix Reloaded", "The Matrix Revolutions");
    }
}
