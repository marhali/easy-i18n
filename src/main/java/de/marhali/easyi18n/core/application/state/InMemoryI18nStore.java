package de.marhali.easyi18n.core.application.state;

import de.marhali.easyi18n.core.domain.model.I18nProject;
import de.marhali.easyi18n.core.domain.model.MutableI18nProject;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * In memory implementation for an i18n store.
 * The underlying working-set is protected by an {@link ReentrantLock} to ensure consistency.
 *
 * @author marhali
 */
public class InMemoryI18nStore implements I18nStore {

    private final @NotNull ReentrantLock lock;

    private final @NotNull MutableI18nProject workingSet;
    private @NotNull I18nProject snapshot;

    public InMemoryI18nStore() {
        this.lock = new ReentrantLock();

        this.workingSet = MutableI18nProject.empty();
        this.snapshot = I18nProject.empty();
    }

    @Override
    public @NotNull I18nProject getSnapshot() {
        return snapshot;
    }

    @Override
    public void mutate(@NotNull Consumer<@NotNull MutableI18nProject> mutateFn) {
        lock.lock();
        try {
            mutateFn.accept(workingSet);
            // Rebuild snapshot
            snapshot = workingSet.toSnapshot();
        } finally {
            lock.unlock();
        }
    }
}
