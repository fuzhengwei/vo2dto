package cn.bugstack.guide.idea.plugin.domain.service.impl;

import cn.bugstack.guide.idea.plugin.domain.model.GenerateContext;
import cn.bugstack.guide.idea.plugin.domain.model.GetObjConfigDO;
import cn.bugstack.guide.idea.plugin.domain.model.MethodVO;
import cn.bugstack.guide.idea.plugin.domain.model.SetObjConfigDO;
import cn.bugstack.guide.idea.plugin.domain.service.AbstractGenerateVo2Dto;
import cn.bugstack.guide.idea.plugin.infrastructure.Utils;
import cn.bugstack.guide.idea.plugin.ui.ConvertSettingUI;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaCodeReferenceElementImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.impl.source.tree.java.PsiLocalVariableImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;
import java.util.regex.Pattern;

public class GenerateVo2DtoImpl extends AbstractGenerateVo2Dto {
    @Override
    protected GenerateContext getGenerateContext(Project project, DataContext dataContext, PsiFile psiFile) {
        // 基础信息
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        assert editor != null;
        Document document = editor.getDocument();
        ((PsiJavaFileImpl) psiFile).getImportList();
        // 封装生成对象上下文
        GenerateContext generateContext = new GenerateContext();
        generateContext.setProject(project);
        generateContext.setPsiFile(psiFile);
        generateContext.setDataContext(dataContext);
        generateContext.setEditor(editor);
        generateContext.setPsiElement(psiElement);
        generateContext.setOffset(editor.getCaretModel().getOffset());
        generateContext.setDocument(document);
        generateContext.setLineNumber(document.getLineNumber(generateContext.getOffset()));
        generateContext.setStartOffset(document.getLineStartOffset(generateContext.getLineNumber()));
        generateContext.setEditorText(document.getCharsSequence());
        return generateContext;
    }

    @Override
    protected SetObjConfigDO getSetObjConfigDO(GenerateContext generateContext) {
        int repair = 0;
        PsiClass psiClass = null;
        String clazzParamName = null;
        PsiElement psiElement = generateContext.getPsiElement();

        // 鼠标定位到「类上」
        if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) generateContext.getPsiElement();
            // 通过光标步长递进找到属性名称
            PsiFile psiFile = generateContext.getPsiFile();
            Editor editor = generateContext.getEditor();
            int offsetStep = generateContext.getOffset() + 1;
            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt
                    || elementAt.getText().equals(psiClass.getName())
                    || elementAt instanceof PsiWhiteSpace
                    || elementAt.getText().equals(".")) {
                elementAt = psiFile.findElementAt(++offsetStep);
            }

            // 最终拿到属性名称
            clazzParamName = elementAt.getText();
        }

        // 鼠标定位到「属性」
        PsiLocalVariable psiLocalVariable = null;
        if (psiElement instanceof PsiLocalVariable) {
            psiLocalVariable = (PsiLocalVariable) psiElement;
        } else if (psiElement == null) {
            //精确定位不到dto时，尝试直接从本地变量获取。适用：public FooDTO toDto(Foo foo)在简单函数内部运行插件的场景
            psiLocalVariable = PsiTreeUtil.collectElementsOfType(generateContext.getPsiFile(), PsiLocalVariable.class)
                    .stream()
                    .findAny()
                    .orElse(null);
        }

        if (psiLocalVariable != null) {
            clazzParamName = psiLocalVariable.getName();
            String clazzName;

            // 判断是否有类的内部类 A.B
            String clazzNameStr = psiLocalVariable.getType().getCanonicalText();
            if (clazzNameStr.contains("\\.")) {
                String clazzNameImport = clazzNameStr;
                if (clazzNameStr.indexOf(".") > 0) {
                    clazzName = clazzNameStr.substring(clazzNameStr.lastIndexOf(".") + 1);
                    clazzNameImport = clazzNameStr;
                } else {
                    clazzName = clazzNameStr;
                }

                psiClass = getPsiClasses(generateContext, clazzNameImport, clazzName);
            } else {
                // 通过光标步长递进找到属性名称
                PsiFile psiFile = generateContext.getPsiFile();
                Editor editor = generateContext.getEditor();
                int offsetStep = ((PsiLocalVariableImpl) psiLocalVariable).getStartOffset() + 1;
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

                clazzName = elementAt.getText();

                PsiClass[] psiClasses = PsiShortNamesCache.getInstance(generateContext.getProject())
                        .getClassesByName(clazzName, GlobalSearchScope.allScope(generateContext.getProject()));
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

        return new SetObjConfigDO(null == psiLocalVariable ? "" : psiLocalVariable.getType().getCanonicalText(),
                null == psiClass ? "" : psiClass.getQualifiedName(),
                clazzParamName,
                paramList,
                paramMtdMap,
                paramNameMap,
                repair,
                isUsedLombokBuilder(psiClass));
    }

    @Override
    protected GetObjConfigDO getObjConfigDOByClipboardText(GenerateContext generateContext) {
        // 获取剪切板信息 【实际使用可补充一些必要的参数判断】
        String systemClipboardText = Utils.getSystemClipboardText().trim();
        // 按照默认规则提取信息，例如：UserDto userDto
        String[] split = systemClipboardText.split("\\s");
        if (split.length != 2) {
            //精确定位不到vo时，尝试直接从入参获取。适用：public FooDTO toDto(Foo foo)在简单函数内部运行插件的场景
            split = PsiTreeUtil.collectElementsOfType(generateContext.getPsiFile(), PsiParameter.class)
                    .stream()
                    .findAny()
                    .map(psiParameter -> psiParameter.getType().getPresentableText() + " " + psiParameter.getName())
                    .map(s -> s.split("\\s"))
                    .orElse(null);
        }
        if (split == null || split.length != 2) {
            return new GetObjConfigDO("", null, null, new HashMap<>());
        }
        // 摘取复制对象中的类和属性，同时支持复制 cn.xxx.class
        String clazzName;
        String clazzParam = split[1].trim();
        String clazzNameImport = "";
        String clazzNameStr = split[0].trim();
        if (clazzNameStr.indexOf(".") > 0) {
            clazzName = clazzNameStr.substring(clazzNameStr.lastIndexOf(".") + 1);
            clazzNameImport = clazzNameStr;
        } else {
            clazzName = split[0].trim();
        }

        // 上下文检测，找到符合的复制类
        PsiClass psiContextClass = getPsiClasses(generateContext, clazzNameImport, clazzName);

        List<PsiClass> psiClassLinkList = getPsiClassLinkList(psiContextClass);
        Map<String, String> paramMtdMap = new HashMap<>();
        Pattern getM = Pattern.compile(getRegex);
        for (PsiClass psi : psiClassLinkList) {
            MethodVO methodVO = getMethods(psi, getRegex, "get");
            for (String methodName : methodVO.getMethodNameList()) {
                String param = getM.matcher(methodName).replaceAll("$1").toLowerCase();
                paramMtdMap.put(param, methodName);
            }
        }
        return new GetObjConfigDO(psiContextClass.getQualifiedName(), clazzName, clazzParam, paramMtdMap);
    }

    @Override
    protected void convertSetting(Project project,
                                  GenerateContext generateContext,
                                  SetObjConfigDO setObjConfigDO,
                                  GetObjConfigDO getObjConfigDO) {
        ShowSettingsUtil.getInstance()
                .editConfigurable(project,
                        new ConvertSettingUI(project, generateContext, setObjConfigDO, getObjConfigDO));
    }

    @Override
    protected void weavingSetGetCode(GenerateContext generateContext,
                                     SetObjConfigDO setObjConfigDO,
                                     GetObjConfigDO getObjConfigDO) {
        Application application = ApplicationManager.getApplication();
        // 获取空格位置长度
        int distance = Utils.getWordStartOffset(generateContext.getEditorText(), generateContext.getOffset())
                - generateContext.getStartOffset()
                - setObjConfigDO.getRepair();
        application.runWriteAction(() -> {
            StringBuilder blankSpace = new StringBuilder();
            for (int i = 0; i < distance; i++) {
                blankSpace.append(" ");
            }
            int lineNumberCurrent = generateContext.getDocument().getLineNumber(generateContext.getOffset()) + 1;

            /*
             * 判断是使用了 Lombok Builder 模式
             * UserDTO userDTO = UserDTO.builder()
             *              .userId(userVO.getUserId())
             *              .userName(userVO.getUserName())
             *              .userAge(userVO.getUserAge())
             *              .build();
             */
            if (setObjConfigDO.isLombokBuilder()) {
                int finalLineNumberCurrent = lineNumberCurrent;
                WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                    String clazzName = setObjConfigDO.getClazzName();
                    String clazzParam = setObjConfigDO.getClazzParamName();
                    int lineEndOffset = generateContext.getDocument().getLineEndOffset(finalLineNumberCurrent - 1);
                    int lineStartOffset = generateContext.getDocument().getLineStartOffset(finalLineNumberCurrent - 1);
                    generateContext.getDocument().deleteString(lineStartOffset, lineEndOffset);
                    generateContext.getDocument()
                            .insertString(generateContext.getDocument().getLineStartOffset(finalLineNumberCurrent - 1),
                                    blankSpace
                                            + clazzName
                                            + " "
                                            + clazzParam
                                            + " = "
                                            + setObjConfigDO.getClazzName()
                                            + ".builder()");
                });
                List<String> setMtdList = setObjConfigDO.getParamList();
                for (String param : setMtdList) {
                    int lineStartOffset = generateContext.getDocument().getLineStartOffset(lineNumberCurrent++);
                    WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                        String builderMethod = blankSpace
                                + blankSpace.toString()
                                + "."
                                + setObjConfigDO.getParamNameMap().get(param)
                                + "("
                                + (null == getObjConfigDO.getParamMtdMap().get(param) ?
                                "" :
                                getObjConfigDO.getClazzParam()
                                        + "."
                                        + getObjConfigDO.getParamMtdMap().get(param)
                                        + "()")
                                + ")";
                        generateContext.getDocument().insertString(lineStartOffset, builderMethod + "\n");
                        generateContext.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                        generateContext.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    });
                }
                int lineStartOffset = generateContext.getDocument().getLineStartOffset(lineNumberCurrent);
                WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                    generateContext.getDocument()
                            .insertString(lineStartOffset, blankSpace + blankSpace.toString() + ".build();\n");
                    generateContext.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                    generateContext.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                });
            } else {
                List<String> setMtdList = setObjConfigDO.getParamList();
                for (String param : setMtdList) {
                    int lineStartOffset = generateContext.getDocument().getLineStartOffset(lineNumberCurrent++);
                    WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                        generateContext.getDocument()
                                .insertString(lineStartOffset,
                                        blankSpace
                                                + setObjConfigDO.getClazzParamName()
                                                + "."
                                                + setObjConfigDO.getParamMtdMap().get(param)
                                                + "("
                                                + (null == getObjConfigDO.getParamMtdMap().get(param) ?
                                                "" :
                                                getObjConfigDO.getClazzParam() + "." + getObjConfigDO.getParamMtdMap()
                                                        .get(param) + "()")
                                                + ");\n");
                        generateContext.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                        generateContext.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    });
                }
            }
        });
    }
}
