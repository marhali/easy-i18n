package de.marhali.easyi18n.next_io.key;

import de.marhali.easyi18n.next_domain.I18nKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author marhali
 */
public class KeyTemplateTest {

    @Test
    public void test_build_returns_proper_i18n_key() {
        var template = KeyTemplate.compile("{pathNs}:{pathKey}.{fileKey:.}");

        var params = Map.of(
            "pathNs", List.of("myNs"),
            "pathKey", List.of("myPathKey"),
            "fileKey", List.of("myFileKeyA", "myFileKeyB")
        );

        Assert.assertEquals(
            I18nKey.of("myNs", "myPathKey", "myFileKeyA", "myFileKeyB"),
            template.build(params)
        );
    }
}
