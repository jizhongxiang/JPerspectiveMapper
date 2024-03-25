package com.aopie.jpm.exceptions;

import com.aopie.jpm.model.TempFile;

/**
 * TempFileException 类是一个自定义异常，专门用于处理与 {@link TempFile} 相关的错误。
 * 当在处理临时文件操作中遇到不可恢复的错误时，比如文件创建、写入、删除失败等情况，将抛出此异常。
 *
 * <p>此异常旨在提供更具体的错误信息，帮助开发者快速定位和解决临时文件处理过程中遇到的问题。
 * 与直接使用 {@code RuntimeException} 相比，使用专门的异常类可以使错误处理更加精细化，
 * 并允许调用者根据异常类型做出更合适的响应。</p>
 *
 * <p><b>示例用法：</b></p>
 * <pre>
 * try {
 *     // 临时文件操作
 * } catch (TempFileException e) {
 *     // 处理临时文件相关的异常
 * }
 * </pre>
 *
 * @author JohnsonGee
 * @version 1.0
 * @see TempFile 与此异常紧密相关的临时文件处理类。
 * @since 2024/3/25
 */
public class TempFileException extends RuntimeException {

    /**
     * 构造一个新的 TempFileException，没有详细消息文本。
     */
    public TempFileException() {
        super();
    }

    /**
     * 构造一个新的 TempFileException，带有详细的消息文本。
     *
     * @param message 详细消息文本
     */
    public TempFileException(String message) {
        super(message);
    }

    /**
     * 构造一个新的 TempFileException，带有详细的消息文本和导致这个异常的原因。
     *
     * @param message 详细消息文本
     * @param cause   导致这个异常的原因
     */
    public TempFileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个新的 TempFileException，带有导致这个异常的原因，不带详细消息文本。
     *
     * @param cause 导致这个异常的原因
     */
    public TempFileException(Throwable cause) {
        super(cause);
    }

}
