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
import de.etecture.opensource.dynamicrepositories.api.Create;
import de.etecture.opensource.dynamicrepositories.api.Delete;
import de.etecture.opensource.dynamicrepositories.api.DeleteSupport;
import de.etecture.opensource.dynamicrepositories.api.Offset;
import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Queries;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;
import de.etecture.opensource.dynamicrepositories.api.Update;
import de.etecture.opensource.dynamicrepositories.api.UpdateSupport;
import de.etecture.opensource.dynamicrepositories.technologies.SampleResultConverter;
import java.util.List;
import javax.annotation.security.RolesAllowed;

/**
 * this is a sample repository to test the cdi extension
 *
 * @author rhk
 */
@Repository
public interface SampleRepository extends
        DeleteSupport,
        UpdateSupport {

    // --- Create-Methods -----------------------------------------------------
    @Create
    Sample create(@ParamName("id") long id);

    @Create
    Sample create(@ParamName("id") long id, @ParamName("name") String name);

    // --- Finder-Methods -----------------------------------------------------
    @Retrieve
    Sample findById(@ParamName("id") long id);

    @Retrieve
    @Query(name = "findById")
    Sample findByIdWithException(@ParamName("id") long id) throws MyException;

    @Retrieve
    List<Sample> findAll();

    @Retrieve
    @Query("select s from Sample s")
    List<Sample> findAllByQuery();

    @Retrieve
    @Count(10)
    @Query(name = "findAll")
    List<Sample> findAllPagedWithDefaultPageSize(@Offset int index);

    @Retrieve
    @Query(name = "findAll")
    List<Sample> findAllPagedWithDynamicPageSize(@Offset int index, @Count int count);

    @Retrieve
    @Queries({
        @Query("select s.name from Sample s where s.id = :id"),
        @Query(technology = "Neo4j", value = "dummy-%s-bliblablubb")
    })
    String getSampleName(@ParamName("id") long id);

    @Retrieve
    @Query(value = "select s from Sample s where s.id = :id", converter = SampleResultConverter.class)
    String getSampleString(@ParamName("id") long id);

    @Retrieve
    @Query("select count(s) from Sample s")
    Long getSampleCount();

    // --- Bulk-Update-Methods ------------------------------------------------
    @Update
    @Query("update Sample s set s.name = :newname where s.name = :oldname")
    Integer updateName(@ParamName("oldname") String oldname, @ParamName("newname") String newname);

    // --- Bulk-Delete-Methods ------------------------------------------------
    @Delete
    @Query("delete from Sample s where s.name = :name")
    @RolesAllowed("SampleDelete")
    Integer deleteByName(@ParamName("name") String name);
}
