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
     * 校验相关文本信息是否为空或无内容
     *
     * @return boolean 如果文本信息、文本位置信息或文本单字的文本及位置信息列表为空或无内容，则返回true；否则返回false。
     */
    public boolean isEmpty() {
        // 检查主文本信息是否为空或空字符串
        if (isNullOrEmpty(text)) {
            return true;
        }

        // 检查文本位置信息是否为空或空集合
        if (textPosition == null || textPosition.isEmpty()) {
            return true;
        }

        // 检查文本单字的文本及位置信息列表是否为空
        if (isCharInfoListEmpty()) {
            return true;
        }

        // 如果以上检查均未通过，则返回false，表示信息不为空
        return false;
    }


    /**
     * 判断字符串是否为空或null。
     *
     * @param str 需要进行判断的字符串。
     * @return 返回true如果字符串为null或空，否则返回false。
     */
    private boolean isNullOrEmpty(String str) {
        // 判断字符串是否为null或空
        return str == null || str.isEmpty();
    }


    /**
     * 检查字符信息列表是否为空。
     * <p>
     * 该方法首先检查charInfoList是否为null或者是否为空集合。
     * 如果是，那么直接返回true，表示列表为空。
     * 如果不是，则通过流操作对列表中的每个元素进行检查，确保每个元素要么为null，要么自身为空。
     * 如果所有元素都满足这个条件，则返回true，否则返回false。
     *
     * @return boolean 如果字符信息列表为空或所有元素均为空，则返回true；否则返回false。
     */
    private boolean isCharInfoListEmpty() {
        if (charInfoList == null || charInfoList.isEmpty()) {
            return true;
        }

        // 使用流操作检查列表中的每个元素是否为空
        return charInfoList.stream().allMatch(charInfo -> charInfo == null || charInfo.isEmpty());
    }


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

        /**
         * 校验四点坐标对象是否为空的方法
         */
        public boolean isEmpty() {
            return topLeft == null || topRight == null || bottomRight == null || bottomLeft == null;
        }
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

        /**
         * 校验单个字符的文本信息、单个字符的位置信息是否为空和空内容的方法
         */
        public boolean isEmpty() {
            return character == null || character.isEmpty() || position == null || position.isEmpty();
        }
    }
}
