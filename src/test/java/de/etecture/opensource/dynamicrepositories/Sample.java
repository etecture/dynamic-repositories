package de.etecture.opensource.dynamicrepositories;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * this is a sample entity
 *
 * @author rhk
 */
@NamedQueries({
    @NamedQuery(name = "findById", query = "select s from Sample s where s.id = :id"),
    @NamedQuery(name = "findAll", query = "select s from Sample s order by s.id asc")
})
@Entity
public class Sample implements Serializable {

    static final long serialVersionUID = 0;
    @Id
    private long id;
    @Basic
    private String name;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 59 * hash + Objects.hashCode(this.name);
        return hash;
    }

    public Sample() {
    }

    public Sample(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sample other = (Sample) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
