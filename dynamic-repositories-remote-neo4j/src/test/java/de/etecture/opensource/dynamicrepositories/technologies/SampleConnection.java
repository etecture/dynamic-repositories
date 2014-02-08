package de.etecture.opensource.dynamicrepositories.technologies;

import de.herschke.neo4j.uplink.core.Neo4jUplinkCore;
import javax.enterprise.inject.Default;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Default
public class SampleConnection extends Neo4jUplinkCore {

    public SampleConnection() {
        super("http://localhost:17474/db/data");
    }
}
