package com.bendb.dropwizard.jooq;

import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Support;
import org.jooq.impl.DSL;

/**
 * Provides DSL support for postgres-specific features.
 */
public final class PostgresSupport {
    private PostgresSupport() {
        // no instances
    }

    /**
     * Applies the {@code array_agg} aggregate function on a field,
     * resulting in the input values being concatenated into an array.
     *
     * @param field the field to be aggregated
     * @param <T> the type of the field
     * @return a {@link Field} representing the array aggregate.
     *
     * @see <a href="http://www.postgresql.org/docs/9.3/static/functions-aggregate.html"/>
     */
    @Support({SQLDialect.POSTGRES})
    public static <T> Field<T[]> arrayAgg(Field<T> field) {
        return DSL.field("array_agg({0})", field.getDataType().getArrayDataType(), field);
    }

    /**
     * Like {@link #arrayAgg}, but uses {@code array_remove} to eliminate
     * SQL {@code NULL} values from the result.
     *
     * @param field the field to be aggregated
     * @param <T> the type of the field
     * @return a {@link Field} representing the array aggregate
     *
     * @see <a href="http://www.postgresql.org/docs/9.3/static/functions-aggregate.html"/>
     */
    @Support({SQLDialect.POSTGRES})
    public static <T> Field<T[]> arrayAggNoNulls(Field<T> field) {
        return DSL.field("array_remove(array_agg({0}), NULL)", field.getDataType().getArrayType(), field);
    }

    /**
     * Joins a set of string values using the given delimiter.
     *
     * @param field the field to be concatenated
     * @param delimiter the separating delimiter
     * @return a {@link Field} representing the joined string
     */
    @Support({SQLDialect.POSTGRES})
    public static Field<String> stringAgg(Field<String> field, String delimiter) {
        return DSL.field("string_agg({0}, {1})", field.getDataType(), field, DSL.val(delimiter));
    }
}
