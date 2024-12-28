package cn.bugstack.guide.idea.plugin.domain.service.generate;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 受理策略处理
 * T 入参类型
 * D 上下文参数
 * R 返参类型
 * @create 2024-12-14 12:06
 */
public interface StrategyHandler<T, D, R> {

    StrategyHandler DEFAULT = (T, D) -> null;

    R apply(T requestParameter, D dynamicContext) throws Exception;

}