package de.marhali.easyi18n.tabs.mapper;

import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.model.bus.FilterMissingTranslationsListener;
import de.marhali.easyi18n.model.bus.SearchQueryListener;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Mapping {@link TranslationData} to {@link TableModel}.
 * @author marhali
 */
public class TableModelMapper implements TableModel, SearchQueryListener, FilterMissingTranslationsListener {

    private final @NotNull TranslationData data;
    private final @NotNull KeyPathConverter converter;

    private final @NotNull List<String> locales;
    private @NotNull List<KeyPath> fullKeys;

    private final @NotNull Consumer<TranslationUpdate> updater;

    public TableModelMapper(@NotNull TranslationData data, @NotNull KeyPathConverter converter,
                            @NotNull Consumer<TranslationUpdate> updater) {
        this.data = data;
        this.converter = converter;

        this.locales = new ArrayList<>(data.getLocales());
        this.fullKeys = new ArrayList<>(data.getFullKeys());

        this.updater = updater;
    }

    @Override
    public void onSearchQuery(@Nullable String query) {
        if(query == null) { // Reset
            this.fullKeys = new ArrayList<>(this.data.getFullKeys());
            return;
        }

        query = query.toLowerCase();
        List<KeyPath> matches = new ArrayList<>();

        for(KeyPath key : this.data.getFullKeys()) {
            if(this.converter.concat(key).toLowerCase().contains(query)) {
                matches.add(key);
            } else {
                for(String content : this.data.getTranslation(key).values()) {
                    if(content.toLowerCase().contains(query)) {
                        matches.add(key);
                    }
                }
            }
        }

        this.fullKeys = matches;
    }

    @Override
    public void onFilterMissingTranslations(boolean filter) {
        if(!filter) { // Reset
            this.fullKeys = new ArrayList<>(this.data.getFullKeys());
            return;
        }

        List<KeyPath> matches = new ArrayList<>();

        for(KeyPath key : this.data.getFullKeys()) {
            if(this.data.getTranslation(key).values().size() != this.locales.size()) {
                matches.add(key);
            }
        }

        this.fullKeys = matches;
    }

    @Override
    public int getRowCount() {
        return this.fullKeys.size();
    }

    @Override
    public int getColumnCount() {
        return this.locales.size() + 1; // Number of locales + 1 (key column)
    }

    @Nls
    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex == 0) {
            return "<html><b>Key</b></html>";
        }

        return "<html><b>" + this.locales.get(columnIndex - 1) + "</b></html>";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        KeyPath key = this.fullKeys.get(rowIndex);

        if(columnIndex == 0) { // Keys
            return this.converter.concat(key);
        }

        String locale = this.locales.get(columnIndex -  1);
        Translation translation = this.data.getTranslation(key);

        return translation == null ? null : translation.get(locale);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        KeyPath key = this.fullKeys.get(rowIndex);
        Translation translation = this.data.getTranslation(key);

        if(translation == null) { // Unknown cell
            return;
        }

        KeyPath newKey = columnIndex == 0 ? this.converter.split(String.valueOf(aValue)) : key;

        // Translation content update
        if(columnIndex > 0) {
            if(aValue == null || ((String) aValue).isEmpty()) {
                translation.remove(this.locales.get(columnIndex - 1));
            } else {
                translation.put(this.locales.get(columnIndex - 1), String.valueOf(aValue));
            }
        }

        TranslationUpdate update = new TranslationUpdate(new KeyedTranslation(key, translation),
                new KeyedTranslation(newKey, translation));

        this.updater.accept(update);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {}

    @Override
    public void removeTableModelListener(TableModelListener l) {}
}