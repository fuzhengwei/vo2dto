package cn.bugstack.guide.idea.plugin.ui;

import cn.bugstack.guide.idea.plugin.domain.model.GenerateContext;
import cn.bugstack.guide.idea.plugin.domain.model.GetObjConfigDO;
import cn.bugstack.guide.idea.plugin.domain.model.SetObjConfigDO;
import cn.bugstack.guide.idea.plugin.infrastructure.Utils;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.List;

/**
 * @description:
 * @author: 小傅哥，微信：fustack
 * @date: 2022/1/9
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class ConvertSettingUI implements Configurable {

    private JPanel main;
    private JTable convertTable;
    private JLabel toLabelVal;
    private JLabel fromLabelVal;
    private JRadioButton selectAllRadioButton;
    private JRadioButton selectExistRadioButton;
    private JRadioButton selectNullRadioButton;

    private GenerateContext generateContext;
    private int repair;

    public ConvertSettingUI(Project project, GenerateContext generateContext, SetObjConfigDO setObjConfigDO, GetObjConfigDO getObjConfigDO, int repair) {

        this.generateContext = generateContext;
        this.repair = repair;

        // 初始化按钮
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(selectAllRadioButton);
        buttonGroup.add(selectExistRadioButton);
        buttonGroup.add(selectNullRadioButton);
        selectExistRadioButton.setSelected(true);

        // 选择全部的
        selectAllRadioButton.addActionListener(e -> {
            convertTable.selectAll();
        });

        // 选择存在的
        selectExistRadioButton.addActionListener(e -> {
            convertTable.clearSelection();
            int rowCount = convertTable.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Object setVal = convertTable.getValueAt(i, 1);
                Object getVal = convertTable.getValueAt(i, 2);
                if (null != setVal && null != getVal) {
                    convertTable.addRowSelectionInterval(i, i);
                }
            }
        });

        // 清空选择
        selectNullRadioButton.addActionListener(e -> {
            convertTable.clearSelection();
        });

        // 转换类名
        fromLabelVal.setText("".equals(getObjConfigDO.getQualifiedName()) ? "您尚未复制被转换对象 X x" : getObjConfigDO.getQualifiedName());
        toLabelVal.setText("".equals(setObjConfigDO.getQualifiedName()) ? "您尚未定位转换对象 Y y" : setObjConfigDO.getQualifiedName());

        // 转换属性
        String[] title = {"", setObjConfigDO.getClazzName(), "".equals(getObjConfigDO.getClazzName()) ? "Null" : getObjConfigDO.getClazzName()};
        List<String> setMtdList = setObjConfigDO.getParamList();
        Object[][] data = new Object[setMtdList.size()][3];
        for (int i = 0; i < setMtdList.size(); i++) {
            // set info
            String param = setMtdList.get(i);
            data[i][1] = setObjConfigDO.getClazzParamName() + "." + setObjConfigDO.getParamMtdMap().get(param);
            // get info
            String getStr = getObjConfigDO.getParamMtdMap().get(param);
            if (null == getStr) continue;
            data[i][2] = getObjConfigDO.getClazzParam() + "." + getObjConfigDO.getParamMtdMap().get(param);
        }

        // 设置元素
        convertTable.setModel(new DefaultTableModel(data, title));
        TableColumn tc = convertTable.getColumnModel().getColumn(0);
        tc.setCellEditor(convertTable.getDefaultEditor(Boolean.class));
        tc.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JCheckBox ck = new JCheckBox();
            ck.setSelected(isSelected);
            return ck;
        });
        tc.setMaxWidth(40);

        // 初始化选择对象
        int rowCount = convertTable.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Object setVal = convertTable.getValueAt(i, 1);
            Object getVal = convertTable.getValueAt(i, 2);
            if (null != setVal && null != getVal) {
                convertTable.addRowSelectionInterval(i, i);
            }
        }

    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Vo2Dto Convert Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return main;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {

        Application application = ApplicationManager.getApplication();
        // 获取空格位置长度
        int distance = Utils.getWordStartOffset(generateContext.getEditorText(), generateContext.getOffset()) - generateContext.getStartOffset() - repair;

        application.runWriteAction(() -> {
            StringBuilder blankSpace = new StringBuilder();
            for (int i = 0; i < distance; i++) {
                blankSpace.append(" ");
            }

            int lineNumberCurrent = generateContext.getDocument().getLineNumber(generateContext.getOffset()) + 1;

            int[] selectedRows = convertTable.getSelectedRows();
            for (int idx : selectedRows) {
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
