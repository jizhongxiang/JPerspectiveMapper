package com.aopie.jpm.exceptions;

/**
 * 当文本位置信息无效或为空时抛出的异常。
 * <p>
 * 用于标识在处理文本及其位置信息时，位置信息（textPosition）为空或不满足预期的错误情况。
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/17
 */
public class InvalidTextPositionException extends IllegalArgumentException {
    // 构造方法，初始化异常对象时设置异常信息
    public InvalidTextPositionException() {
        super("文本位置信息无效或为空。");
    }
}
