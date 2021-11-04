package de.marhali.easyi18n.model.table;

import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.model.LegacyKeyedTranslation;
import de.marhali.easyi18n.model.LegacyTranslationUpdate;
import de.marhali.easyi18n.model.Translations;

import org.jetbrains.annotations.Nls;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Table model to represents localized messages.
 * @author marhali
 */
public class TableModelTranslator implements TableModel {

    private final Translations translations;
    private final List<String> locales;
    private final List<String> fullKeys;

    private final Consumer<LegacyTranslationUpdate> updater;

    /**
     * @param translations Translations instance
     * @param searchQuery Search / filter param
     * @param updater Consumer which can be called on cell change / update
     */
    public TableModelTranslator(Translations translations, String searchQuery, Consumer<LegacyTranslationUpdate> updater) {
        this.translations = translations;
        this.locales = translations.getLocales();
        this.updater = updater;

        List<String> fullKeys = translations.getFullKeys();

        if(searchQuery != null && !searchQuery.isEmpty()) { // Filter keys by searchQuery
            fullKeys.removeIf(key -> !key.startsWith(searchQuery));
        }

        this.fullKeys = fullKeys;
    }

    @Override
    public int getRowCount() {
        return fullKeys.size();
    }

    @Override
    public int getColumnCount() {
        return locales.size() + 1; // Number of locales plus 1 for the Key's column
    }

    @Nls
    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex == 0) {
            return "<html><b>Key</b></html>";
        }

        return "<html><b>" + locales.get(columnIndex - 1) + "</b></html>";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return rowIndex > 0; // Everything should be editable except the headline
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) { // Keys
            return fullKeys.get(rowIndex);
        }

        String key = fullKeys.get(rowIndex);
        String locale = locales.get(columnIndex - 1);
        LocalizedNode node = translations.getNode(key);

        return node == null ? null : node.getValue().get(locale);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String key = String.valueOf(getValueAt(rowIndex, 0));
        LocalizedNode node = translations.getNode(key);

        if(node == null) { // Unknown cell
            return;
        }

        String newKey = columnIndex == 0 ? String.valueOf(aValue) : key;
        Map<String, String> messages = node.getValue();

        // Locale message update
        if(columnIndex > 0) {
            if(aValue == null || ((String) aValue).isEmpty()) {
                messages.remove(locales.get(columnIndex - 1));
            } else {
                messages.put(locales.get(columnIndex - 1), String.valueOf(aValue));
            }
        }

        LegacyTranslationUpdate update = new LegacyTranslationUpdate(new LegacyKeyedTranslation(key, messages),
                new LegacyKeyedTranslation(newKey, messages));

        updater.accept(update);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {}

    @Override
    public void removeTableModelListener(TableModelListener l) {}
}