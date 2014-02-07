package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.executor.DefaultQueryBuilder;
import de.etecture.opensource.dynamicrepositories.executor.Query;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.util.Arrays;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class Test {

    @org.junit.Test
    public void test() throws Exception {
        for (MethodDescriptor md : Introspector.getBeanInfo(
                SampleRepository.class).getMethodDescriptors()) {
            System.out.println(md.getDisplayName());
            Object[] args =
                    new Object[md.getMethod().getParameterTypes().length];
                Arrays.fill(args, 0, args.length, null);
            Query<?> query = new DefaultQueryBuilder()
                    .buildQuery(md.getMethod(), args);
            System.out.printf("\ttechnology: %s%n", query.getTechnology());
            System.out.printf("\tconnection: %s%n", query.getConnection());
            System.out.printf("\tstatement: %s%n", query.getStatement());
            System.out
                    .printf("\tresultType: %s%n", query.getGenericResultType());
            System.out.println("\tparameter:");
            for (String paramName : query.getParameterNames()) {
                System.out.printf("\t\t%s = %s%n", paramName, query
                        .getParameterValue(paramName));
            }
            System.out.println("\thints:");
            for (String hintName : query.getQueryHints()) {
                System.out.printf("\t\t%s = %s%n", hintName, query
                        .getQueryHintValue(hintName));
            }
        }
    }
}
