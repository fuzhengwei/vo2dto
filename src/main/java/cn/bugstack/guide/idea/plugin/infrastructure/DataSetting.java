package cn.bugstack.guide.idea.plugin.infrastructure;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description: 数据配置
 * @author: 小傅哥，微信：fustack
 * @date: 2022/1/9
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@State(name = "DataSetting", storages = @Storage("plugin.xml"))
public class DataSetting implements PersistentStateComponent<DataSetting.DataState> {

    private DataState state = new DataState();

    public static DataSetting getInstance(Project project) {
        return project.getService(DataSetting.class);
    }

    @Nullable
    @Override
    public DataState getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull DataState state) {
        this.state = state;
    }

    public static class DataState {

        private String selectRadio = "selectExistRadioButton";

        private String configRadio = "show";

        public String getSelectRadio() {
            return selectRadio;
        }

        public void setSelectRadio(String selectRadio) {
            this.selectRadio = selectRadio;
        }

        public String getConfigRadio() {
            return configRadio;
        }

        public void setConfigRadio(String configRadio) {
            this.configRadio = configRadio;
        }
    }

}
