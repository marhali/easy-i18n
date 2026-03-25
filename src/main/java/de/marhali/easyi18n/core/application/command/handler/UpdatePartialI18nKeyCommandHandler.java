package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.UpdatePartialI18nKeyCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.service.EnsurePersistService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command handler for {@link UpdatePartialI18nKeyCommand}.
 *
 * @author marhalirabs
 *
 */
public class UpdatePartialI18nKeyCommandHandler implements CommandHandler<UpdatePartialI18nKeyCommand> {

    private final @NotNull String REPLACEABLE_GROUP = "replaceable";

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull EnsurePersistService ensurePersistService;
    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public UpdatePartialI18nKeyCommandHandler(@NotNull EnsureLoadedService ensureLoadedService, @NotNull EnsurePersistService ensurePersistService, @NotNull I18nStore store, @NotNull DomainEventPublisherPort domainEventPublisherPort) {
        this.ensureLoadedService = ensureLoadedService;
        this.ensurePersistService = ensurePersistService;
        this.store = store;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull UpdatePartialI18nKeyCommand command) {
        var keyCandidatePattern = compileI18nKeyCandidatePattern(command);

        ensureLoadedService.ensureLoaded(command.moduleId());

        store.mutate((project) -> {
            var module = project.getOrCreateModule(command.moduleId());

            Map<I18nKey, I18nKey> newKeyMap = new HashMap<>();

            // Collect candidates for key replacement
            for (I18nKey translationKey : module.getTranslationKeys()) {
                var newTranslationKey = isCandidateAndGetNewTranslationKey(command, keyCandidatePattern, translationKey);

                if (newTranslationKey != null) {
                    // Is candidate for replacement
                    newKeyMap.put(translationKey, newTranslationKey);
                }
            }

            // Iterate over new key mappings and apply them
            for (Map.Entry<I18nKey, I18nKey> newKeyEntry : newKeyMap.entrySet()) {
                var content = Objects.requireNonNull(module.getTranslation(newKeyEntry.getKey()),
                    "Unexpected empty translation content for key: " + newKeyEntry.getKey());

                module.removeTranslation(newKeyEntry.getKey());
                module.setTranslation(newKeyEntry.getValue(), content);
            }
        });

        ensurePersistService.ensurePersist(command.moduleId());
        domainEventPublisherPort.publish(new ModuleChanged(command.moduleId(), null));
    }

    /**
     * Builds a regular expression to match and find the index of the replaceable part.
     * <pre>
     * {@code
     * // RegExp structure
     * ^%parentKeyPartElement%.*%parentKeyPartElementN%.*(?<replaceable>%previousKeyPart%).*$
     * }
     * </pre>
     * @param command Update command
     * @return {@link Pattern}
     */
    private @NotNull Pattern compileI18nKeyCandidatePattern(@NotNull UpdatePartialI18nKeyCommand command) {
        StringBuilder patternBuilder = new StringBuilder();

        patternBuilder.append("^");

        for (String parentKeyPart : command.parentKeyParts()) {
            patternBuilder.append(Pattern.quote(parentKeyPart)).append(".*");
        }

        patternBuilder
            .append("(?<")
            .append(REPLACEABLE_GROUP)
            .append(">")
            .append(Pattern.quote(command.previousKeyPart()))
            .append(").*$");

        return Pattern.compile(patternBuilder.toString());
    }

    /**
     * Checks if the given {@link I18nKey} is a candidate for partial key replacement
     * @param command Update command
     * @param keyCandidatePattern Pattern to match candidates
     * @param key Translation key to check against
     * @return New {@link I18nKey} if canidates matches, otherwise {@code null}
     */
    private @Nullable I18nKey isCandidateAndGetNewTranslationKey(
        @NotNull UpdatePartialI18nKeyCommand command,
        @NotNull Pattern keyCandidatePattern,
        @NotNull I18nKey key
    ) {
        String canonical = key.canonical();
        Matcher matcher = keyCandidatePattern.matcher(canonical);

        // Translation key does not match - not a candidate
        if (!matcher.matches()) {
            return null;
        }

        int startIndex = matcher.start(REPLACEABLE_GROUP);
        int endIndex = matcher.end(REPLACEABLE_GROUP);

        String newCanonical = canonical.substring(0, startIndex) + command.newKeyPart() + canonical.substring(endIndex);
        return I18nKey.of(newCanonical);
    }
}
