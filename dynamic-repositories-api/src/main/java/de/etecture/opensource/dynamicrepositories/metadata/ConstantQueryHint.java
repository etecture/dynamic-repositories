package de.etecture.opensource.dynamicrepositories.metadata;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class ConstantQueryHint<V> implements QueryHintDefinition<V> {

    private final String name;
    private final V value;

    public ConstantQueryHint(String name, V value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public Class<V> getValueType() {
        return (Class<V>) this.value.getClass();
    }
}
