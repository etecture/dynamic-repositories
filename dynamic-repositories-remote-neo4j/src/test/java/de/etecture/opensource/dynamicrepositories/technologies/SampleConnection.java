package de.etecture.opensource.dynamicrepositories.technologies;

import de.herschke.neo4j.uplink.core.AbstractNeo4jUplink;
import de.herschke.neo4j.uplink.spi.CypherResultObjectMapper;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Default
public class SampleConnection extends AbstractNeo4jUplink {

    @Inject
    Instance<CypherResultObjectMapper> mappers;

    public SampleConnection() {
        super("http://localhost:17474/db/data");
    }

    @Override
    protected <T> CypherResultObjectMapper findMapper(
            Class<T> type) {
        for (CypherResultObjectMapper mapper : mappers) {
            if (mapper.isResponsibleFor(type)) {
                return mapper;
            }
        }
        return null;
    }
}
