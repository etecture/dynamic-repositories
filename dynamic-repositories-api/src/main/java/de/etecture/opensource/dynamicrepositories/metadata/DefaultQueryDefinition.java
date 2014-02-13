package de.etecture.opensource.dynamicrepositories.metadata;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class DefaultQueryDefinition extends AbstractQueryDefinition {

    private final String statement;

    public DefaultQueryDefinition(String statement) {
        this.statement = statement;
    }

    @Override
    public String getTechnology() {
        return "default";
    }

    @Override
    public String getConnection() {
        return "default";
    }

    @Override
    public String getStatement() {
        return statement;
    }

    @Override
    public String getConverter() {
        return "";
    }
}
