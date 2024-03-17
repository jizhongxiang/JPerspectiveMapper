package com.aopie.jpm.exceptions;

/**
 * 表示当期望的文本为空或不存在时抛出的异常。
 * <p>
 * 这个异常通常在进行操作需要非空文本作为前提条件时使用，
 * 比如在处理文本解析或生成字符信息列表等场景。
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/17
 */
public class EmptyTextException extends IllegalArgumentException {

    /**
     * 构造一个带有默认错误消息的EmptyTextException异常。
     */
    public EmptyTextException() {
        super("文本不能为空，无法进行操作。");
    }

    /**
     * 构造一个带有特定错误消息的EmptyTextException异常。
     *
     * @param message 详细的错误信息
     */
    public EmptyTextException(String message) {
        super(message);
    }
}
