package de.marhali.easyi18n.config.project.component;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.next_io.file.FileParser;

import java.util.*;

/**
 * @author marhali
 */
public class ProjectConfigExtMapperUi extends BaseProjectConfigUi {

    private final Map<FileParser, JBTextField> fileParserFields;

    protected ProjectConfigExtMapperUi(Project project) {
        super(project);

        this.fileParserFields = new HashMap<>();
    }

    @Override
    public void buildComponent(FormBuilder formBuilder) {
        // Title
        formBuilder.addComponent(new TitledSeparator(i18n.getString("config.project.ext-mapper.title")), 1);

        // Mapper fields
        for (FileParser fileParser : FileParser.values()) {
            JBTextField field = new JBTextField();
            field.setToolTipText(i18n.getString("config.project.ext-mapper.item.tooltip"));

            formBuilder.addLabeledComponent(fileParser.getDisplayName(), field, 1, false);
            fileParserFields.put(fileParser, field);
        }
    }

    @Override
    public boolean isModified() {
        return fileParserFields.entrySet().stream()
            .anyMatch(entry ->
                !entry.getValue().getText().equals(stringifyFileExtList(state.getFileExtMapper().get(entry.getKey())))
            );
    }

    @Override
    public void applyChangesToState() {
        var fileExtMapper = state.getFileExtMapper();
        fileExtMapper.clear(); // Erase previous state

        for (Map.Entry<FileParser, JBTextField> entry : fileParserFields.entrySet()) {
            fileExtMapper.put(entry.getKey(), parseFileExtList(entry.getValue().getText()));
        }
    }

    @Override
    public void applyStateToComponent(ProjectConfig state) {
        super.applyStateToComponent(state);

        for (Map.Entry<FileParser, JBTextField> entry : fileParserFields.entrySet()) {
            entry.getValue().setText(stringifyFileExtList(state.getFileExtMapper().get(entry.getKey())));
        }
    }

    private String stringifyFileExtList(List<String> extensions) {
        if (extensions == null) {
            return "";
        }

        return String.join(",", extensions);
    }

    private List<String> parseFileExtList(String extensions) {
        return Arrays.stream(extensions.split(","))
            .map(String::trim)
            .toList();
    }
}
