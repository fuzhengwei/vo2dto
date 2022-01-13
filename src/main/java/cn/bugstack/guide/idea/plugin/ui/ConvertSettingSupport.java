package cn.bugstack.guide.idea.plugin.ui;

import cn.bugstack.guide.idea.plugin.domain.model.GenerateContext;
import cn.bugstack.guide.idea.plugin.domain.model.GetObjConfigDO;
import cn.bugstack.guide.idea.plugin.domain.model.SetObjConfigDO;
import cn.bugstack.guide.idea.plugin.infrastructure.DataSetting;
import cn.bugstack.guide.idea.plugin.infrastructure.Utils;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.List;

/**
 * @description: 处理逻辑行为
 * @author: 小傅哥，微信：fustack
 * @date: 2022/1/9
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class ConvertSettingSupport {

    private GenerateContext generateContext;
    private SetObjConfigDO setObjConfigDO;
    private GetObjConfigDO getObjConfigDO;

    protected DataSetting.DataState state;

    public ConvertSettingSupport(Project project, GenerateContext generateContext, SetObjConfigDO setObjConfigDO, GetObjConfigDO getObjConfigDO) {
        // 从数据记录中选择
        this.generateContext = generateContext;
        this.setObjConfigDO = setObjConfigDO;
        this.getObjConfigDO = getObjConfigDO;

        // 配置数据
        state = DataSetting.getInstance(project).getState();
    }

    protected String getFromLabelValText() {
        String qualifiedName = getObjConfigDO.getQualifiedName();
        if ("".equals(qualifiedName)) {
            return "您尚未复制被转换对象，例如：X x = new X() 需要复制 X x";
        }
        return qualifiedName;
    }

    protected String getToLabelValText() {
        String qualifiedName = setObjConfigDO.getQualifiedName();
        if ("".equals(qualifiedName)) {
            return "您尚未定位转换对象 Y y，例如把鼠标定位到对象 Y 或者 y 上";
        }
        return qualifiedName;
    }

    protected String[] getTableTitle() {
        return new String[]{"", setObjConfigDO.getClazzName(), "".equals(getObjConfigDO.getClazzName()) ? "Null" : getObjConfigDO.getClazzName()};
    }

    protected Object[][] getTableData() {
        List<String> setMtdList = setObjConfigDO.getParamList();
        Object[][] data = new Object[setMtdList.size()][3];
        for (int i = 0; i < setMtdList.size(); i++) {
            data[i][0] = Boolean.FALSE;
            // set info
            String param = setMtdList.get(i);
            data[i][1] = setObjConfigDO.getClazzParamName() + "." + setObjConfigDO.getParamMtdMap().get(param);
            // get info
            String getStr = getObjConfigDO.getParamMtdMap().get(param);
            if (null == getStr) continue;
            data[i][2] = getObjConfigDO.getClazzParam() + "." + getObjConfigDO.getParamMtdMap().get(param);
        }
        return data;
    }

    protected void weavingSetGetCode(JTable convertTable) {

        Application application = ApplicationManager.getApplication();
        // 获取空格位置长度
        int distance = Utils.getWordStartOffset(generateContext.getEditorText(), generateContext.getOffset()) - generateContext.getStartOffset() - setObjConfigDO.getRepair();

        application.runWriteAction(() -> {
            StringBuilder blankSpace = new StringBuilder();
            for (int i = 0; i < distance; i++) {
                blankSpace.append(" ");
            }

            int lineNumberCurrent = generateContext.getDocument().getLineNumber(generateContext.getOffset()) + 1;

            // setNullRadioButton -> 全部清空，则默认生成空转换
            if ("setNullRadioButton".equals(state.getSelectRadio())) {
                List<String> setMtdList = setObjConfigDO.getParamList();
                for (String param : setMtdList) {
                    int lineStartOffset = generateContext.getDocument().getLineStartOffset(lineNumberCurrent++);

                    WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                        generateContext.getDocument().insertString(lineStartOffset, blankSpace + setObjConfigDO.getClazzParamName() + "." + setObjConfigDO.getParamMtdMap().get(param) + "();\n");
                        generateContext.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                        generateContext.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    });

                }
                return;
            }

            // selectAllRadioButton、selectExistRadioButton -> 按照选择进行转换插入
            int rowCount = convertTable.getRowCount();
            for (int idx = 0; idx < rowCount; idx++) {
                boolean isSelected = (boolean) convertTable.getValueAt(idx, 0);
                if (!isSelected) continue;

                int lineStartOffset = generateContext.getDocument().getLineStartOffset(lineNumberCurrent++);
                Object setVal = convertTable.getValueAt(idx, 1);
                Object getVal = convertTable.getValueAt(idx, 2);

                WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                    generateContext.getDocument().insertString(lineStartOffset, blankSpace + setVal.toString() + "(" + (null == getVal ? "" : getVal + "()") + ");\n");
                    generateContext.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                    generateContext.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                });
            }

        });
    }

}
