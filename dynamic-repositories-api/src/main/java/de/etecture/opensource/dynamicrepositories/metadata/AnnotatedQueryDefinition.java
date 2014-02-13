package de.etecture.opensource.dynamicrepositories.metadata;

import de.etecture.opensource.dynamicrepositories.api.annotations.Query;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class AnnotatedQueryDefinition extends AbstractQueryDefinition {

    private final Query annotation;

    public AnnotatedQueryDefinition(Query annotation) {
        this.annotation = annotation;
    }

    @Override
    public String getConnection() {
        return annotation.connection();
    }

    @Override
    public String getStatement() {
        return annotation.statement();
    }

    @Override
    public String getConverter() {
        return annotation.converter();
    }

    @Override
    public String getTechnology() {
        return annotation.technology();
    }
}
