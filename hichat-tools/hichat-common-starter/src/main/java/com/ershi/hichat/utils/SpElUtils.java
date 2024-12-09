package com.ershi.hichat.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Spring EL表达式解析工具类
 * 提供方法参数解析和方法键获取功能
 * @author Ershi
 * @date 2024/12/08
 */
public class SpElUtils {

    // 使用SpelExpressionParser作为表达式解析器
    private static final ExpressionParser parser = new SpelExpressionParser();
    // 使用DefaultParameterNameDiscoverer来发现参数名
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 解析SpringEL表达式，动态获取方法指定参数的值
     *
     * @param method 要解析的方法
     * @param args 方法的参数值数组
     * @param spEl SpEL表达式字符串 -> 要获取值的参数名
     * @return 解析后的字符串结果，返回目标参数的值
     */
    public static String parseMethodArgsSpEl(Method method, Object[] args, String spEl) {
        // 解析方法参数名，如果无法解析则使用空数组
        String[] params = Optional.ofNullable(parameterNameDiscoverer.getParameterNames(method)).orElse(new String[]{});
        // 创建标准的EL上下文对象
         EvaluationContext context = new StandardEvaluationContext();
        // 将方法参数名-参数值绑定到EL上下文中
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        // 解析SpEL表达式
        Expression expression = parser.parseExpression(spEl);
        // 返回表达式解析结果
        return expression.getValue(context, String.class);
    }

    /**
     * 生成方法的唯一键
     *
     * @param method 方法对象
     * @return 方法的唯一键，格式为：类名#方法名
     */
    public static String getMethodKey(Method method) {
        // 拼接方法所属类和方法名作为方法键
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
