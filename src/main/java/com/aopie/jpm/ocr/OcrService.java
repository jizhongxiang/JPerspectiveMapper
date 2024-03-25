package com.aopie.jpm.ocr;

import com.aopie.jpm.model.ParseUnit;

import java.io.InputStream;
import java.util.List;

/**
 * OcrService接口提供了一个方法来从图像中识别文字并解析为文本单元。
 * 这个接口设计用于处理输入流中的图像数据，通过光学字符识别技术（OCR）识别出图像中的文字，
 * 并将这些文字封装成ParseUnit列表返回。每个ParseUnit代表图像中的一个文本块或识别到的文字片段，
 * 包含了识别到的文字及其在原图中的位置信息等元数据。
 *
 * <p>这个接口可以被实现用于不同的OCR技术和库，提供一个统一的方法来访问这些服务。
 * 实现类应当能够处理各种格式的图像数据，解析并返回高质量的识别结果。</p>
 *
 * <p>使用此接口时，你需要提供一个包含图像数据的输入流，然后调用ocr方法进行处理。</p>
 *
 * <p><b>示例用法：</b></p>
 * <pre>
 * OcrService ocrService = new SomeOcrServiceImpl();
 * try (InputStream imageStream = new FileInputStream("path/to/image.jpg")) {
 *     List<ParseUnit> parseUnits = ocrService.ocr(imageStream);
 *     parseUnits.forEach(unit -> System.out.println(unit.getText()));
 * } catch (IOException e) {
 *     e.printStackTrace();
 * }
 * </pre>
 *
 * @author JohnsonGee
 * @version 1.0
 * @see ParseUnit 用于封装OCR识别结果的类。
 * @since 2024/3/25
 */
public interface OcrService {

    /**
     * 从给定的输入流中识别图像文字。
     *
     * @param inputStream 包含图像数据的输入流，该图像是OCR识别过程的目标。
     * @return 一个ParseUnit列表，每个单位包含识别到的一个或多个文字的信息。
     * 列表中的每个ParseUnit代表图像中的一个独立文本块或文字片段。
     */
    List<ParseUnit> ocr(InputStream inputStream);

}
