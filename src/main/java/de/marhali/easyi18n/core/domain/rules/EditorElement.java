package de.marhali.easyi18n.core.domain.rules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Extracted editor element details.
 *
 * @author marhali
 */
public final class EditorElement {

    private final @NotNull EditorLanguage language;
    private final @NotNull LiteralKind literalKind;
    private final @NotNull TriggerKind triggerKind;
    private final boolean staticallyKnown;
    private final @NotNull String literalValue;

    private final @Nullable String callableName;
    private final @Nullable String callableFqn;
    private final @Nullable String receiverTypeFqn;
    private final @Nullable Integer argumentIndex;
    private final @Nullable String argumentName;

    private final @Nullable String declarationName;
    private final @Nullable String declarationType;
    private final @NotNull Set<String> declarationMarkers;

    private final @Nullable String propertyName;
    private final @Nullable String propertyPath;

    private final @NotNull Set<String> importSources;
    private final @Nullable String filePath;
    private final boolean inTestSources;

    private EditorElement(Builder builder) {
        this.language = Objects.requireNonNull(builder.language, "language must not be null");
        this.literalKind = Objects.requireNonNull(builder.literalKind, "literalKind must not be null");
        this.triggerKind = Objects.requireNonNull(builder.triggerKind, "triggerKind  must not be null");
        this.staticallyKnown = builder.staticallyKnown;
        this.literalValue = Objects.requireNonNull(builder.literalValue, "literalValue  must not be null");
        this.callableName = builder.callableName;
        this.callableFqn = builder.callableFqn;
        this.receiverTypeFqn = builder.receiverTypeFqn;
        this.argumentIndex = builder.argumentIndex;
        this.argumentName = builder.argumentName;
        this.declarationName = builder.declarationName;
        this.declarationType = builder.declarationType;
        this.declarationMarkers = Collections.unmodifiableSet(builder.declarationMarkers);
        this.propertyName = builder.propertyName;
        this.propertyPath = builder.propertyPath;
        this.importSources = Collections.unmodifiableSet(builder.importSources);
        this.filePath = builder.filePath;
        this.inTestSources = builder.inTestSources;
    }

    public static @NotNull Builder builder(@NotNull EditorLanguage language,
                                           @NotNull LiteralKind literalKind,
                                           @NotNull TriggerKind triggerKind,
                                           @NotNull String literalValue) {
        return new Builder(language, literalKind, triggerKind, literalValue);
    }

    public @NotNull EditorLanguage language() { return language; }
    public @NotNull LiteralKind literalKind() { return literalKind; }
    public @NotNull TriggerKind triggerKind() { return triggerKind; }
    public boolean staticallyKnown() { return staticallyKnown; }
    public @NotNull String literalValue() { return literalValue; }
    public @Nullable String callableName() { return callableName; }
    public @Nullable String callableFqn() { return callableFqn; }
    public @Nullable String receiverTypeFqn() { return receiverTypeFqn; }
    public @Nullable Integer argumentIndex() { return argumentIndex; }
    public @Nullable String argumentName() { return argumentName; }
    public @Nullable String declarationName() { return declarationName; }
    public @Nullable String declarationType() { return declarationType; }
    public @NotNull Set<String> declarationMarkers() { return declarationMarkers; }
    public @Nullable String propertyName() { return propertyName; }
    public @Nullable String propertyPath() { return propertyPath; }
    public @NotNull Set<String> importSources() { return importSources; }
    public @Nullable String filePath() { return filePath; }
    public boolean inTestSources() { return inTestSources; }

    public static final class Builder {
        private final EditorLanguage language;
        private final LiteralKind literalKind;
        private final TriggerKind triggerKind;
        private final String literalValue;

        private boolean staticallyKnown = true;
        private String callableName;
        private String callableFqn;
        private String receiverTypeFqn;
        private Integer argumentIndex;
        private String argumentName;
        private String declarationName;
        private String declarationType;
        private Set<String> declarationMarkers = Collections.emptySet();
        private String propertyName;
        private String propertyPath;
        private Set<String> importSources = Collections.emptySet();
        private String filePath;
        private boolean inTestSources;

        private Builder(EditorLanguage language,
                        LiteralKind literalKind,
                        TriggerKind triggerKind,
                        String literalValue) {
            this.language = language;
            this.literalKind = literalKind;
            this.triggerKind = triggerKind;
            this.literalValue = literalValue;
        }

        public @NotNull Builder staticallyKnown(boolean value) {
            this.staticallyKnown = value;
            return this;
        }

        public @NotNull Builder callableName(@Nullable String value) {
            this.callableName = value;
            return this;
        }

        public @NotNull Builder callableFqn(@Nullable String value) {
            this.callableFqn = value;
            return this;
        }

        public @NotNull Builder receiverTypeFqn(@Nullable String value) {
            this.receiverTypeFqn = value;
            return this;
        }

        public @NotNull Builder argumentIndex(@Nullable Integer value) {
            this.argumentIndex = value;
            return this;
        }

        public @NotNull Builder argumentName(@Nullable String value) {
            this.argumentName = value;
            return this;
        }

        public @NotNull Builder declarationName(@Nullable String value) {
            this.declarationName = value;
            return this;
        }

        public @NotNull Builder declarationType(@Nullable String value) {
            this.declarationType = value;
            return this;
        }

        public @NotNull Builder declarationMarkers(@NotNull Set<String> value) {
            this.declarationMarkers = value;
            return this;
        }

        public @NotNull Builder propertyName(@Nullable String value) {
            this.propertyName = value;
            return this;
        }

        public @NotNull Builder propertyPath(@Nullable String value) {
            this.propertyPath = value;
            return this;
        }

        public @NotNull Builder importSources(@NotNull Set<String> value) {
            this.importSources = value;
            return this;
        }

        public @NotNull Builder filePath(@Nullable String value) {
            this.filePath = value;
            return this;
        }

        public @NotNull Builder inTestSources(boolean value) {
            this.inTestSources = value;
            return this;
        }

        public @NotNull EditorElement build() {
            return new EditorElement(this);
        }
    }
}
