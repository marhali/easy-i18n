package de.marhali.easyi18n.core.domain.template;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * @author marhali
 */
public class TemplateValueFormulatorTest {
    @Test
    public void test_path_template_demo() {
        var templateDefinition = "$PROJECT_DIR$/locales/{ns}/{locale}.json";
        var params = I18nParams.builder()
            .add("ns", "user", "billing")
            .add("locale", "de", "en")
            .build();

        var template = TemplateDefinitionParser.parse(templateDefinition);
        var formulator = new TemplateValueFormulator(template);

        Set<TemplateValue> variants = formulator.buildVariants(params);

        Assert.assertEquals(
            Set.of(
                new TemplateValue("$PROJECT_DIR$/locales/user/de.json", I18nParams.builder()
                    .add("ns", "user").add("locale", "de").build()),
                new TemplateValue("$PROJECT_DIR$/locales/user/en.json", I18nParams.builder()
                    .add("ns", "user").add("locale", "en").build()),
                new TemplateValue("$PROJECT_DIR$/locales/billing/de.json", I18nParams.builder()
                    .add("ns", "billing").add("locale", "de").build()),
                new TemplateValue("$PROJECT_DIR$/locales/billing/en.json", I18nParams.builder()
                    .add("ns", "billing").add("locale", "en").build())
            ),
            variants
        );
    }

    @Test
    public void test_placeholder_with_delimiter() {
        var templateDefinition = "{fileKey:.}";
        var params = I18nParams.builder()
            .add("fileKey", "user", "billing", "label")
            .build();

        var template = TemplateDefinitionParser.parse(templateDefinition);
        var formulator = new TemplateValueFormulator(template);

        Set<TemplateValue> variants = formulator.buildVariants(params);

        Assert.assertEquals(
            Set.of(
                new TemplateValue(
                    "user.billing.label",
                    I18nParams.builder()
                        .add("fileKey", "user", "billing", "label").build()
                )
            ),
            variants
        );
    }
}
