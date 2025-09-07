package de.marhali.easyi18n;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.settings.presets.NamingConvention;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link KeyPathConverter}.
 * @author marhali
 */
public class KeyPathConverterTest {

    @Test
    public void noNamespaceDelimiter() {
        KeyPathConverter converter = getConverter(FolderStrategyType.MODULARIZED_NAMESPACE, null, ".", null, true);

        Assert.assertEquals(new KeyPath("username"), converter.fromString("username"));
        Assert.assertEquals(new KeyPath("username:nested"), converter.fromString("username:nested"));
        Assert.assertEquals(new KeyPath("username:nested", "leaf"), converter.fromString("username:nested.leaf"));
    }

    @Test
    public void emptyDefaultNamespace() {
        KeyPathConverter converter = getConverter(FolderStrategyType.MODULARIZED_NAMESPACE, ":", ".", null, true);

        Assert.assertEquals(new KeyPath("username"), converter.fromString("username"));
        Assert.assertEquals(new KeyPath("username", "nested"), converter.fromString("username:nested"));
        Assert.assertEquals(new KeyPath("username", "nested", "leaf"), converter.fromString("username:nested.leaf"));
    }

    @Test
    public void nonNestedSingle() {
        KeyPathConverter converter = getConverter(FolderStrategyType.SINGLE, null, ".", null, false);

        Assert.assertEquals("username", converter.toString(new KeyPath("username")));
        Assert.assertEquals("username\\.nested.section", converter.toString(new KeyPath("username", "nested.section")));
        Assert.assertEquals("username.normal.nested", converter.toString(new KeyPath("username.normal.nested")));

        Assert.assertEquals(new KeyPath("username"), converter.fromString("username"));
        Assert.assertEquals(new KeyPath("username", "nested.section"), converter.fromString("username\\.nested.section"));
        Assert.assertEquals(new KeyPath("username.normal.nested"), converter.fromString("username.normal.nested"));
    }

    @Test
    public void nonNestedNamespace() {
        KeyPathConverter converter = getConverter(FolderStrategyType.MODULARIZED_NAMESPACE, ":", ".", "common", false);

        Assert.assertEquals("username", converter.toString(new KeyPath("username")));
        Assert.assertEquals("username.title\\:concat.leaf\\.node", converter.toString(new KeyPath("username.title", "concat.leaf", "node")));

        Assert.assertEquals(new KeyPath("common", "username"), converter.fromString("username"));
        Assert.assertEquals(new KeyPath("username.title", "concat", "leaf.node"), converter.fromString("username.title\\:concat\\.leaf.node"));
    }

    @Test
    public void single() {
        KeyPathConverter converter = getConverter(FolderStrategyType.SINGLE,null, ".", null, true);

        Assert.assertEquals("username", converter.toString(new KeyPath("username")));
        Assert.assertEquals("username.title", converter.toString(new KeyPath("username", "title")));
        Assert.assertEquals("username.nested\\.section", converter.toString(new KeyPath("username", "nested.section")));
        Assert.assertEquals("username.deep.nested", converter.toString(new KeyPath("username", "deep", "nested")));

        Assert.assertEquals(new KeyPath("username"), converter.fromString("username"));
        Assert.assertEquals(new KeyPath("username", "title"), converter.fromString("username.title"));
        Assert.assertEquals(new KeyPath("username", "nested.section"), converter.fromString("username.nested\\.section"));
        Assert.assertEquals(new KeyPath("username", "deep", "nested"), converter.fromString("username.deep.nested"));
    }

    @Test
    public void namespace() {
        KeyPathConverter converter = getConverter(FolderStrategyType.MODULARIZED_NAMESPACE, ":", ".", "common", true);

        Assert.assertEquals("common", converter.toString(new KeyPath("common")));
        Assert.assertEquals("common:username", converter.toString(new KeyPath("common", "username")));
        Assert.assertEquals("nested\\:common:username", converter.toString(new KeyPath("nested:common", "username")));
        Assert.assertEquals("common:username.nested\\.section", converter.toString(new KeyPath("common", "username", "nested.section")));
        Assert.assertEquals("common:username.deep.nested", converter.toString(new KeyPath("common", "username", "deep", "nested")));

        Assert.assertEquals(new KeyPath("common", "key"), converter.fromString("key"));
        Assert.assertEquals(new KeyPath("common", "common:username", "title"), converter.fromString("common\\:username.title"));
        Assert.assertEquals(new KeyPath("user", "title"), converter.fromString("user:title"));
        Assert.assertEquals(new KeyPath("user:complex", "deep.nested", "value"), converter.fromString("user\\:complex:deep\\.nested.value"));
    }

    private KeyPathConverter getConverter(FolderStrategyType strategy, String namespaceDelim,
                                        String sectionDelim, String defaultNs, boolean nestKeys) {
        return new KeyPathConverter(new ProjectSettings() {
            @Override
            public @Nullable String getLocalesDirectory() {
                return null;
            }

            @Override
            public @NotNull FolderStrategyType getFolderStrategy() {
                return strategy;
            }

            @Override
            public @NotNull ParserStrategyType getParserStrategy() {
                return null;
            }

            @Override
            public @NotNull String getFilePattern() {
                return null;
            }

            @Override
            public boolean isSorting() {
                return false;
            }

            @Override
            public @Nullable String getNamespaceDelimiter() {
                return namespaceDelim;
            }

            @Override
            public @NotNull String getSectionDelimiter() {
                return sectionDelim;
            }

            @Override
            public @Nullable String getContextDelimiter() {
                return null;
            }

            @Override
            public @Nullable String getPluralDelimiter() {
                return null;
            }

            @Override
            public @Nullable String getDefaultNamespace() {
                return defaultNs;
            }

            @Override
            public @NotNull String getPreviewLocale() {
                return null;
            }

            @Override
            public boolean isNestedKeys() {
                return nestKeys;
            }

            @Override
            public boolean isAssistance() {
                return false;
            }

            @Override
            public boolean isAlwaysFold() {
                return false;
            }

            @Override
            public boolean isAddBlankLine() { return false; }

            @Override
            public String getFlavorTemplate() {
                return "";
            }

            @Override
            public boolean isIncludeSubDirs() {
                return false;
            }

            @Override
            public @NotNull NamingConvention getCaseFormat() {
                return NamingConvention.CAMEL_CASE;
            }
        });
    }
}
