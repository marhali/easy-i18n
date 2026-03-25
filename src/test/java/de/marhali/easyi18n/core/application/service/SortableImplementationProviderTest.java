package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author marhali
 */
public class SortableImplementationProviderTest {

    private SortableImplementationProvider providerWithSorting(boolean sortingEnabled) {
        ProjectConfig projectConfig = ProjectConfig.fromDefaultPreset().toBuilder()
            .sorting(sortingEnabled).build();
        ProjectConfigPort projectConfigPort = new InMemoryProjectConfigAdapter(projectConfig);
        return new SortableImplementationProvider(projectConfigPort);
    }

    @Test
    public void testSortingEnabled() {
        var provider = providerWithSorting(true);

        Assert.assertTrue(provider.getMap() instanceof TreeMap);
        Assert.assertTrue(provider.getMap(1) instanceof TreeMap);
        Assert.assertTrue(provider.getMap(Map.of("key", "val")) instanceof TreeMap);

        Assert.assertTrue(provider.getSet() instanceof TreeSet);
        Assert.assertTrue(provider.getSet(1) instanceof TreeSet);
        Assert.assertTrue(provider.getSet(Set.of("key")) instanceof TreeSet);
    }

    @Test
    public void testSortingDisabled() {
        var provider = providerWithSorting(false);

        Assert.assertTrue(provider.getMap() instanceof LinkedHashMap);
        Assert.assertTrue(provider.getMap(1) instanceof LinkedHashMap);
        Assert.assertTrue(provider.getMap(Map.of("key", "val")) instanceof LinkedHashMap);

        Assert.assertTrue(provider.getSet() instanceof LinkedHashSet);
        Assert.assertTrue(provider.getSet(1) instanceof LinkedHashSet);
        Assert.assertTrue(provider.getSet(Set.of("key")) instanceof LinkedHashSet);
    }
}
