package cn.bugstack.guide.idea.plugin.domain.service.generate.node;

import cn.bugstack.guide.idea.plugin.domain.service.generate.AbstractGenerateStrategySupport;
import cn.bugstack.guide.idea.plugin.domain.service.generate.StrategyHandler;
import cn.bugstack.guide.idea.plugin.domain.service.generate.factory.DefaultGenerateStrategyFactory;
import cn.bugstack.guide.idea.plugin.ui.ConvertSettingUI;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

public class ConvertSettingNode extends AbstractGenerateStrategySupport {

    @Override
    public Boolean apply(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        // 获取上下文
        DefaultGenerateStrategyFactory.ProjectConfigVO projectConfigVO = dynamicContext.getProjectConfigVO();
        DefaultGenerateStrategyFactory.SetObjConfigVO setObjConfigVO = dynamicContext.getSetObjConfigVO();
        DefaultGenerateStrategyFactory.GetObjConfigVO getObjConfigVO = dynamicContext.getGetObjConfigVO();

        ShowSettingsUtil.getInstance()
                .editConfigurable(requestParameter.getProject(),
                        new ConvertSettingUI(requestParameter.getProject(),
                                projectConfigVO,
                                setObjConfigVO,
                                getObjConfigVO));

        return true;
    }

    @Override
    public StrategyHandler<AnActionEvent, DefaultGenerateStrategyFactory.DynamicContext, Boolean> get(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }

}
