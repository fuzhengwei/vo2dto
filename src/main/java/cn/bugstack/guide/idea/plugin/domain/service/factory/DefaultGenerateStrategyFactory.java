package cn.bugstack.guide.idea.plugin.domain.service.factory;

import cn.bugstack.guide.idea.plugin.domain.service.StrategyHandler;
import cn.bugstack.guide.idea.plugin.domain.service.node.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;
import java.util.Map;

public class DefaultGenerateStrategyFactory {

    private final RootNode rootNode;

    public DefaultGenerateStrategyFactory() {
        // 实例化对象
        WeavingSetGetCodeNode weavingSetGetCodeNode = new WeavingSetGetCodeNode();
        ConvertSettingNode convertSettingNode = new ConvertSettingNode();

        GetObjConfigNode getObjConfigNode = new GetObjConfigNode(weavingSetGetCodeNode, convertSettingNode);
        SetObjConfigNode setObjConfigNode = new SetObjConfigNode(getObjConfigNode);

        this.rootNode = new RootNode(setObjConfigNode);
    }

    public StrategyHandler<AnActionEvent, DynamicContext, Boolean> strategyHandler(){
        return rootNode;
    }

    public static class DynamicContext {
        // 工程配置
        private ProjectConfigVO projectConfigVO;
        // Set 值对象
        private SetObjConfigVO setObjConfigVO;
        // Get 值对象
        private GetObjConfigVO getObjConfigVO;

        public ProjectConfigVO getProjectConfigVO() {
            return projectConfigVO;
        }

        public void setProjectConfigVO(ProjectConfigVO projectConfigVO) {
            this.projectConfigVO = projectConfigVO;
        }

        public SetObjConfigVO getSetObjConfigVO() {
            return setObjConfigVO;
        }

        public void setSetObjConfigVO(SetObjConfigVO setObjConfigVO) {
            this.setObjConfigVO = setObjConfigVO;
        }

        public GetObjConfigVO getGetObjConfigVO() {
            return getObjConfigVO;
        }

        public void setGetObjConfigVO(GetObjConfigVO getObjConfigVO) {
            this.getObjConfigVO = getObjConfigVO;
        }
    }

    public static class ProjectConfigVO {
        /**
         * 工程对象
         */
        private Project project;
        /**
         * 文件
         */
        private PsiFile psiFile;
        /**
         * 数据上下文
         */
        private DataContext dataContext;
        /**
         * 编辑器
         */
        private Editor editor;
        /**
         * 元素
         */
        private PsiElement psiElement;
        /**
         * 位点
         */
        private Integer offset;
        /**
         * 文档
         */
        private Document document;
        /**
         * 行号
         */
        private Integer lineNumber;
        /**
         * 开始位置
         */
        private Integer startOffset;
        /**
         * 文本编辑
         */
        private CharSequence editorText;

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public PsiFile getPsiFile() {
            return psiFile;
        }

        public void setPsiFile(PsiFile psiFile) {
            this.psiFile = psiFile;
        }

        public DataContext getDataContext() {
            return dataContext;
        }

        public void setDataContext(DataContext dataContext) {
            this.dataContext = dataContext;
        }

        public Editor getEditor() {
            return editor;
        }

        public void setEditor(Editor editor) {
            this.editor = editor;
        }

        public PsiElement getPsiElement() {
            return psiElement;
        }

        public void setPsiElement(PsiElement psiElement) {
            this.psiElement = psiElement;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }

        public Integer getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(Integer lineNumber) {
            this.lineNumber = lineNumber;
        }

        public Integer getStartOffset() {
            return startOffset;
        }

        public void setStartOffset(Integer startOffset) {
            this.startOffset = startOffset;
        }

        public CharSequence getEditorText() {
            return editorText;
        }

        public void setEditorText(CharSequence editorText) {
            this.editorText = editorText;
        }

    }

    public static class SetObjConfigVO {
        /**
         * 类的名称
         */
        private String clazzName;
        /**
         * 类路径名
         */
        private String qualifiedName;
        /**
         * 类属性名
         */
        private String clazzParamName;
        /**
         * param 集合，保证顺序性
         */
        private List<String> paramList;
        /**
         * key：param val：set方法
         */
        private Map<String, String> paramMtdMap;
        /**
         * key：param val：set方法对应的字段属性
         */
        private Map<String, String> paramNameMap;
        /**
         * 位移记录
         */
        private int repair;
        /**
         * 是否使用了 Lombok 的 Builder 模式
         */
        private boolean isLombokBuilder;

        public SetObjConfigVO(String clazzName,
                              String qualifiedName,
                              String clazzParamName,
                              List<String> paramList,
                              Map<String, String> paramMtdMap,
                              Map<String, String> paramNameMap,
                              int repair,
                              boolean isLombokBuilder) {
            this.clazzName = clazzName;
            this.qualifiedName = qualifiedName;
            this.clazzParamName = clazzParamName;
            this.paramList = paramList;
            this.paramMtdMap = paramMtdMap;
            this.paramNameMap = paramNameMap;
            this.repair = repair;
            this.isLombokBuilder = isLombokBuilder;
        }

        public String getClazzName() {
            return clazzName;
        }

        public void setClazzName(String clazzName) {
            this.clazzName = clazzName;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }

        public void setQualifiedName(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public String getClazzParamName() {
            return clazzParamName;
        }

        public void setClazzParamName(String clazzParamName) {
            this.clazzParamName = clazzParamName;
        }

        public List<String> getParamList() {
            return paramList;
        }

        public void setParamList(List<String> paramList) {
            this.paramList = paramList;
        }

        public Map<String, String> getParamMtdMap() {
            return paramMtdMap;
        }

        public void setParamMtdMap(Map<String, String> paramMtdMap) {
            this.paramMtdMap = paramMtdMap;
        }

        public int getRepair() {
            return repair;
        }

        public void setRepair(int repair) {
            this.repair = repair;
        }

        public boolean isLombokBuilder() {
            return isLombokBuilder;
        }

        public void setLombokBuilder(boolean lombokBuilder) {
            isLombokBuilder = lombokBuilder;
        }

        public Map<String, String> getParamNameMap() {
            return paramNameMap;
        }
    }

    public static class GetObjConfigVO {
        /** 类路径名 */
        private String qualifiedName;
        /** 类名称 */
        private String clazzName;
        /** 属性名 */
        private String clazzParam;

        /** key：param val：get方法 */
        private Map<String, String> paramMtdMap;

        public GetObjConfigVO(String qualifiedName, String clazzName, String clazzParam, Map<String, String> paramMtdMap) {
            this.qualifiedName = qualifiedName;
            this.clazzName = clazzName;
            this.clazzParam = clazzParam;
            this.paramMtdMap = paramMtdMap;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }

        public void setQualifiedName(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public String getClazzName() {
            return clazzName;
        }

        public void setClazzName(String clazzName) {
            this.clazzName = clazzName;
        }

        public String getClazzParam() {
            return clazzParam;
        }

        public void setClazzParam(String clazzParam) {
            this.clazzParam = clazzParam;
        }

        public Map<String, String> getParamMtdMap() {
            return paramMtdMap;
        }

        public void setParamMtdMap(Map<String, String> paramMtdMap) {
            this.paramMtdMap = paramMtdMap;
        }
    }

}
