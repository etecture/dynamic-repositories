package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.spi.Technology;
import de.etecture.opensource.dynamicrepositories.spi.ConnectionResolver;
import de.herschke.neo4j.uplink.api.Neo4jUplink;
import de.herschke.neo4j.uplink.core.Neo4jUplinkCore;
import javax.annotation.Resource;
import javax.ejb.Singleton;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Singleton
@Technology("Neo4j")
public class SampleConnectionResolver implements ConnectionResolver<Neo4jUplink> {

    @Resource(name = "neo4j-server-url")
    String neo4jURL;

    @Override
    public Neo4jUplink getConnection(String name) {
        return new Neo4jUplinkCore(neo4jURL);
    }
}
