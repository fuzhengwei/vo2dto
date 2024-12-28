package cn.bugstack.guide.idea.plugin.domain.service.node;

import cn.bugstack.guide.idea.plugin.domain.model.MethodVO;
import cn.bugstack.guide.idea.plugin.domain.service.AbstractGenerateStrategySupport;
import cn.bugstack.guide.idea.plugin.domain.service.StrategyHandler;
import cn.bugstack.guide.idea.plugin.domain.service.factory.DefaultGenerateStrategyFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaCodeReferenceElementImpl;
import com.intellij.psi.impl.source.tree.java.PsiLocalVariableImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;
import java.util.regex.Pattern;

public class SetObjConfigNode extends AbstractGenerateStrategySupport {

    private final GetObjConfigNode getObjConfigNode;

    public SetObjConfigNode(GetObjConfigNode getObjConfigNode) {
        this.getObjConfigNode = getObjConfigNode;
    }

    @Override
    public Boolean apply(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        // 获取上下文对象
        DefaultGenerateStrategyFactory.ProjectConfigVO projectConfigVO = dynamicContext.getProjectConfigVO();

        int repair = 0;
        PsiClass psiClass = null;
        String clazzParamName = null;
        String clazzFullName = null;

        PsiElement psiElement = projectConfigVO.getPsiElement();
        // 鼠标定位到类
        if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) projectConfigVO.getPsiElement();
            // 通过光标步长递进找到属性名称
            PsiFile psiFile = projectConfigVO.getPsiFile();
            Editor editor = projectConfigVO.getEditor();
            int offsetStep = projectConfigVO.getOffset() + 1;
            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());

            clazzFullName = elementAt.getText();

            // 做循环补充，一直找到空格位置
            while (!(elementAt instanceof PsiWhiteSpace)) {
                elementAt = psiFile.findElementAt(--offsetStep);
            }

            elementAt = psiFile.findElementAt(++offsetStep);

            // 一直循环到第一个空白区
            while (!(elementAt instanceof PsiWhiteSpace)) {
                if (elementAt.getText().equals(".")) {
                    elementAt = psiFile.findElementAt(++offsetStep);
                    clazzFullName += "." + elementAt.getText();
                }

                elementAt = psiFile.findElementAt(++offsetStep);
            }

            // 从空白区开始循环找内容
            while (null == elementAt || elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(++offsetStep);
            }

            // 最终拿到属性名称
            clazzParamName = elementAt.getText();
        }

        // 鼠标定位到属性
        PsiLocalVariable psiLocalVariable = null;

        if (psiElement instanceof PsiLocalVariable) {
            psiLocalVariable = (PsiLocalVariable) psiElement;
        } else if (psiElement == null) {
            //精确定位不到dto时，尝试直接从本地变量获取。适用：public FooDTO toDto(Foo foo)在简单函数内部运行插件的场景
            psiLocalVariable = PsiTreeUtil.collectElementsOfType(projectConfigVO.getPsiFile(), PsiLocalVariable.class)
                    .stream()
                    .findAny()
                    .orElse(null);
        }

        if (psiLocalVariable != null) {
            clazzParamName = psiLocalVariable.getName();

            // 通过光标步长递进找到属性名称
            PsiFile psiFile = projectConfigVO.getPsiFile();
            Editor editor = projectConfigVO.getEditor();

            // 获取全类名路径
            int startOffset = ((PsiLocalVariableImpl) psiLocalVariable).getStartOffset() + 1;
            PsiElement startElementAt = psiFile.findElementAt(startOffset);

            // 类名称获取 UserVO. UserVO2 userVO2; 获取 UserVO. UserVO2
            clazzFullName = startElementAt.getText();
            while (!startElementAt.getText().equals(clazzParamName)) {
                if (startElementAt.getText().equals(".")) {
                    startElementAt = psiFile.findElementAt(++startOffset);
                    // 排除点后面的空格
                    while (startElementAt instanceof PsiWhiteSpace) {
                        startElementAt = psiFile.findElementAt(++startOffset);
                    }
                    clazzFullName += "." + startElementAt.getText();
                }
                startElementAt = psiFile.findElementAt(++startOffset);
            }

            // 找到最终类名称
            int offsetStep = ((PsiLocalVariableImpl) psiLocalVariable).getTextOffset();
            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt
                    || elementAt.getText().equals(clazzParamName)
                    || elementAt.getText().equals(";")
                    || elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(--offsetStep);
                if (elementAt instanceof PsiWhiteSpace) {
                    ++repair;
                }
            }

            String clazzName = elementAt.getText();

            // 类名
            // String presentableText = psiLocalVariable.getType().getPresentableText();
            // 含整包名的类名
            // String canonicalText = psiLocalVariable.getType().getCanonicalText();

            PsiClass[] psiClasses = PsiShortNamesCache.getInstance(projectConfigVO.getProject())
                    .getClassesByName(clazzName, GlobalSearchScope.allScope(projectConfigVO.getProject()));

            if (psiClasses.length > 1) {
                assert elementAt.getContext() != null;
                String qualifiedName = ((PsiJavaCodeReferenceElementImpl) elementAt.getContext()).getQualifiedName();
                for (PsiClass clazz : psiClasses) {
                    if (Objects.equals(clazz.getQualifiedName(), qualifiedName)) {
                        psiClass = clazz;
                        break;
                    }
                }
            } else {
                psiClass = psiClasses[0];
            }
            if (null == psiClass) {
                psiClass = psiClasses[0];
            }
            repair += Objects.requireNonNull(psiClass.getName()).length();
        }

        // 获取类的set方法并存放起来
        Pattern setMtd = Pattern.compile(setRegex);
        List<String> paramList = new ArrayList<>();
        Map<String, String> paramMtdMap = new HashMap<>();
        Map<String, String> paramNameMap = new HashMap<>();
        List<PsiClass> psiClassLinkList = getPsiClassLinkList(psiClass);
        for (PsiClass psi : psiClassLinkList) {
            MethodVO methodVO = getMethods(psi, setRegex, "set");
            for (String methodName : methodVO.getMethodNameList()) {
                // 替换属性
                String param = setMtd.matcher(methodName).replaceAll("$1").toLowerCase();
                // 保存获取的属性信息
                paramMtdMap.put(param, methodName);
                paramList.add(param);
            }
            for (String fieldName : methodVO.getFieldNameList()) {
                paramNameMap.put(fieldName.toLowerCase(), fieldName);
            }
        }

        DefaultGenerateStrategyFactory.SetObjConfigVO setObjConfigVO = new DefaultGenerateStrategyFactory.SetObjConfigVO(
                null != clazzFullName ? clazzFullName : psiClass.getName(),
                null == psiClass ? "" : psiClass.getQualifiedName(),
                clazzParamName,
                paramList,
                paramMtdMap,
                paramNameMap,
                repair,
                isUsedLombokBuilder(psiClass));

        // 写入上下文
        dynamicContext.setSetObjConfigVO(setObjConfigVO);

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<AnActionEvent, DefaultGenerateStrategyFactory.DynamicContext, Boolean> get(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return getObjConfigNode;
    }

}
