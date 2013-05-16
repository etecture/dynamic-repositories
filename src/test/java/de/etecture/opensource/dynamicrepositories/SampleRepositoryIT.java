package de.etecture.opensource.dynamicrepositories;

import de.etecture.opensource.dynamicrepositories.api.PageIndex;
import de.etecture.opensource.dynamicrepositories.api.PageSize;
import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;
import de.etecture.opensource.dynamicrepositories.extension.RepositoryBean;
import de.etecture.opensource.dynamicrepositories.extension.RepositoryExtension;
import de.etecture.opensource.dynamicrepositories.extension.RepositoryInvocationHandler;
import de.etecture.opensource.dynamicrepositories.technologies.DummyQueryExecutor;
import de.etecture.opensource.dynamicrepositories.technologies.JPAQueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
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
    SampleRepository defaultRepository;
    @Inject
    @Technology("JPA")
    SampleRepository repository;
    @Inject
    @Technology("Neo4j")
    SampleRepository neo4jRepository;

    @Deployment(order = 1, name = "test-candidate")
    public static WebArchive createTestArchive() {
        WebArchive wa = ShrinkWrap.create(WebArchive.class, "sample.war")
                .addClasses(Sample.class, SampleRepository.class, Retrieve.class, QueryExecutor.class, ParamName.class, Technology.class, JPAQueryExecutor.class, DummyQueryExecutor.class, PageIndex.class, PageSize.class, RepositoryInvocationHandler.class, Repository.class, RepositoryBean.class, RepositoryExtension.class);
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
    public void defaultTechnologyTest() throws Exception {
        assertThat(defaultRepository).isNotNull().isSameAs(repository);
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void otherTechnologyTest() throws Exception {
        assertThat(neo4jRepository).isNotNull();
        assertThat(neo4jRepository.getSampleName(4711L)).isEqualTo("dummy-4711-bliblablubb");
    }

    @Test
    @OperateOnDeployment("test-candidate")
    @UsingDataSet("datasets/SampleRepositoryIT.xml")
    public void repositoryFinderTest() throws Exception {
        assertThat(repository).isNotNull();
        assertThat(repository.findById(1l)).isNotNull().isInstanceOf(Sample.class);
        assertThat(repository.findById(1).getName()).isEqualTo("bodo");
        assertThat(repository.findById(2).getName()).isEqualTo("duke");
        assertThat(repository.findAll()).hasSize(15).onProperty("name").contains("bodo", "duke");
        assertThat(repository.getSampleCount()).isEqualTo(15l);
        assertThat(repository.getSampleName(2l)).isEqualTo("duke");
        assertThat(repository.findAllPagedWithDefaultPageSize(5)).hasSize(10).onProperty("name").excludes("bodo", "duke", "robert", "martin", "nadja");
        assertThat(repository.findAllPagedWithDynamicPageSize(2, 3)).hasSize(3).onProperty("name").containsOnly("robert", "martin", "nadja");
    }

    @Test
    @OperateOnDeployment("test-candidate")
    public void repositoryCreateMethodTest() throws Exception {
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

        sample = repository.createByUsingTheConstructor(3l, "timo");
        assertThat(sample).isNotNull();
        assertThat(sample.getId()).isEqualTo(3l);
        assertThat(sample.getName()).isEqualTo("timo");
        assertThat(repository.findById(3l)).isEqualTo(sample);

        assertThat(repository.updateName("timo", "klaus")).isEqualTo(2);

        assertThat(repository.deleteByName("klaus")).isEqualTo(2);
    }

    @Test
    @OperateOnDeployment("test-candidate")
    @UsingDataSet("datasets/SampleRepositoryIT.xml")
    public void repositoryUpdateSupportTest() throws Exception {
        assertThat(repository).isNotNull();
        Sample sample = repository.findById(1l);
        sample.setName("ingo");
        sample = repository.update(sample);
        assertThat(sample.getName()).isEqualTo("ingo");
        assertThat(repository.getSampleName(1l)).isEqualTo("ingo");
    }

    @Test
    @OperateOnDeployment("test-candidate")
    @UsingDataSet("datasets/SampleRepositoryIT.xml")
    public void repositoryDeleteSupportTest() throws Exception {
        assertThat(repository).isNotNull();
        assertThat(repository.getSampleCount()).isEqualTo(15l);
        Sample sample = repository.findById(1l);
        repository.delete(sample);
        assertThat(repository.getSampleCount()).isEqualTo(14l);
    }
}
