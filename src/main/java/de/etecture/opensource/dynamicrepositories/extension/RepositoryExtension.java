/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

/**
 *
 * @author rhk
 */
public class RepositoryExtension implements Extension {
    private Set<Class<?>> repositoryInterfaces = new HashSet<>();
    private Map<RepositoryKey, RepositoryBean> repositoryBeans = new HashMap<>();

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		System.out.println("beginning the scanning process");
	}

	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        if (pat.getAnnotatedType().isAnnotationPresent(Repository.class)) {
            System.out.println("... found repository interface type: " + pat.getAnnotatedType().getJavaClass().getName());
            repositoryInterfaces.add(pat.getAnnotatedType().getJavaClass());
		}
	}

    @SuppressWarnings("element-type-mismatch")
    <T> void processInjectionPoint(@Observes ProcessInjectionTarget<T> pit, BeanManager beanManager) {
		for (InjectionPoint point : pit.getInjectionTarget().getInjectionPoints()) {
            if (repositoryInterfaces.contains(point.getType())) {
                System.out.printf("... found InjectionPoint in Class: %s with name: %s for the repository with type: %s%n", point.getBean().getBeanClass().getName(), point.getMember().getName(), point.getType());
                String technology = Technology.defaultTechnology;
                if (point.getAnnotated().isAnnotationPresent(Technology.class)) {
                    technology = point.getAnnotated().getAnnotation(Technology.class).value();
                }
                RepositoryKey key = new RepositoryKey((Class<?>) point.getType(), technology);
                if (!repositoryBeans.containsKey(key)) {
                    System.out.printf("... create Bean for Repository: %s with technology: %s%n", point.getType(), technology);
                    repositoryBeans.put(key, new RepositoryBean(beanManager, key));
                }
            }
		}
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		System.out.println("finished the scanning process");
        for (RepositoryBean bean : repositoryBeans.values()) {
			abd.addBean(bean);
		}
	}
}