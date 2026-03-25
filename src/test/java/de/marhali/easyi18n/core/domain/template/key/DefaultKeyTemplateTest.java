package de.marhali.easyi18n.core.domain.template.key;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.template.RegExpTemplateValueResolver;
import de.marhali.easyi18n.core.domain.template.TemplateDefinitionParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author marhali
 */
public class DefaultKeyTemplateTest {

    @Test
    public void test_roundtrip_pathNs_fileKey() {
        var templateDefinition = "{pathNs:.:[^:]+}:{fileKey:.}";
        var template = TemplateDefinitionParser.parse(templateDefinition);
        var resolver = RegExpTemplateValueResolver.fromTemplate(template, ".+");

        var keyTemplate = new DefaultKeyTemplate(template, resolver);

        var canonical = "core.account:my.nested.file.key";
        var key = I18nKey.of(canonical);
        var params = keyTemplate.toParams(key);

        Assert.assertEquals(
            I18nParams.builder()
                .add("pathNs", "core", "account")
                .add("fileKey", "my", "nested", "file", "key")
                .build(),
            params
        );

        Assert.assertEquals(key, keyTemplate.fromParams(params));

        Assert.assertEquals(List.of("core", "account", ":", "my", "nested", "file", "key"), keyTemplate.toHierarchy(key));
    }

    @Test
    public void test_roundtrip_escaping() {
        var templateDefinition = "{fileKey:.}";
        var template = TemplateDefinitionParser.parse(templateDefinition);
        var resolver = RegExpTemplateValueResolver.fromTemplate(template, ".+");
        var keyTemplate = new DefaultKeyTemplate(template, resolver);

        var canonical = "my.nested\\.file.key";
        var key = I18nKey.of(canonical);

        var params = keyTemplate.toParams(key);

        Assert.assertEquals(
            I18nParams.builder()
                .add("fileKey", "my", "nested.file", "key")
                .build(),
            params
        );

        Assert.assertEquals(key, keyTemplate.fromParams(params));

        Assert.assertEquals(List.of("my", "nested.file", "key"), keyTemplate.toHierarchy(key));
    }
}
