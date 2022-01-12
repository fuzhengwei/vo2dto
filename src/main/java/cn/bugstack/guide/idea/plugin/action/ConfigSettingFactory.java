package cn.bugstack.guide.idea.plugin.action;

import cn.bugstack.guide.idea.plugin.infrastructure.DataSetting;
import cn.bugstack.guide.idea.plugin.ui.ConfigSettingUI;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigSettingFactory implements SearchableConfigurable {

    private ConfigSettingUI configSettingUI;

    @SuppressWarnings("FieldCanBeLocal")
    private final Project project;

    public ConfigSettingFactory(Project project) {
        this.project = project;
        configSettingUI = new ConfigSettingUI(project);
    }

    @Override
    public @NotNull String getId() {
        return "vo2dto";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Vo2Dto 是否弹窗提醒映射关系配置";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return configSettingUI.getComponent();
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        DataSetting.DataState state = DataSetting.getInstance(project).getState();
        assert state != null;
        state.setConfigRadio(configSettingUI.getRadioVal());
    }

}
