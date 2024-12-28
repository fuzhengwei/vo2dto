package cn.bugstack.guide.idea.plugin.ui;

//import cn.bugstack.guide.idea.plugin.domain.model.projectConfigVO;
//import cn.bugstack.guide.idea.plugin.domain.model.getObjConfigVO;
//import cn.bugstack.guide.idea.plugin.domain.model.setObjConfigVO;
import cn.bugstack.guide.idea.plugin.domain.service.generate.factory.DefaultGenerateStrategyFactory;
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

//    private projectConfigVO projectConfigVO;
//    private setObjConfigVO setObjConfigVO;
//    private getObjConfigVO getObjConfigVO;

    private DefaultGenerateStrategyFactory.ProjectConfigVO projectConfigVO;
    private DefaultGenerateStrategyFactory.SetObjConfigVO setObjConfigVO;
    private DefaultGenerateStrategyFactory.GetObjConfigVO getObjConfigVO;
    
    protected DataSetting.DataState state;

    public ConvertSettingSupport(Project project, 
                                 DefaultGenerateStrategyFactory.ProjectConfigVO projectConfigVO,
                                 DefaultGenerateStrategyFactory.SetObjConfigVO setObjConfigVO,
                                 DefaultGenerateStrategyFactory.GetObjConfigVO getObjConfigVO) {
        // 从数据记录中选择
        this.projectConfigVO = projectConfigVO;
        this.setObjConfigVO = setObjConfigVO;
        this.getObjConfigVO = getObjConfigVO;

        // 配置数据
        state = DataSetting.getInstance(project).getState();
    }

    protected String getFromLabelValText() {
        String qualifiedName = getObjConfigVO.getQualifiedName();
        if ("".equals(qualifiedName)) {
            return "您尚未复制被转换对象，例如：X x = new X() 需要复制 X x";
        }
        return qualifiedName;
    }

    protected String getToLabelValText() {
        String qualifiedName = setObjConfigVO.getQualifiedName();
        if ("".equals(qualifiedName)) {
            return "您尚未定位转换对象 Y y，例如把鼠标定位到对象 Y 或者 y 上";
        }
        return qualifiedName;
    }

    protected String[] getTableTitle() {
        return new String[]{"", setObjConfigVO.getClazzName(), "".equals(getObjConfigVO.getClazzName()) ? "Null" : getObjConfigVO.getClazzName()};
    }

    protected Object[][] getTableData() {
        List<String> setMtdList = setObjConfigVO.getParamList();
        Object[][] data = new Object[setMtdList.size()][3];
        for (int i = 0; i < setMtdList.size(); i++) {
            data[i][0] = Boolean.FALSE;
            // set info
            String param = setMtdList.get(i);
            data[i][1] = setObjConfigVO.getClazzParamName() + "." + setObjConfigVO.getParamMtdMap().get(param);
            // get info
            String getStr = getObjConfigVO.getParamMtdMap().get(param);
            if (null == getStr) continue;
            data[i][2] = getObjConfigVO.getClazzParam() + "." + getObjConfigVO.getParamMtdMap().get(param);
        }
        return data;
    }

    protected void weavingSetGetCode(JTable convertTable) {

        Application application = ApplicationManager.getApplication();
        // 获取空格位置长度
        int distance = Utils.getWordStartOffset(projectConfigVO.getEditorText(), projectConfigVO.getOffset()) - projectConfigVO.getStartOffset() - setObjConfigVO.getRepair();

        application.runWriteAction(() -> {
            StringBuilder blankSpace = new StringBuilder();
            for (int i = 0; i < distance; i++) {
                blankSpace.append(" ");
            }

            int lineNumberCurrent = projectConfigVO.getDocument().getLineNumber(projectConfigVO.getOffset()) + 1;

            // 判断是否使用了 Lombok 标签的 Builder 且开启了使用 Lombok Builder
            if (setObjConfigVO.isLombokBuilder() && state.isUsedLombokBuilder()) {
                /*
                 * 判断是使用了 Lombok Builder 模式
                 * UserDTO userDTO = UserDTO.builder()
                 *              .userId(userVO.getUserId())
                 *              .userName(userVO.getUserName())
                 *              .userAge(userVO.getUserAge())
                 *              .build();
                 */
                int finalLineNumberCurrent = lineNumberCurrent;
                WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                    String clazzName = setObjConfigVO.getClazzName();
                    String clazzParam = setObjConfigVO.getClazzParamName();
                    int lineEndOffset = projectConfigVO.getDocument().getLineEndOffset(finalLineNumberCurrent - 1);
                    int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(finalLineNumberCurrent - 1);

                    projectConfigVO.getDocument().deleteString(lineStartOffset, lineEndOffset);
                    projectConfigVO.getDocument().insertString(projectConfigVO.getDocument().getLineStartOffset(finalLineNumberCurrent - 1), blankSpace + clazzName + " " + clazzParam + " = " + setObjConfigVO.getClazzName() + ".builder()");
                });

                // setNullRadioButton -> 全部清空，则默认生成空转换
                if ("setNullRadioButton".equals(state.getSelectRadio())) {
                    List<String> setMtdList = setObjConfigVO.getParamList();
                    for (String param : setMtdList) {
                        int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent++);
                        WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                            String builderMethod = blankSpace + blankSpace.toString() + "." + setObjConfigVO.getParamNameMap().get(param) + "()";

                            projectConfigVO.getDocument().insertString(lineStartOffset, builderMethod + "\n");
                            projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                            projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                        });
                    }

                    int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent);
                    WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                        projectConfigVO.getDocument().insertString(lineStartOffset, blankSpace + blankSpace.toString() + ".build();\n");
                        projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                        projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    });
                } else {
                    // selectAllRadioButton、selectExistRadioButton -> 按照选择进行转换插入
                    int rowCount = convertTable.getRowCount();
                    for (int idx = 0; idx < rowCount; idx++) {
                        boolean isSelected = (boolean) convertTable.getValueAt(idx, 0);
                        if (!isSelected) continue;

                        int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent++);
                        Object setVal = convertTable.getValueAt(idx, 1);
                        Object getVal = convertTable.getValueAt(idx, 2);

                        WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                            String setValParam = setVal.toString().substring(setVal.toString().indexOf("set") + 3).toLowerCase();
                            String builderMethod = blankSpace + blankSpace.toString() + "." + setObjConfigVO.getParamNameMap().get(setValParam) + "(" + (null == getVal ? "" : getVal + "()") + ")";

                            projectConfigVO.getDocument().insertString(lineStartOffset, builderMethod + "\n");
                            projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                            projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                        });
                    }
                }

                int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent);
                WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                    projectConfigVO.getDocument().insertString(lineStartOffset, blankSpace + blankSpace.toString() + ".build();\n");
                    projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                    projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                });
            } else {
                // setNullRadioButton -> 全部清空，则默认生成空转换
                if ("setNullRadioButton".equals(state.getSelectRadio())) {
                    List<String> setMtdList = setObjConfigVO.getParamList();
                    for (String param : setMtdList) {
                        int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent++);

                        WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                            projectConfigVO.getDocument().insertString(lineStartOffset, blankSpace + blankSpace.toString() + setObjConfigVO.getClazzParamName() + "." + setObjConfigVO.getParamMtdMap().get(param) + "();\n");
                            projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                            projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                        });

                    }
                    return;
                }

                // selectAllRadioButton、selectExistRadioButton -> 按照选择进行转换插入
                int rowCount = convertTable.getRowCount();
                for (int idx = 0; idx < rowCount; idx++) {
                    boolean isSelected = (boolean) convertTable.getValueAt(idx, 0);
                    if (!isSelected) continue;

                    int lineStartOffset = projectConfigVO.getDocument().getLineStartOffset(lineNumberCurrent++);
                    Object setVal = convertTable.getValueAt(idx, 1);
                    Object getVal = convertTable.getValueAt(idx, 2);

                    WriteCommandAction.runWriteCommandAction(projectConfigVO.getProject(), () -> {
                        projectConfigVO.getDocument().insertString(lineStartOffset, blankSpace + setVal.toString() + "(" + (null == getVal ? "" : getVal + "()") + ");\n");
                        projectConfigVO.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                        projectConfigVO.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    });
                }
            }

        });
    }

}
