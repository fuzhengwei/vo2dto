package cn.bugstack.guide.idea.plugin.action;

import cn.bugstack.guide.idea.plugin.application.IGenerateVo2Dto;
import cn.bugstack.guide.idea.plugin.domain.service.generate.factory.DefaultGenerateStrategyFactory;
import cn.bugstack.guide.idea.plugin.domain.service.impl.GenerateVo2DtoImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class Vo2DtoGenerateAction extends AnAction {

    private IGenerateVo2Dto generateVo2Dto = new GenerateVo2DtoImpl();

    private final DefaultGenerateStrategyFactory defaultGenerateStrategyFactory = new DefaultGenerateStrategyFactory();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            // 织入代码
            //generateVo2Dto.doGenerate(event.getProject(), event.getDataContext(), event.getData(LangDataKeys.PSI_FILE));
            defaultGenerateStrategyFactory.strategyHandler().apply(event, new DefaultGenerateStrategyFactory.DynamicContext());
        } catch (Exception e) {
            Messages.showErrorDialog(event.getProject(), "请按规：先复制对象后，例如：A a，再光标放到需要织入的对象上，例如：B b！联系作者：小傅哥 微信：fustack 或者提交 issue https://github.com/fuzhengwei/vo2dto/issues", "错误提示");
        }
    }

}
