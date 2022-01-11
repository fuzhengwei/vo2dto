package cn.bugstack.guide.idea.plugin.ui;

import cn.bugstack.guide.idea.plugin.domain.model.GenerateContext;
import cn.bugstack.guide.idea.plugin.domain.model.GetObjConfigDO;
import cn.bugstack.guide.idea.plugin.domain.model.SetObjConfigDO;
import cn.bugstack.guide.idea.plugin.infrastructure.DataSetting;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * @description: 转换配置框体
 * @author: 小傅哥，微信：fustack
 * @date: 2022/1/9
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class ConvertSettingUI extends ConvertSettingSupport implements Configurable {

    private JPanel main;
    private JTable convertTable;
    private JLabel toLabelVal;
    private JLabel fromLabelVal;
    private JRadioButton selectAllRadioButton;
    private JRadioButton selectExistRadioButton;
    private JRadioButton selectNullRadioButton;

    public ConvertSettingUI(Project project, GenerateContext generateContext, SetObjConfigDO setObjConfigDO, GetObjConfigDO getObjConfigDO) {
        super(project, generateContext, setObjConfigDO, getObjConfigDO);
        main.setSize(300, 400);

        // 添加按钮
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(selectAllRadioButton);
        buttonGroup.add(selectExistRadioButton);
        buttonGroup.add(selectNullRadioButton);

        // 转换类名
        fromLabelVal.setText(getFromLabelValText());
        toLabelVal.setText(getToLabelValText());

        // 设置元素
        convertTable.setModel(new DefaultTableModel(getTableData(), getTableTitle()));
        TableColumn tc = convertTable.getColumnModel().getColumn(0);
        tc.setCellEditor(convertTable.getDefaultEditor(Boolean.class));
        tc.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JCheckBox ck = new JCheckBox();
            ck.setSelected(isSelected);
            return ck;
        });
        tc.setMaxWidth(20);

        // 添加事件
        addRadioButtonEvent(state);

        // 初始化选择配置
        initSelectConfig(state);
    }

    private void addRadioButtonEvent(DataSetting.DataState state) {
        // 选择全部的
        selectAllRadioButton.addActionListener(e -> {
            convertTable.selectAll();
            state.setSelectRadio("selectAllRadioButton");
        });

        // 选择存在的
        selectExistRadioButton.addActionListener(e -> {
            convertTable.clearSelection();
            setRowSelectData();
            state.setSelectRadio("selectExistRadioButton");
        });

        // 清空选择
        selectNullRadioButton.addActionListener(e -> {
            convertTable.clearSelection();
            state.setSelectRadio("selectNullRadioButton");
        });
    }

    private void initSelectConfig(DataSetting.DataState state) {
        switch (state.getSelectRadio()) {
            case "selectAllRadioButton":
                selectAllRadioButton.setSelected(true);
                convertTable.selectAll();
                break;
            case "selectExistRadioButton":
                convertTable.clearSelection();
                setRowSelectData();
                selectExistRadioButton.setSelected(true);
                break;
            case "selectNullRadioButton":
                convertTable.clearSelection();
                selectNullRadioButton.setSelected(true);
                break;
        }
    }

    private void setRowSelectData() {
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
        weavingSetGetCode(convertTable);
    }

}
