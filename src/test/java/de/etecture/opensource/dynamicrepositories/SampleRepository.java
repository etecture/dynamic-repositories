package de.etecture.opensource.dynamicrepositories;

import de.etecture.opensource.dynamicrepositories.api.Create;
import de.etecture.opensource.dynamicrepositories.api.Delete;
import de.etecture.opensource.dynamicrepositories.api.DeleteSupport;
import de.etecture.opensource.dynamicrepositories.api.FieldName;
import de.etecture.opensource.dynamicrepositories.api.PageIndex;
import de.etecture.opensource.dynamicrepositories.api.PageSize;
import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Queries;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.QueryName;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;
import de.etecture.opensource.dynamicrepositories.api.Update;
import de.etecture.opensource.dynamicrepositories.api.UpdateSupport;
import java.util.List;
import javax.annotation.security.RolesAllowed;

/**
 * this is a sample repository to test the cdi extension
 *
 * @author rhk
 */
@Repository
public interface SampleRepository extends
        DeleteSupport<Sample>,
        UpdateSupport<Sample> {

    // --- Create-Methods -----------------------------------------------------
    @Create
    Sample create(@FieldName("id") long id);

    @Create
    Sample create(@FieldName("id") long id, @FieldName("name") String name);

    @Create(useConstructor = true)
    Sample createByUsingTheConstructor(long id, String name);

    // --- Finder-Methods -----------------------------------------------------
    @Retrieve
    Sample findById(@ParamName("id") long id);

    @Retrieve
    List<Sample> findAll();

    @Retrieve
    @PageSize(10)
    @QueryName("findAll")
    List<Sample> findAllPagedWithDefaultPageSize(@PageIndex int index);

    @Retrieve
    @QueryName("findAll")
    List<Sample> findAllPagedWithDynamicPageSize(@PageIndex int index, @PageSize int count);

    @Retrieve
    @Queries({
        @Query("select s.name from Sample s where s.id = :id"),
        @Query(technology = "Neo4j", value = "dummy-%s-bliblablubb")
    })
    String getSampleName(@ParamName("id") long id);

    @Retrieve
    @Query("select count(s) from Sample s")
    Long getSampleCount();

    // --- Bulk-Update-Methods ------------------------------------------------
    @Update
    @Query("update Sample s set s.name = :newname where s.name = :oldname")
    int updateName(@ParamName("oldname") String oldname, @ParamName("newname") String newname);

    // --- Bulk-Delete-Methods ------------------------------------------------
    @Delete
    @Query("delete from Sample s where s.name = :name")
    @RolesAllowed("SampleDelete")
    int deleteByName(@ParamName("name") String name);
}
