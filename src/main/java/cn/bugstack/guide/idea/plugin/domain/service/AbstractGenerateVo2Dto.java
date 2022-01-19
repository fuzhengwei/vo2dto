package cn.bugstack.guide.idea.plugin.domain.service;

import cn.bugstack.guide.idea.plugin.application.IGenerateVo2Dto;
import cn.bugstack.guide.idea.plugin.domain.model.GenerateContext;
import cn.bugstack.guide.idea.plugin.domain.model.GetObjConfigDO;
import cn.bugstack.guide.idea.plugin.domain.model.SetObjConfigDO;
import cn.bugstack.guide.idea.plugin.infrastructure.DataSetting;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractGenerateVo2Dto implements IGenerateVo2Dto {

    protected final String setRegex = "set(\\w+)";
    protected final String getRegex = "get(\\w+)";

    @Override
    public void doGenerate(Project project, DataContext dataContext, PsiFile psiFile) {
        // 1. 获取上下文
        GenerateContext generateContext = this.getGenerateContext(project, dataContext, psiFile);

        // 2. 获取对象的 set 方法集合
        SetObjConfigDO setObjConfigDO = this.getSetObjConfigDO(generateContext);

        // 3. 获取对的的 get 方法集合 【从剪切板获取】
        GetObjConfigDO getObjConfigDO = this.getObjConfigDOByClipboardText(generateContext);

        // 4. 弹框选择，织入代码。分为弹窗提醒和非弹窗提醒
        DataSetting.DataState state = DataSetting.getInstance(project).getState();
        assert state != null;
        if ("hide".equals(state.getConfigRadio())) {
            this.weavingSetGetCode(generateContext, setObjConfigDO, getObjConfigDO);
        } else {
            this.convertSetting(project, generateContext, setObjConfigDO, getObjConfigDO);
        }

    }

    protected abstract GenerateContext getGenerateContext(Project project, DataContext dataContext, PsiFile psiFile);

    protected abstract SetObjConfigDO getSetObjConfigDO(GenerateContext generateContext);

    protected abstract GetObjConfigDO getObjConfigDOByClipboardText(GenerateContext generateContext);

    protected abstract void convertSetting(Project project, GenerateContext generateContext, SetObjConfigDO setObjConfigDO, GetObjConfigDO getObjConfigDO);

    protected abstract void weavingSetGetCode(GenerateContext generateContext, SetObjConfigDO setObjConfigDO, GetObjConfigDO getObjConfigDO);

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

    protected List<String> getMethods(PsiClass psiClass, String regex, String typeStr) {
        PsiMethod[] methods = psiClass.getMethods();
        List<String> methodList = new ArrayList<>();

        // 判断使用了 lombok，需要补全生成 get、set
        if (isUsedLombok(psiClass)) {
            Pattern p = Pattern.compile("static.*?final|final.*?static");
            PsiField[] fields = psiClass.getFields();
            for (PsiField psiField : fields) {
                String fieldVal = Objects.requireNonNull(psiField.getNameIdentifier().getContext()).getText();
                // serialVersionUID 判断
                if (fieldVal.contains("serialVersionUID")){
                    continue;
                }
                // static final 常量判断过滤
                Matcher matcher = p.matcher(fieldVal);
                if (matcher.find()){
                    continue;
                }
                String name = psiField.getNameIdentifier().getText();
                methodList.add(typeStr + name.substring(0, 1).toUpperCase() + name.substring(1));
            }

            for (PsiMethod method : methods) {
                String methodName = method.getName();
                if (Pattern.matches(regex, methodName) && !methodList.contains(methodName)) {
                    methodList.add(methodName);
                }
            }

            return methodList;
        }


        // 正常创建的get、set，直接获取即可
        for (PsiMethod method : methods) {
            String methodName = method.getName();
            if (Pattern.matches(regex, methodName)) {
                methodList.add(methodName);
            }
        }

        return methodList;
    }

    private boolean isUsedLombok(PsiClass psiClass) {
        return null != psiClass.getAnnotation("lombok.Data");
    }

    protected List<String> getImportList(String docText) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("import(.*?);");
        Matcher m = p.matcher(docText);
        while (m.find()) {
            String val = m.group(1).replaceAll(" ","");
            list.add(val.substring(0, val.lastIndexOf(".")));
        }
        return list;
    }

}
