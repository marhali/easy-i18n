package util;

import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.util.TranslationUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TranslationUtilTest {
    @Test
    public void isIncomplete() {
        TranslationData data = new TranslationData(true);

        data.addLocale("de");
        data.addLocale("en");

        TranslationValue complete = new TranslationValue();
        complete.setLocaleValues(Map.of("de", "deValue", "en", "enValue"));
        Assert.assertFalse(TranslationUtil.isIncomplete(complete, data));

        TranslationValue missingLocale = new TranslationValue("de", "deValue");
        Assert.assertTrue(TranslationUtil.isIncomplete(missingLocale, data));

        TranslationValue emptyLocaleValue = new TranslationValue();
        emptyLocaleValue.setLocaleValues(Map.of("de", "deValue", "en", ""));
        Assert.assertTrue(TranslationUtil.isIncomplete(emptyLocaleValue, data));
    }
}
