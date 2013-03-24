// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package de.gzockoll.camel.solarmon.helper;

import de.gzockoll.camel.solarmon.entity.Dataset;

public class DatasetBuilder extends DatasetBuilderBase<DatasetBuilder> {
    public static DatasetBuilder dataset() {
        return new DatasetBuilder();
    }

    public DatasetBuilder() {
        super(new Dataset());
    }

    public Dataset build() {
        return getInstance();
    }
}

class DatasetBuilderBase<GeneratorT extends DatasetBuilderBase<GeneratorT>> {
    private Dataset instance;

    protected DatasetBuilderBase(Dataset aInstance) {
        instance = aInstance;
    }

    protected Dataset getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withTagesEnergie(double aValue) {
        instance.setTagesEnergie(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withMomentanLeistung(double aValue) {
        instance.setMomentanLeistung(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withJahresEnergie(double aValue) {
        instance.setJahresEnergie(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withId(Long aValue) {
        instance.setId(aValue);

        return (GeneratorT) this;
    }
}