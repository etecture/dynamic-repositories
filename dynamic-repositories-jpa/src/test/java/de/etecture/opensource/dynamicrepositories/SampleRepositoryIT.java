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
package de.etecture.opensource.dynamicrepositories;

import de.etecture.opensource.dynamicrepositories.api.Count;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import de.etecture.opensource.dynamicrepositories.api.Offset;
import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import de.etecture.opensource.dynamicrepositories.extension.RepositoryBean;
import de.etecture.opensource.dynamicrepositories.extension.RepositoryExtension;
import de.etecture.opensource.dynamicrepositories.extension.RepositoryInvocationHandler;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.technologies.DummyQueryExecutor;
import de.etecture.opensource.dynamicrepositories.technologies.jpa.OldJPAQueryExecutor;
import de.etecture.opensource.dynamicrepositories.technologies.SampleResultConverter;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import static org.fest.assertions.Assertions.assertThat;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * tests the dynamic repository implementation.
 *
 * @author rhk
 */
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.ROLLBACK)
public class SampleRepositoryIT {

    @Inject
    @Technology("JPA")
    SampleRepository repository;
    @Inject
    @Technology("Neo4j")
    SampleRepository neo4jRepository;

    @Deployment(order = 1, name = "test-candidate")
    public static WebArchive createTestArchive() {
        WebArchive wa = ShrinkWrap.create(WebArchive.class, "sample.war")
                .addClasses(Sample.class, SampleRepository.class, Retrieve.class, QueryExecutor.class, ParamName.class, Technology.class, OldJPAQueryExecutor.class, DummyQueryExecutor.class, Offset.class, Count.class, RepositoryInvocationHandler.class, Repository.class, RepositoryBean.class, RepositoryExtension.class, ResultConverter.class, SampleResultConverter.class);
        wa.addAsWebInfResource("META-INF/beans.xml");
        wa.addAsWebInfResource("ejb-jar.xml");
        wa.addAsResource("META-INF/persistence.xml");
        wa.addAsServiceProvider(Extension.class, RepositoryExtension.class);
        System.out.println("------------------------------- sample.war --------------------------------");
        System.out.println(wa.toString(true));
        System.out.println("---------------------------------------------------------------------------");
        return wa;
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void otherTechnologyTest() {
        assertThat(neo4jRepository).isNotNull();
        assertThat(neo4jRepository.getSampleName(4711L)).isEqualTo("dummy-4711-bliblablubb");
    }

    @Test
    @OperateOnDeployment("test-candidate")
    @UsingDataSet("datasets/SampleRepositoryIT.xml")
    public void repositoryFinderTest() {
        assertThat(repository).isNotNull();
        assertThat(repository.findById(1l)).isNotNull().isInstanceOf(Sample.class);
        assertThat(repository.findById(1).getName()).isEqualTo("bodo");
        assertThat(repository.findById(2).getName()).isEqualTo("duke");
        assertThat(repository.findAllByQuery()).hasSize(15).onProperty("name").contains("bodo", "duke");
        assertThat(repository.findAll()).hasSize(15).onProperty("name").contains("bodo", "duke");
        assertThat(repository.getSampleCount()).isEqualTo(15l);
        assertThat(repository.getSampleName(2l)).isEqualTo("duke");
        assertThat(repository.findAllPagedWithDefaultPageSize(5)).hasSize(10).onProperty("name").excludes("bodo", "duke", "robert", "martin", "nadja");
        assertThat(repository.findAllPagedWithDynamicPageSize(2, 3)).hasSize(3).onProperty("name").containsOnly("robert", "martin", "nadja");

        assertThat(repository.getSampleString(1l)).isEqualTo("bodo::1");

        try {
            repository.findByIdWithException(200l);
            fail("must throw MyException!");
        } catch (MyException ex) {
            System.out.println(String.format("Correct exception caught: %s (%s)", ex.getClass().getSimpleName(), ex.getMessage()));
        }
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void repositoryCreateMethodTest() {
        assertThat(repository).isNotNull();
        Sample sample = repository.create(1l);
        assertThat(sample).isNotNull();
        assertThat(sample.getId()).isEqualTo(1l);
        assertThat(sample.getName()).isNull();
        assertThat(repository.findById(1l)).isEqualTo(sample);

        sample = repository.create(2l, "timo");
        assertThat(sample).isNotNull();
        assertThat(sample.getId()).isEqualTo(2l);
        assertThat(sample.getName()).isEqualTo("timo");
        assertThat(repository.findById(2l)).isEqualTo(sample);

        assertThat(repository.updateName("timo", "klaus")).isEqualTo(1);

        assertThat(repository.deleteByName("klaus")).isEqualTo(1);
    }

    @Test
    @OperateOnDeployment("test-candidate")
    @UsingDataSet("datasets/SampleRepositoryIT.xml")
    public void repositoryUpdateSupportTest() {
        assertThat(repository).isNotNull();
        Sample sample = repository.findById(1l);
        sample.setName("ingo");
        try {
            sample = repository.update("default", sample);
            assertThat(sample.getName()).isEqualTo("ingo");
            assertThat(repository.getSampleName(1l)).isEqualTo("ingo");
        } catch (EntityNotFoundException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    @OperateOnDeployment("test-candidate")
    @UsingDataSet("datasets/SampleRepositoryIT.xml")
    public void repositoryDeleteSupportTest() {
        assertThat(repository).isNotNull();
        assertThat(repository.getSampleCount()).isEqualTo(15l);
        Sample sample = repository.findById(1l);
        try {
            repository.delete("default", sample);
            assertThat(repository.getSampleCount()).isEqualTo(14l);
        } catch (EntityNotFoundException ex) {
            fail(ex.getMessage());
        }
    }
}
