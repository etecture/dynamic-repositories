package de.etecture.opensource.dynamicrepositories.extension;

import java.util.Objects;

/**
 * Combines a repository interface with a technology
 *
 * @author rhk
 */
class RepositoryKey {
    private final Class<?> repositoryInterface;
    private final String technology;

    public RepositoryKey(Class<?> repositoryInterface, String technology) {
        this.repositoryInterface = repositoryInterface;
        this.technology = technology;
    }

    public Class<?> getRepositoryInterface() {
        return repositoryInterface;
    }

    public String getTechnology() {
        return technology;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.repositoryInterface);
        hash = 89 * hash + Objects.hashCode(this.technology);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RepositoryKey other = (RepositoryKey) obj;
        if (!Objects.equals(this.repositoryInterface, other.repositoryInterface)) {
            return false;
        }
        if (!Objects.equals(this.technology, other.technology)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return repositoryInterface + "::" + technology;
    }
}
