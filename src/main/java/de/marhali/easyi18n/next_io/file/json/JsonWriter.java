package de.marhali.easyi18n.next_io.file.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.marhali.easyi18n.next_domain.I18nModuleStore;
import de.marhali.easyi18n.next_io.I18nFile;
import de.marhali.easyi18n.next_io.ModuleTemplate;
import de.marhali.easyi18n.next_io.TranslationConsumer;
import de.marhali.easyi18n.next_io.TranslationProducer;
import de.marhali.easyi18n.next_io.file.FileMapper;
import de.marhali.easyi18n.next_io.file.FileTemplateLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author marhali
 */
public class JsonWriter extends FileMapper {

    private final @NotNull JsonObject rootElement;

    protected JsonWriter(@NotNull I18nModuleStore store, @NotNull ModuleTemplate template, @NotNull I18nFile file) {
        super(store, template, file);

        this.rootElement = new JsonObject();
    }

    protected void write(@NotNull TranslationConsumer consumer) {
        // TODO: connect with file template, we need List<String> as key and Object as value

        var key = getKey(consumer);
        var value = getValue(consumer);

        Iterator<String> keyIterator = key.iterator();
        JsonObject targetElement = rootElement;
        String memberName = keyIterator.next();

        while (keyIterator.hasNext()) {
            if (!targetElement.has(memberName)) {
                targetElement.add(memberName, new JsonObject());
            }
            targetElement = targetElement.getAsJsonObject(memberName);
            memberName = keyIterator.next();
        }

        targetElement.add(memberName, value);
    }

    private @NotNull List<String> getKey(@NotNull TranslationConsumer consumer) {
        List<String> keys = new ArrayList<>();

        var levelIterator = template.file().getLevels().iterator();

        var level = levelIterator.next();
        return keys;
    }

    private @NotNull JsonElement getValue(@NotNull TranslationConsumer consumer) {
        // TODO: implement
        return new JsonPrimitive("myMockVal");
    }

    // TODO: we need a combined function which returns a List of FileTranslation?

    public JsonElement getRootElement() {
        return this.rootElement;
    }
}
