/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.spi.Technology;
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
        if (repositoryKey.getTechnology().equalsIgnoreCase(Technology.defaultTechnology)) {
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
        System.out.printf("create proxy for repository: %s with technology: %s%n", repositoryKey.getRepositoryInterface().getName(), repositoryKey.getTechnology());
        return Proxy.newProxyInstance(repositoryKey.getRepositoryInterface().getClassLoader(), new Class[]{repositoryKey.getRepositoryInterface()}, new RepositoryInvocationHandler(repositoryKey.getTechnology(), beanManager, ctx));
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
