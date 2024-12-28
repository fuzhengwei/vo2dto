package cn.bugstack.guide.idea.plugin.domain.service;

import cn.bugstack.guide.idea.plugin.domain.model.MethodVO;
import cn.bugstack.guide.idea.plugin.domain.service.node.ConvertSettingNode;
import cn.bugstack.guide.idea.plugin.domain.service.node.GetObjConfigNode;
import cn.bugstack.guide.idea.plugin.domain.service.node.SetObjConfigNode;
import cn.bugstack.guide.idea.plugin.domain.service.node.WeavingSetGetCodeNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractGenerateStrategySupport extends AbstractStrategyRouter<AnActionEvent, cn.bugstack.guide.idea.plugin.domain.service.factory.DefaultGenerateStrategyFactory.DynamicContext, Boolean> {

    protected SetObjConfigNode setObjConfigNode;

    protected GetObjConfigNode getObjConfigNode;

    protected WeavingSetGetCodeNode weavingSetGetCodeNode;

    protected ConvertSettingNode convertSettingNode;

    protected final String setRegex = "set(\\w+)";
    protected final String getRegex = "get(\\w+)";

    protected List<PsiClass> getPsiClassLinkList(PsiClass psiClass) {
        List<PsiClass> psiClassList = new ArrayList<>();
        PsiClass currentClass = psiClass;
        while (null != currentClass && !"Object".equals(currentClass.getName())) {
            psiClassList.add(currentClass);
            currentClass = currentClass.getSuperClass();
        }
        Collections.reverse(psiClassList);
        return psiClassList;
    }

    protected MethodVO getMethods(PsiClass psiClass, String regex, String typeStr) {
        PsiMethod[] psiMethods = psiClass.getMethods();
        List<String> fieldNameList = new ArrayList<>();
        List<String> methodNameList = new ArrayList<>();

        PsiField[] psiFields = psiClass.getFields();
        for (PsiField field : psiFields) {
            String fieldName = field.getName();
            fieldNameList.add(field.getName());

            PsiAnnotation getter = field.getAnnotation("lombok.Getter");
            if (null != getter) {
                methodNameList.add("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
            }

            PsiAnnotation setter = field.getAnnotation("lombok.Setter");
            if (null != setter) {
                methodNameList.add("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
            }
        }

        // 判断使用了 lombok，需要补全生成 get、set
        if (("set".equals(typeStr) && isUsedLombokBuilder(psiClass)) || isUsedLombokData(psiClass) || ("get".equals(typeStr) && isUserLombokGetter(psiClass)) || ("set".equals(typeStr) && isUserLombokSetter(psiClass))) {
            Pattern p = Pattern.compile("static.*?final|final.*?static");
            PsiField[] fields = psiClass.getFields();
            for (PsiField psiField : fields) {
                PsiElement context = psiField.getNameIdentifier().getContext();
                if (null == context) continue;
                String fieldVal = context.getText();
                // serialVersionUID 判断
                if (fieldVal.contains("serialVersionUID")) {
                    continue;
                }
                // static final 常量判断过滤
                Matcher matcher = p.matcher(fieldVal);
                if (matcher.find()) {
                    continue;
                }
                String name = psiField.getNameIdentifier().getText();
                String methodName = typeStr + name.substring(0, 1).toUpperCase() + name.substring(1);
                if (!methodNameList.contains(methodName)) {
                    methodNameList.add(methodName);
                }
                fieldNameList.add(name);
            }

            for (PsiMethod method : psiMethods) {
                String methodName = method.getName();
                if (Pattern.matches(regex, methodName) && !methodNameList.contains(methodName)) {
                    methodNameList.add(methodName);
                    continue;
                }

                PsiAnnotation getter = method.getAnnotation("Getter");
                if (null != getter) {
                    methodNameList.add("get" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1));
                }

                PsiAnnotation setter = method.getAnnotation("Setter");
                if (null != setter) {
                    methodNameList.add("set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1));
                }
            }

            return new MethodVO(fieldNameList, methodNameList);
        }

        // 正常创建的get、set，直接获取即可
        for (PsiMethod method : psiMethods) {
            String methodName = method.getName();
            if (Pattern.matches(regex, methodName)) {
                methodNameList.add(methodName);
            }
        }

        return new MethodVO(fieldNameList, methodNameList);
    }


    protected boolean isUsedLombokData(PsiClass psiClass) {
        return null != psiClass.getAnnotation("lombok.Data");
    }

    protected boolean isUserLombokGetter(PsiClass psiClass) {
        return null != psiClass.getAnnotation("lombok.Getter");
    }

    protected boolean isUserLombokSetter(PsiClass psiClass) {
        return null != psiClass.getAnnotation("lombok.Setter");
    }

    protected boolean isUsedLombokBuilder(PsiClass psiClass) {
        return null != psiClass.getAnnotation("lombok.Builder");
    }

    protected List<String> getImportList(String docText) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("import(.*?);");
        Matcher m = p.matcher(docText);
        while (m.find()) {
            String val = m.group(1).replaceAll(" ", "");
            list.add(val.substring(0, val.lastIndexOf(".")));
        }
        return list;
    }

}
