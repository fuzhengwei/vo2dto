package cn.bugstack.guide.idea.plugin.domain.model;

import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 方法信息值对象
 * @create 2023-12-15 21:42
 */
public class MethodVO {

    /**
     * 字段名称列表
     */
    private List<String> fieldNameList;

    /**
     * get\set method name list
     */
    private List<String> methodNameList;

    public MethodVO() {
    }

    public MethodVO(List<String> fieldNameList, List<String> methodNameList) {
        this.fieldNameList = fieldNameList;
        this.methodNameList = methodNameList;
    }

    public List<String> getFieldNameList() {
        return fieldNameList;
    }

    public List<String> getMethodNameList() {
        return methodNameList;
    }

}
