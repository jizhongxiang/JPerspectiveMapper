package com.aopie.jpm.model;

import com.aopie.jpm.exceptions.EmptyTextException;
import com.aopie.jpm.exceptions.InvalidTextPositionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
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
     * 根据对象内的文本和文本位置，通过计算生成单字符信息列表。
     * <p>
     * 警告：此方法仅在没有高精度单字符识别的OCR引擎时使用。
     * <p>
     * 此方法首先验证文本和文本位置的有效性，然后根据文本的排列方向（横版或竖版）和字符在文本中的位置，
     * 计算每个字符的宽度、高度和中心点位置，并考虑字符可能的旋转角度。
     * 最后，生成并保存每个字符的详细信息，包括字符的边界框和中心点坐标。
     * <p>
     * 抛出：
     * EmptyTextException - 如果文本为空。
     * InvalidTextPositionException - 如果文本位置无效或为空。
     */
    public static void generateCharInfoList(ParseUnit parseUnit) {

        if (parseUnit == null) {
            throw new NullPointerException("parseUnit is null");
        }

        String text = parseUnit.getText();
        Quadrilateral textPosition = parseUnit.getTextPosition();
        // 验证文本是否为空
        if (StringUtils.isBlank(text)) {
            throw new EmptyTextException();
        }

        // 验证文本位置是否有效
        if (textPosition == null || textPosition.isEmpty()) {
            throw new InvalidTextPositionException();
        }

        List<CharInfo> charInfoList = new ArrayList<>();

        // 将文本分割为单个字符
        String[] textCharacters = text.split("");

        // 计算文本的旋转角度
        double angle = Math.toDegrees(Math.atan2(textPosition.getTopRight().getY() - textPosition.getTopLeft().getY(), textPosition.getTopRight().getX() - textPosition.getTopLeft().getX()));
        BigDecimal angleDecimal = new BigDecimal(Double.toString(angle)).abs();
        if (angleDecimal.compareTo(new BigDecimal("180")) >= 0) {
            angle = 360 - angleDecimal.doubleValue();
        } else if (angleDecimal.compareTo(new BigDecimal("90")) >= 0) {
            angle = 180 - angleDecimal.doubleValue();
        }

        // 计算字符区域的宽度和高度
        float regionWidth = textPosition.getTopRight().getX() - textPosition.getTopLeft().getX();
        float regionHeight = textPosition.getBottomLeft().getY() - textPosition.getTopLeft().getY();

        double characterWidth;
        double characterHeight;

        // 判断是否为竖版排列
        boolean isVertical = regionHeight > regionWidth;

        // 判断是否为竖版排列
        if (isVertical) {
            // 竖版排列时，均匀分割字符高度
            characterWidth = regionWidth;
            characterHeight = (double) regionHeight / textCharacters.length;
        } else {
            // 横版排列时，按照原有逻辑计算宽度和高度
            characterWidth = (double) regionWidth / textCharacters.length;
            characterHeight = regionHeight;
        }

        // 初始化字符统计信息
        int chinese = 0;
        int downEnglish = 0;
        int upEnglish = 0;
        int chinesePunctuation = 0;
        int englishPunctuation = 0;
        int other = 0;
        // 分类统计字符类型
        for (int i = 0; i < textCharacters.length; i++) {
            String character = textCharacters[i];
            boolean isLast = (i == (textCharacters.length - 1));
            if (isChinese(character)) {
                chinese++;
            } else if (isUpEnglish(character)) {
                upEnglish++;
            } else if (isDownEnglish(character)) {
                downEnglish++;
            } else if (!isLast && isChinesePunctuation(character)) {
                chinesePunctuation++;
            } else if (isEnglishPunctuation(character)) {
                englishPunctuation++;
            } else {
                other++;
            }
        }

        // 设置不同字符类型的放大倍数
        double chineseMagnification = 2;
        double upEnglishMagnification = 1.3;
        double otherMagnification = 1;

        // 根据字符类型统计计算字符宽度
        double downEnglishWidth = (double) regionWidth
                / (chinese * chineseMagnification
                + upEnglish * upEnglishMagnification
                + downEnglish
                + chinesePunctuation * chineseMagnification
                + englishPunctuation
                + other * otherMagnification);

        double chineseWidth = chineseMagnification * downEnglishWidth;
        double chinesePunctuationWidth = chineseMagnification * downEnglishWidth;
        double upEnglishPunctuationWidth = upEnglishMagnification * downEnglishWidth;
        double otherPunctuationWidth = otherMagnification * downEnglishWidth;

        // 初始化计算字符位置所需的变量
        double totalWidth = textPosition.getTopLeft().getX();
        double totalHeight = textPosition.getTopLeft().getY();
        // 遍历字符，计算每个字符的位置和大小
        for (int i = 0; i < textCharacters.length; i++) {
            String character = textCharacters[i];
            boolean isLast = (i == (textCharacters.length - 1));

            // 根据文本方向计算字符宽度和高度
            if (isVertical) {
                characterHeight = (double) regionHeight / textCharacters.length;
            } else {
                if (isChinese(character)) {
                    characterWidth = chineseWidth;
                } else if (isUpEnglish(character)) {
                    characterWidth = upEnglishPunctuationWidth;
                } else if (isDownEnglish(character)) {
                    characterWidth = downEnglishWidth;
                } else if (!isLast && isChinesePunctuation(character)) {
                    characterWidth = chinesePunctuationWidth;
                } else if (isEnglishPunctuation(character)) {
                    characterWidth = downEnglishWidth;
                } else {
                    characterWidth = otherPunctuationWidth;
                }
            }

            // 计算字符中心点坐标，并考虑旋转角度的影响
            double centerX = totalWidth + characterWidth / 2;
            double centerY = totalHeight + characterHeight / 2;
            double[] rotated = rotatePoint(centerX, centerY, textPosition.getTopLeft().getX(), textPosition.getTopLeft().getY(), angle);
            centerX = rotated[0];
            centerY = rotated[1];

            // 更新字符位置信息
            if (isVertical) {
                totalHeight += characterHeight;
            } else {
                totalWidth += characterWidth;
            }

            // 创建并添加字符信息到列表
            double newX1 = centerX - characterWidth / 2;
            double newY1 = centerY - characterHeight / 2;
            double newX2 = centerX + characterWidth / 2;
            double newY2 = centerY + characterHeight / 2;
            CharInfo ocrCharacter = new CharInfo(character, (float) newX1, (float) newY1, (float) newX2, (float) newY2);
            charInfoList.add(ocrCharacter);
        }
        parseUnit.setCharInfoList(charInfoList);
    }

    /**
     * 判断给定的字符是否为中文。
     *
     * @param character 需要判断的字符，类型为String，长度限制为1。
     * @return 返回一个布尔值，如果字符是中文，则返回true；否则返回false。
     */
    private static boolean isChinese(String character) {
        // 使用正则表达式判断字符是否为中文
        return character.matches("[\\u4e00-\\u9fa5]+");
    }

    /**
     * 判断给定的字符是否为大写英文字母。
     *
     * @param character 需要判断的字符，类型为String，因为Java中字符型不能作为方法参数。
     * @return 返回一个布尔值，如果字符是大写英文字母，则返回true；否则返回false。
     */
    private static boolean isUpEnglish(String character) {
        // 使用正则表达式判断字符是否为大写英文字母
        return character.matches("[A-Z]+");
    }

    /**
     * 判断给定的字符是否为小写英文字母。
     *
     * @param character 需要判断的字符，类型为String，因为单个字符在Java中以字符串形式传递。
     * @return 返回一个布尔值，如果字符为小写英文字母，则返回true；否则返回false。
     */
    private static boolean isDownEnglish(String character) {
        // 使用正则表达式判断字符是否为连续的小写英文字母
        return character.matches("[a-z]+");
    }

    /**
     * 判断给定的字符是否为中文标点。
     *
     * @param character 需要判断的字符，类型为String，因为标点符号也可以看作是一个字符。
     * @return 返回一个布尔值，如果该字符是中文标点，则返回true；否则返回false。
     */
    private static boolean isChinesePunctuation(String character) {
        // 使用正则表达式判断字符是否为中文标点或其他指定的标点符号
        return character.matches("[\\u4e00-\\u9fa5；、，。：！？～【】《》…（）]+");
    }

    /**
     * 判断给定的字符是否为英文标点符号。
     *
     * @param character 需要判断的字符，类型为String，因为字符在Java中是String的一个字符元素。
     * @return 返回一个布尔值，如果该字符是英文标点符号，则返回true；否则返回false。
     */
    private static boolean isEnglishPunctuation(String character) {
        // 使用正则表达式判断字符是否为指定的英文标点符号
        return character.matches("[!?.‘’:“”@*<>;\"',-]");
    }

    /**
     * 旋转点坐标的辅助方法
     *
     * @param pointX       待旋转点的X坐标
     * @param pointY       待旋转点的Y坐标
     * @param centerX      旋转中心的X坐标
     * @param centerY      旋转中心的Y坐标
     * @param angleDegrees 旋转角度（以度为单位）
     * @return 旋转后点的新坐标，作为长度为2的double数组返回，数组第一个元素为新X坐标，第二个元素为新Y坐标
     */
    private static double[] rotatePoint(double pointX, double pointY, double centerX, double centerY, double angleDegrees) {
        // 将角度转换为弧度
        double radians = Math.toRadians(angleDegrees);
        // 计算余弦和正弦值
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        // 将点平移到旋转中心
        pointX -= centerX;
        pointY -= centerY;

        // 执行旋转
        double xNew = pointX * cos - pointY * sin;
        double yNew = pointX * sin + pointY * cos;

        // 将点平移回原始位置
        xNew += centerX;
        yNew += centerY;

        return new double[]{xNew, yNew};
    }

    /**
     * 根据对象内的文本和文本位置，通过计算生成单字符信息列表。
     * <p>
     * 警告：此方法仅在没有高精度单字符识别的OCR引擎时使用。
     * <p>
     * 此方法首先验证文本和文本位置的有效性，然后根据文本的排列方向（横版或竖版）和字符在文本中的位置，
     * 计算每个字符的宽度、高度和中心点位置，并考虑字符可能的旋转角度。
     * 最后，生成并保存每个字符的详细信息，包括字符的边界框和中心点坐标。
     * <p>
     * 抛出：
     * EmptyTextException - 如果文本为空。
     * InvalidTextPositionException - 如果文本位置无效或为空。
     */
    public void generateCharInfoList() {
        // 调用generateCharInfoList方法，传入当前对象以生成字符信息列表
        generateCharInfoList(this);
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

        public CharInfo(String character, float newX1, float newY1, float newX2, float newY2) {
            this.character = character;
            this.position = new Quadrilateral(new Point(newX1, newY1), new Point(newX2, newY1), new Point(newX2, newY2), new Point(newX1, newY2));
        }

        /**
         * 校验单个字符的文本信息、单个字符的位置信息是否为空和空内容的方法
         */
        public boolean isEmpty() {
            return character == null || character.isEmpty() || position == null || position.isEmpty();
        }
    }
}
