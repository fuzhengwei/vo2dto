package cn.bugstack.guide.idea.plugin.action;

import cn.bugstack.guide.idea.plugin.domain.service.factory.DefaultGenerateStrategyFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class Vo2DtoGenerateAction extends AnAction {

    private final DefaultGenerateStrategyFactory defaultGenerateStrategyFactory = new DefaultGenerateStrategyFactory();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            // 织入代码
            defaultGenerateStrategyFactory.strategyHandler().apply(event, new DefaultGenerateStrategyFactory.DynamicContext());
        } catch (Exception e) {
            Messages.showErrorDialog(event.getProject(), "请按规：先复制对象后，例如：A a，再光标放到需要织入的对象上，例如：B b - (定位到b身上) | 其他问题请联系作者：小傅哥 微信：fustack 或者提交 issue https://github.com/fuzhengwei/vo2dto/issues", "错误提示");
        }
    }

}
