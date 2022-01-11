package cn.bugstack.guide.idea.plugin.ui;

import javax.swing.*;

/**
 * @description: Vo2Dto 是否弹窗提醒映射关系配置
 * @author: 小傅哥，微信：fustack
 * @date: 2022/1/9
 * @github: https://github.com/fuzhengwei
 * @Copyright: 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class ConfigSettingUI {

    private JPanel main;
    private JRadioButton showRadioButton;
    private JRadioButton hideButton;

    public ConfigSettingUI() {
        // 添加按钮组
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(showRadioButton);
        buttonGroup.add(hideButton);
        showRadioButton.setSelected(true);
    }

    public JComponent getComponent() {
        return main;
    }

    public String getRadioVal() {
        return showRadioButton.isSelected() ? "show" : "hide";
    }

}
