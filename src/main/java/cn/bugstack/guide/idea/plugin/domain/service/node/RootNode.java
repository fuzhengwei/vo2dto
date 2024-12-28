package cn.bugstack.guide.idea.plugin.domain.service.node;

import cn.bugstack.guide.idea.plugin.domain.service.AbstractGenerateStrategySupport;
import cn.bugstack.guide.idea.plugin.domain.service.StrategyHandler;
import cn.bugstack.guide.idea.plugin.domain.service.factory.DefaultGenerateStrategyFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class RootNode extends AbstractGenerateStrategySupport {

    protected SetObjConfigNode setObjConfigNode;

    public RootNode(SetObjConfigNode setObjConfigNode) {
        this.setObjConfigNode = setObjConfigNode;
    }

    @Override
    public Boolean apply(AnActionEvent anActionEvent, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {

        // 工程对象
        Project project = anActionEvent.getProject();
        DataContext dataContext = anActionEvent.getDataContext();
        PsiFile psiFile = anActionEvent.getData(LangDataKeys.PSI_FILE);

        // 基础信息
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        assert editor != null;
        Document document = editor.getDocument();

        // 工程配置
        DefaultGenerateStrategyFactory.ProjectConfigVO projectConfigVO = new DefaultGenerateStrategyFactory.ProjectConfigVO();
        projectConfigVO.setProject(project);
        projectConfigVO.setPsiFile(psiFile);
        projectConfigVO.setDataContext(dataContext);
        projectConfigVO.setEditor(editor);
        projectConfigVO.setPsiElement(psiElement);
        projectConfigVO.setOffset(editor.getCaretModel().getOffset());
        projectConfigVO.setDocument(document);
        projectConfigVO.setLineNumber(document.getLineNumber(projectConfigVO.getOffset()));
        projectConfigVO.setStartOffset(document.getLineStartOffset(projectConfigVO.getLineNumber()));
        projectConfigVO.setEditorText(document.getCharsSequence());

        // 写入上下文
        dynamicContext.setProjectConfigVO(projectConfigVO);

        return router(anActionEvent, dynamicContext);
    }

    @Override
    public StrategyHandler<AnActionEvent, DefaultGenerateStrategyFactory.DynamicContext, Boolean> get(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return setObjConfigNode;
    }

}
