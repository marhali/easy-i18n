package de.marhali.easyi18n.next_domain;

import de.marhali.easyi18n.config.project.ProjectConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * @author marhali
 */
public class MapImplFactoryTest {
    @Test
    public void test_project_constructor_uses_sort_parameter() {
        var config = ProjectConfig.fromDefaultPreset();
        config.setSorting(true);
        var factory = new MapImplFactory(config);
        Assert.assertEquals(config.isSorting(), factory.sort());
    }

    @Test
    public void test_sort_returns_tree_map() {
        var factory = new MapImplFactory(true);
        Assert.assertEquals(
            TreeMap.class,
            factory.get().getClass()
        );
    }

    @Test
    public void test_non_sort_returns_hash_map() {
        var factory = new MapImplFactory(false);
        Assert.assertEquals(
            LinkedHashMap.class,
            factory.get().getClass()
        );
    }
}
