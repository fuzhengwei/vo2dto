package cn.bugstack.guide.idea.plugin.domain.service.node;

import cn.bugstack.guide.idea.plugin.domain.model.MethodVO;
import cn.bugstack.guide.idea.plugin.domain.service.AbstractGenerateStrategySupport;
import cn.bugstack.guide.idea.plugin.domain.service.StrategyHandler;
import cn.bugstack.guide.idea.plugin.domain.service.factory.DefaultGenerateStrategyFactory;
import cn.bugstack.guide.idea.plugin.types.DataSetting;
import cn.bugstack.guide.idea.plugin.types.Utils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;
import java.util.regex.Pattern;

public class GetObjConfigNode extends AbstractGenerateStrategySupport {

    private final WeavingSetGetCodeNode weavingSetGetCodeNode;
    private final ConvertSettingNode convertSettingNode;

    public GetObjConfigNode(WeavingSetGetCodeNode weavingSetGetCodeNode, ConvertSettingNode convertSettingNode) {
        this.weavingSetGetCodeNode = weavingSetGetCodeNode;
        this.convertSettingNode = convertSettingNode;
    }

    @Override
    public Boolean apply(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        // 获取上下文对象
        DefaultGenerateStrategyFactory.ProjectConfigVO projectConfigVO = dynamicContext.getProjectConfigVO();

        // 获取剪切板信息 【实际使用可补充一些必要的参数判断】
        String systemClipboardText = Utils.getSystemClipboardText().trim();
        // 按照默认规则提取信息，例如：UserDto userDto
        String[] split = systemClipboardText.split("\\s");
        if (split.length != 2) {
            //精确定位不到vo时，尝试直接从入参获取。适用：public FooDTO toDto(Foo foo)在简单函数内部运行插件的场景
            split = PsiTreeUtil.collectElementsOfType(projectConfigVO.getPsiFile(), PsiParameter.class)
                    .stream()
                    .findAny()
                    .map(psiParameter -> psiParameter.getType().getPresentableText() + " " + psiParameter.getName())
                    .map(s -> s.split("\\s"))
                    .orElse(null);
        }
        if (split == null || split.length != 2) {
            DefaultGenerateStrategyFactory.GetObjConfigVO getObjConfigVO = new DefaultGenerateStrategyFactory.GetObjConfigVO("", null, null, new HashMap<>());
            dynamicContext.setGetObjConfigVO(getObjConfigVO);
            return router(requestParameter, dynamicContext);
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
        // 获取同名类集合
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(projectConfigVO.getProject())
                .getClassesByName(clazzName, GlobalSearchScope.allScope(projectConfigVO.getProject()));
        // 上下文检测，找到符合的复制类
        PsiClass psiContextClass = null;
        // 相同类名处理
        if (psiClasses.length > 1) {
            // 获取比对包文本
            List<String> importList;
            if (!"".equals(clazzNameImport)) {
                importList = Collections.singletonList(clazzNameImport);
            } else {
                importList = getImportList(projectConfigVO.getDocument().getText());
            }
            // 循环比对，通过引入的包名与类做包名做对比
            for (PsiClass psiClass : psiClasses) {
                String qualifiedName = Objects.requireNonNull(psiClass.getQualifiedName());
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                if (importList.contains(packageName)) {
                    psiContextClass = psiClass;
                    break;
                }
            }
            // 同包下比对
            if (null == psiContextClass) {
                String psiFilePackageName = ((PsiJavaFileImpl) projectConfigVO.getPsiFile()).getPackageName();
                for (PsiClass psiClass : psiClasses) {
                    String qualifiedName = Objects.requireNonNull(psiClass.getQualifiedName());
                    String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                    if (psiFilePackageName.equals(packageName)) {
                        psiContextClass = psiClass;
                        break;
                    }
                }
            }
        }
        if (null == psiContextClass) {
            psiContextClass = psiClasses[0];
        }
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

        DefaultGenerateStrategyFactory.GetObjConfigVO getObjConfigVO = new DefaultGenerateStrategyFactory.GetObjConfigVO(
                psiContextClass.getQualifiedName(),
                clazzName,
                clazzParam,
                paramMtdMap);

        // 写入上下文
        dynamicContext.setGetObjConfigVO(getObjConfigVO);

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<AnActionEvent, DefaultGenerateStrategyFactory.DynamicContext, Boolean> get(AnActionEvent requestParameter, DefaultGenerateStrategyFactory.DynamicContext dynamicContext) throws Exception {
        // 弹框选择，织入代码。分为弹窗提醒和非弹窗提醒
        DataSetting.DataState state = DataSetting.getInstance(dynamicContext.getProjectConfigVO().getProject()).getState();

        assert state != null;
        if ("hide".equals(state.getConfigRadio())) {
            return weavingSetGetCodeNode;
        }

        return convertSettingNode;
    }

}
