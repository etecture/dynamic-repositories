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
package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.extension.RepositoryExtension;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import de.herschke.neo4j.uplink.ejb.Neo4jRestService;
import java.io.File;
import java.util.List;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import static org.fest.assertions.Assertions.assertThat;
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

    @Inject
    @Technology("Neo4j")
    SampleRepository repository;

    @Deployment(order = 2, name = "test-candidate")
    public static WebArchive createTestArchive() {
        WebArchive wa = ShrinkWrap.create(WebArchive.class, "sample.war");
        wa.addClasses(Actor.class, Movie.class, SampleRepository.class, Neo4jRestService.class, RemoteNeo4jQueryExecutor.class);
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
    public void simpleTest() throws Exception {
        assertThat(repository).isNotNull();
        Actor actor = repository.findPersonByName("Keanu Reeves");
        assertThat(actor).isNotNull();
        assertThat(actor.getName()).isNotNull().isEqualTo("Keanu Reeves");
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void simpleListTest() throws Exception {
        assertThat(repository).isNotNull();
        List<Movie> movies = repository.findMoviesWherePersonIsAnActor("Keanu Reeves");
        assertThat(movies).isNotNull().hasSize(3).onProperty("title").contains("The Matrix", "The Matrix Reloaded", "The Matrix Revolutions");
        assertThat(movies).onProperty("year").contains("1999-03-31", "2003-05-07", "2003-10-27");
    }
}
