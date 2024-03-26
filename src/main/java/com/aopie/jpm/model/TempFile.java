package com.aopie.jpm.model;

import com.aopie.jpm.exceptions.TempFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

/**
 * 提供自管理的临时文件处理机制，专用于在与外部存储服务（如OSS）交互时创建和管理临时文件。
 * 该类的实例在垃圾收集时自动删除关联的文件，从而避免长期占用存储空间。
 * 主要用于生成、读取、写入和传输临时文件，支持自动资源管理。
 * <p>
 * 注意：
 * - 严禁用于非临时文件的场景。
 * - 设计为仅支持本地系统使用，不适用于跨网络服务的数据传输，如不应通过Feign接口共享或传递。
 * - 继承Closeable接口，推荐使用try-with-resources语句确保文件使用后被及时删除。
 * - 通过重写finalize方法加强了垃圾回收时的文件删除操作。
 * <p>
 * 示例和注意事项详见各方法注释。
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/25
 */
@Slf4j
public class TempFile extends File implements AutoCloseable {

    /**
     * 标记文件是否已关闭。
     */
    protected boolean closed = false;

    /**
     * 通过文件路径创建一个临时文件实例。
     */
    public TempFile(String pathname) {
        super(pathname);
    }

    /**
     * 通过父路径和子路径（文件名）创建一个临时文件实例。
     */
    public TempFile(File parent, String child) {
        super(parent, child);
    }

    /**
     * 生成并返回一个空的临时文件夹。
     */
    public static TempFile generateTempFolder() throws IOException {
        // 创建一个临时目录
        java.nio.file.Path tempDirectoryPath = Files.createTempDirectory("tempFolder_");

        // 将Path转换为File，然后包装为TempFile以使用TempFile的特性
        TempFile tempFile = new TempFile(tempDirectoryPath.toFile().getAbsolutePath());

        // 注册JVM退出时删除该目录，注意：这需要递归删除目录中的所有文件
        tempFile.deleteOnExit();

        return tempFile;
    }

    /**
     * 根据提供的文件名在临时目录下生成一个新的临时文件。
     */
    public static TempFile generateTempFile(String fileName) {
        try {
            File tempFile = File.createTempFile("temp", "_" + fileName);
            // 将创建的 File 对象转换为 TempFile，确保它拥有 TempFile 类的特性和行为
            TempFile customTempFile = new TempFile(tempFile.getAbsolutePath());
            // 注册一个钩子，在JVM停止时删除临时文件
            customTempFile.deleteOnExit();
            return customTempFile;
        } catch (IOException e) {
            log.error("无法创建临时文件: " + fileName, e);
            // 抛出一个运行时异常可以强制调用者处理这种情况
            throw new RuntimeException("无法创建临时文件: " + fileName, e);
        }
    }

    /**
     * 根据提供的文件名和输入流，在临时目录下生成一个新的临时文件，并写入输入流的数据。
     */
    public static TempFile generateTempFile(String fileName, InputStream inputStream) {
        try {
            // 前缀和后缀之间使用"_"来确保文件名的唯一性
            File tempFile = File.createTempFile("temp", "_" + fileName);

            // 使用try-with-resources确保文件输出流和输入流被正确关闭
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                // 1MB buffer
                byte[] buffer = new byte[1024 * 1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            return new TempFile(tempFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("创建临时文件时发生错误: {}", fileName, e);
            // 使用自定义异常来封装捕获的异常
            throw new TempFileException("无法创建临时文件: " + fileName, e);
        }
    }

    /**
     * 根据提供的文件名和字节数组，在临时目录下生成一个新的临时文件，并写入字节数组的数据。
     */
    public static TempFile generateTempFile(String fileName, byte[] data) {
        try {
            // 利用File.createTempFile方法创建临时文件，确保唯一性
            File tempFile = File.createTempFile("temp", "_" + fileName);

            // 使用try-with-resources确保文件输出流被正确关闭
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                outputStream.write(data);
            }

            // 将File转换为TempFile，以便使用TempFile的特性
            TempFile customTempFile = new TempFile(tempFile.getAbsolutePath());
            customTempFile.deleteOnExit();
            return customTempFile;
        } catch (IOException e) {
            log.error("无法创建或写入临时文件: {}", fileName, e);
            throw new TempFileException("无法创建或写入临时文件: " + fileName, e);
        }
    }

    /**
     * 生成一个临时文件路径。
     */
    private static String generateTempFilePath() {
        return System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID();
    }

    /**
     * 从给定的文件复制内容到此临时文件，覆盖原有内容。
     */
    public boolean copyFrom(File file) {
        if (file.exists() && !file.isDirectory()) {
            try (
                    FileInputStream inputStream = new FileInputStream(file);
                    FileOutputStream outputStream = new FileOutputStream(this)
            ) {
                byte[] car = new byte[1024 * 1024];
                int length;
                while ((length = inputStream.read(car)) > 0) {
                    outputStream.write(car, 0, length);
                }
            } catch (Exception e) {
                log.error("从文件复制内容到临时文件时发生错误: {}", file, e);
            }
        }
        return false;
    }

    /**
     * 从给定的输入流复制内容到此临时文件，覆盖原有内容。
     */
    public boolean copyFrom(InputStream fileInputStream) {
        if (fileInputStream != null) {
            try (
                    InputStream inputStream = fileInputStream;
                    FileOutputStream outputStream = new FileOutputStream(this)
            ) {
                byte[] car = new byte[1024 * 1024];
                int length;
                while ((length = inputStream.read(car)) > 0) {
                    outputStream.write(car, 0, length);
                }
            } catch (Exception e) {
                log.error("从输入流复制内容到临时文件时发生错误: {}", fileInputStream, e);
            }
        }
        return false;
    }

    /**
     * 将此临时文件的内容保存到指定的输出流。
     */
    public boolean saveTo(OutputStream outputStream) {
        if (exists() && outputStream != null) {
            try (
                    FileInputStream inputStream = new FileInputStream(this)
            ) {
                byte[] batch = new byte[1014 * 1024];
                int length;
                while ((length = inputStream.read(batch)) > 0) {
                    outputStream.write(batch, 0, length);
                }
                return true;
            } catch (Exception e) {
                log.error("保存文件到指定输出流时发生错误: {}", outputStream, e);
            }
        }
        return false;
    }

    /**
     * 将此临时文件的内容保存到指定路径的文件。
     */
    public boolean saveTo(String path) {
        if (exists() && StringUtils.isNotBlank(path)) {
            if (path.contains(File.separator)) {
                File file = new File(path);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
            }
            try (
                    FileInputStream inputStream = new FileInputStream(this);
                    FileOutputStream outputStream = new FileOutputStream(path)
            ) {
                byte[] batch = new byte[1014 * 1024];
                int length;
                while ((length = inputStream.read(batch)) > 0) {
                    outputStream.write(batch, 0, length);
                }
                return true;
            } catch (Exception e) {
                log.error("保存文件到指定路径时发生错误: {}", path, e);
            }
        }
        return false;
    }

    /**
     * 关闭此临时文件，并删除与之关联的物理文件。
     */
    @Override
    public void close() {
        if (!closed && exists()) {
            delete();
            closed = true;
        } else {
            log.error("尝试关闭一个已经关闭的文件");
        }
    }

    /**
     * 将文件内容读取为字节数组。
     */
    public byte[] readFileToBytes() {
        try (FileInputStream in = new FileInputStream(this)) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            log.error("读取文件到字节数组时发生错误: {}", this.getAbsolutePath(), e);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getAbsolutePath();
    }
}
