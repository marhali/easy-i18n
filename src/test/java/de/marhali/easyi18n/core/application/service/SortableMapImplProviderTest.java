package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * @author marhali
 */
public class SortableMapImplProviderTest {
    @Test
    public void test_project_constructor_uses_sort_parameter() {
        var config = ProjectConfig.fromDefaultPreset().toBuilder()
                .sorting(true)
                    .build();
        var adapter = new InMemoryProjectConfigAdapter(config);
        var factory = new SortableMapImplProvider(adapter);
        Assert.assertEquals(config.sorting(), factory.sort());
    }

    @Test
    public void test_sort_returns_tree_map() {
        var factory = new SortableMapImplProvider(true);
        Assert.assertEquals(
            TreeMap.class,
            factory.get().getClass()
        );
    }

    @Test
    public void test_non_sort_returns_hash_map() {
        var factory = new SortableMapImplProvider(false);
        Assert.assertEquals(
            LinkedHashMap.class,
            factory.get().getClass()
        );
    }
}
