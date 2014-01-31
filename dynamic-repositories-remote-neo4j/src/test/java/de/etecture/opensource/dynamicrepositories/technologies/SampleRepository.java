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
package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;
import java.util.List;

/**
 * this is a sample repository for test purposes.
 *
 * @author rhk
 */
@Repository
public interface SampleRepository {

    @Retrieve
    @Query(value = "MATCH (n:Actor) \n"
            + "WHERE n.name = {actorname} \n"
            + "RETURN n.name as `name`")
    Actor findPersonByName(@ParamName("actorname") String name);

    @Retrieve
    @Query(value = "MATCH (n:Actor)-[r:ACTS_IN]->(initialMovie:Movie) \n"
            + "WHERE n.name = {actorname} AND initialMovie.title = {initialmovietitle}\n"
            + "RETURN n.name as `name`, initialMovie")
    Actor findPersonWithInitialMovieByName(@ParamName("actorname") String name, @ParamName("initialmovietitle") String title);

    @Retrieve
    @Query(value = "MATCH (n:Actor)-[r:ACTS_IN]->(m:Movie) \n"
            + "WHERE n.name = {actorname} \n"
            + "RETURN n.name as `name`, collect(m) as `movies`, collect(DISTINCT r.role) AS `roles`")
    Actor findPersonWithMoviesByName(@ParamName("actorname") String name);

    @Retrieve
    List<Movie> findMoviesWherePersonIsAnActor(@ParamName("actorname") String name);

    @Retrieve
    @Query(name = "anotherQuery")
    Movie findMovieWithActors(@ParamName("movietitle") String title);
}
