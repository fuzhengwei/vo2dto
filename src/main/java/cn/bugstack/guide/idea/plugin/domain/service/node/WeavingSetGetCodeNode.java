package cn.bugstack.guide.idea.plugin.domain.service.node;

import cn.bugstack.guide.idea.plugin.domain.service.AbstractGenerateStrategySupport;
import cn.bugstack.guide.idea.plugin.domain.service.StrategyHandler;
import cn.bugstack.guide.idea.plugin.domain.service.factory.DefaultGenerateStrategyFactory;
import cn.bugstack.guide.idea.plugin.types.Utils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ScrollType;

import java.util.List;

public class WeavingSetGetCodeNode extends AbstractGenerateStrategySupport {

    @Override
    public Boolean apply(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        
        // 获取上下文
        DefaultGenerateStrategyFactory.ProjectConfigVO projectConfigVO = dynamicContext.getProjectConfigVO();
        DefaultGenerateStrategyFactory.SetObjConfigVO setObjConfigVO = dynamicContext.getSetObjConfigVO();
        DefaultGenerateStrategyFactory.GetObjConfigVO getObjConfigVO = dynamicContext.getGetObjConfigVO();

        Application application = ApplicationManager.getApplication();
        // 获取空格位置长度
        int distance = Utils.getWordStartOffset(projectConfigVO.getEditorText(), projectConfigVO.getOffset())
                - projectConfigVO.getStartOffset()
                - setObjConfigVO.getRepair();
        application.runWriteAction(() -> {
            StringBuilder blankSpace = new StringBuilder();
            for (int i = 0; i < distance; i++) {
                blankSpace.append(" ");
            }
            int lineNumberCurrent = projectConfigVO.getDocument().getLineNumber(projectConfigVO.getOffset()) + 1;

            /*
             * 判断是使用了 Lombok Builder 模式
             * UserDTO userDTO = UserDTO.builder()
             *              .userId(userVO.getUserId())
             *              .userName(userVO.getUserName())
             *              .userAge(userVO.getUserAge())
             *              .build();
             */
            if (setObjConfigVO.isLombokBuilder()) {
                int finalLineNumberCurrent = lineNumberCurrent;
                WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                    String clazzName = setObjConfigVO.getClazzName();
                    String clazzParam = setObjConfigVO.getClazzParamName();
                    int lineEndOffset = projectConfigVO.getDocument().getLineEndOffset(finalLineNumberCurrent - 1);
                    int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(finalLineNumberCurrent - 1);
                    projectConfigVO.getDocument().deleteString(lineStartOffset, lineEndOffset);
                    projectConfigVO.getDocument()
                            .insertString(projectConfigVO.getDocument().getLineStartOffset(finalLineNumberCurrent - 1),
                                    blankSpace
                                            + clazzName
                                            + " "
                                            + clazzParam
                                            + " = "
                                            + setObjConfigVO.getClazzName()
                                            + ".builder()");
                });
                List<String> setMtdList = setObjConfigVO.getParamList();
                for (String param : setMtdList) {
                    int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent++);
                    WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                        String builderMethod = blankSpace
                                + blankSpace.toString()
                                + "."
                                + setObjConfigVO.getParamNameMap().get(param)
                                + "("
                                + (null == getObjConfigVO.getParamMtdMap().get(param) ?
                                "" :
                                getObjConfigVO.getClazzParam()
                                        + "."
                                        + getObjConfigVO.getParamMtdMap().get(param)
                                        + "()")
                                + ")";
                        projectConfigVO.getDocument().insertString(lineStartOffset, builderMethod + "\n");
                        projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                        projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    });
                }
                int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent);
                WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                    projectConfigVO.getDocument()
                            .insertString(lineStartOffset, blankSpace + blankSpace.toString() + ".build();\n");
                    projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                    projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                });
            } else {
                List<String> setMtdList = setObjConfigVO.getParamList();
                for (String param : setMtdList) {
                    int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent++);
                    WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                        projectConfigVO.getDocument()
                                .insertString(lineStartOffset,
                                        blankSpace
                                                + setObjConfigVO.getClazzParamName()
                                                + "."
                                                + setObjConfigVO.getParamMtdMap().get(param)
                                                + "("
                                                + (null == getObjConfigVO.getParamMtdMap().get(param) ?
                                                "" :
                                                getObjConfigVO.getClazzParam() + "." + getObjConfigVO.getParamMtdMap()
                                                        .get(param) + "()")
                                                + ");\n");
                        projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                        projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    });
                }
            }
        });

        return true;
    }

    @Override
    public StrategyHandler<AnActionEvent, DefaultGenerateStrategyFactory.DynamicContext, Boolean> get(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
    
}
