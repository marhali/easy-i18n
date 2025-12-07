package de.marhali.easyi18n.next_io.key;

import de.marhali.easyi18n.next_domain.I18nKey;
import de.marhali.easyi18n.next_domain.I18nParams;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author marhali
 */
public class KeyTemplateTest {

    @Test
    public void test_build_returns_proper_i18n_key() {
        var template = KeyTemplate.compile("{pathNs}:{pathKey}.{fileKey:.}");

        var params = new I18nParams();
        params.add("pathNs", "myNs");
        params.add("pathKey", "myPathKey");
        params.add("fileKey", "myFileKeyA", "myFileKeyB");

        Assert.assertEquals(
            I18nKey.of("myNs", "myPathKey", "myFileKeyA", "myFileKeyB"),
            template.build(params)
        );
    }
}
