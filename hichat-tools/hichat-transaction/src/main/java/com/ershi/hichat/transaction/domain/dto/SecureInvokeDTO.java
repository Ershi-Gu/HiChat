package com.ershi.hichat.transaction.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安全调用方法参数
 * @author Ershi
 * @date 2025/02/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureInvokeDTO {
    /**
     * 方法全限定类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private String parameterTypes;
    /**
     * 参数名
     */
    private String args;
}
