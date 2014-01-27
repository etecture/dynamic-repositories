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

import de.etecture.opensource.dynamicrepositories.spi.QueryExecutorResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author rhk
 */
public class RepositoryBean implements Bean<Object> {

    private final BeanManager beanManager;
    private final RepositoryKey repositoryKey;

    public RepositoryBean(BeanManager beanManager, RepositoryKey repositoryKey) {
        this.beanManager = beanManager;
        this.repositoryKey = repositoryKey;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> qualifiers = new HashSet<>();
        if (repositoryKey.getTechnology().equalsIgnoreCase("default")) {
            qualifiers.add(new AnnotationLiteral<Default>() {
            });
        }
        qualifiers.add(new TechnologyLiteral(repositoryKey.getTechnology()));
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<>();
        types.add(repositoryKey.getRepositoryInterface());
        types.add(Object.class);
        return types;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Object create(CreationalContext ctx) {
        QueryExecutorResolver resolver =
                (QueryExecutorResolver) beanManager.resolve(
                beanManager.getBeans(QueryExecutorResolver.class)).create(
                ctx);
        return Proxy.newProxyInstance(repositoryKey.getRepositoryInterface()
                .getClassLoader(), new Class[]{repositoryKey
            .getRepositoryInterface()}, new RepositoryInvocationHandler(
                repositoryKey.getTechnology(), resolver));
    }

    @Override
    public void destroy(Object instance,
            CreationalContext ctx) {
        ctx.release();
    }

    @Override
    public String getName() {
        return repositoryKey.toString();
    }

    @Override
    public Class<?> getBeanClass() {
        return repositoryKey.getRepositoryInterface();
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }
}
