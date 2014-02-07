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
package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.api.annotations.Repository;
import de.etecture.opensource.dynamicrepositories.executor.Technology;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
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
    private final Logger log = Logger.getLogger("RepositoryExtension");

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        log.fine("beginning the scanning process");
	}

	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        if (pat.getAnnotatedType().isAnnotationPresent(Repository.class)) {
            log.fine(String.format("... found repository interface type: %s%n", pat.getAnnotatedType().getJavaClass().getName()));
            repositoryInterfaces.add(pat.getAnnotatedType().getJavaClass());
		}
	}

    @SuppressWarnings("element-type-mismatch")
    <T> void processInjectionPoint(@Observes ProcessInjectionTarget<T> pit, BeanManager beanManager) {
		for (InjectionPoint point : pit.getInjectionTarget().getInjectionPoints()) {
            if (repositoryInterfaces.contains(point.getType())) {
                log.fine(String.format("... found InjectionPoint in Class: %s with name: %s for the repository with type: %s%n", point.getBean().getBeanClass().getName(), point.getMember().getName(), point.getType()));
                String technology = "default";
                if (point.getAnnotated().isAnnotationPresent(Technology.class)) {
                    technology = point.getAnnotated().getAnnotation(Technology.class).value();
                }
                RepositoryKey key = new RepositoryKey((Class<?>) point.getType(), technology);
                if (!repositoryBeans.containsKey(key)) {
                    log.fine(String.format("... create Bean for Repository: %s with technology: %s%n", point.getType(), technology));
                    repositoryBeans.put(key, new RepositoryBean(beanManager, key));
                }
            }
		}
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
        log.fine("finished the scanning process");
        for (RepositoryBean bean : repositoryBeans.values()) {
			abd.addBean(bean);
		}
	}
}