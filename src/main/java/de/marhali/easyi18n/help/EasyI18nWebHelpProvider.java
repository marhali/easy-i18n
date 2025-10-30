package de.marhali.easyi18n.help;

import com.intellij.openapi.help.WebHelpProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Plugin-specific web help provider with all topics.
 * @author marhali
 */
public class EasyI18nWebHelpProvider extends WebHelpProvider {

    @Override
    public @Nullable String getHelpPageUrl(@NotNull String helpTopicId) {
        for (Topic topic : Topic.values()) {
            if (topic.helpTopicId().equals(helpTopicId)) {
                return topic.url;
            }
        }

        return null;
    }

    public enum Topic {
        DOCS("docs", "https://github.com/marhali/easy-i18n"),
        ;

        private final String id;
        private final String url;

        Topic(String id, String url) {
            this.id = id;
            this.url = url;
        }

        public String helpTopicId() {
            return "de.marhali.easyi18n." + id;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }
}
