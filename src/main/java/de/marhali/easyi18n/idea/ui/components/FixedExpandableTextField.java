package de.marhali.easyi18n.idea.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.Expandable;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollBar;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.SwingUndoUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static java.util.Collections.singletonList;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Expandable text field with proper newline handling using only public IntelliJ Platform APIs.
 * Converts literal "\n" escape sequences to real line breaks in the expanded view and back
 * on collapse. Empty lines (including trailing ones) are fully preserved.
 */
public class FixedExpandableTextField extends ExtendableTextField implements Expandable {

    @Nullable private String title;
    @Nullable private JBPopup activePopup;
    @Nullable private JBTextArea expandedArea;

    public FixedExpandableTextField() {
        setExtensions(createExtensions());
    }

    protected @NotNull List<ExtendableTextComponent.Extension> createExtensions() {
        return singletonList(ExtendableTextComponent.Extension.create(
                AllIcons.General.ExpandComponent,
                AllIcons.General.ExpandComponentHover,
                null,
                this::expand
        ));
    }

    @Override
    public void expand() {
        if (isExpanded()) return;

        Font font = getFont();
        FontMetrics metrics = font != null ? getFontMetrics(font) : null;
        int height = metrics != null ? metrics.getHeight() : 16;
        Dimension size = new Dimension(height * 32, height * 16);

        String displayText = getText().replace("\\n", "\n");
        expandedArea = createTextArea(displayText, isEditable(), getBackground(), getForeground(), font);
        copyCaretPosition(this, expandedArea);

        JLabel collapseLabel = new JLabel(AllIcons.General.CollapseComponent);
        collapseLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        collapseLabel.setBorder(JBUI.Borders.empty(5, 0, 5, 5));
        collapseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                collapse();
            }
        });

        JBScrollPane pane = new JBScrollPane(expandedArea);
        pane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        pane.getVerticalScrollBar().add(JBScrollBar.LEADING, collapseLabel);
        pane.getVerticalScrollBar().setBackground(expandedArea.getBackground());

        Insets insets = getInsets();
        Insets margin = getMargin();
        if (margin != null) {
            insets.top += margin.top;
            insets.left += margin.left;
            insets.right += margin.right;
            insets.bottom += margin.bottom;
        }
        JBInsets.addTo(size, insets);
        JBInsets.addTo(size, pane.getInsets());
        pane.setPreferredSize(size);
        pane.setViewportBorder(createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));

        ComponentPopupBuilder builder = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(pane, expandedArea)
                .setRequestFocus(true)
                .setFocusable(true)
                .setLocateWithinScreenBounds(true);

        if (title != null) {
            builder.setTitle(title);
        }

        activePopup = builder.createPopup();
        activePopup.addListener(new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                if (isEditable() && expandedArea != null) {
                    // Use replace — NOT split — so empty lines are fully preserved
                    setText(expandedArea.getText().replace("\n", "\\n"));
                    copyCaretPosition(expandedArea, FixedExpandableTextField.this);
                }
                expandedArea = null;
                activePopup = null;
            }
        });

        activePopup.show(new RelativePoint(this, new Point(0, 0)));
    }

    @Override
    public void collapse() {
        if (activePopup != null && !activePopup.isDisposed()) {
            activePopup.cancel();
        }
    }

    @Override
    public boolean isExpanded() {
        return activePopup != null && !activePopup.isDisposed();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) collapse();
        super.setEnabled(enabled);
    }

    protected @NotNull JBTextArea createTextArea(@NotNull String text, boolean editable,
                                                Color background, Color foreground, Font font) {
        JBTextArea area = new JBTextArea(text);
        area.putClientProperty(Expandable.class, this);
        area.setEditable(editable);
        area.setBackground(background);
        area.setForeground(foreground);
        area.setFont(font != null ? new Font(Font.MONOSPACED, font.getStyle(), font.getSize()) : null);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        SwingUndoUtil.addUndoRedoActions(area);
        return area;
    }

    public @Nullable String getTitle() {
        return title;
    }

    public void setTitle(@NlsContexts.PopupTitle @Nullable String title) {
        this.title = title;
    }

    private static void copyCaretPosition(JTextComponent source, JTextComponent destination) {
        try {
            destination.setCaretPosition(source.getCaretPosition());
        } catch (Exception ignored) {
        }
    }
}
