package com.aopie.jpm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ParseUnit类是用于存储从OCR识别过程中得到的文本信息及其位置信息的数据结构。
 * 它封装了整个识别文本的信息，文本在文档中的位置，以及每个单字的文本和详细的位置信息。
 * 通过这个类，可以方便地访问和处理OCR处理后的详细数据，包括但不限于文本内容和其在原图像或文档中的具体位置。
 * 该类使用Lombok库简化了代码，自动为类的字段生成getter、setter方法以及构造器等。
 * <p>
 * 主要应用场景包括文档解析、图像处理和信息提取等，特别适合需要精确位置信息的OCR应用。
 * </p>
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/14
 */
@Data
public class ParseUnit {

    /**
     * 文本信息
     */
    private String text;

    /**
     * 文本位置信息
     */
    private Quadrilateral textPosition;

    /**
     * 文本单字的文本及位置信息列表
     */
    private List<CharInfo> charInfoList;

    /**
     * Quadrilateral类用于描述文本在文档中的四点坐标位置，代表一个四边形区域。
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Quadrilateral {
        /**
         * 表示四角形的左上角
         */
        private Point topLeft;

        /**
         * 表示四角形的右上角
         */
        private Point topRight;

        /**
         * 表示四角形的右下角
         */
        private Point bottomRight;

        /**
         * 表示四角形的左下角
         */
        private Point bottomLeft;
    }

    /**
     * Point类用于描述二维平面上的一个点。
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Point {

        /**
         * x坐标
         */
        private float x;

        /**
         * y坐标
         */
        private float y;
    }

    /**
     * CharInfo类用于存储单个字符的文本信息和位置信息。
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CharInfo {

        /**
         * 单个字符的文本信息
         */
        private String character;

        /**
         * 单个字符的位置信息
         */
        private Quadrilateral position;
    }
}
