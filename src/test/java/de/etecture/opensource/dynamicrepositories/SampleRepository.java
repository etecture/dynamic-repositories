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
