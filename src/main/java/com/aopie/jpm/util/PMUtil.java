package com.aopie.jpm.util;

import com.aopie.jpm.model.ParseUnit;
import com.aopie.jpm.model.PerspectiveContent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PMUtil (Perspective Mapping Utility) 是一个专门设计用于处理空间透视坐标转换和空间映射的工具类。
 * 它提供了一系列静态方法，旨在支持各种空间数据处理需求，包括但不限于透视变换、坐标映射、空间数据分析等功能。
 * 此工具类适用于需要高精度空间数据处理的应用场景，如图像处理等。
 * <p>
 * 主要功能：
 * - 空间透视坐标转换：根据给定的透视参数，将空间中的坐标点转换为透视视图中的坐标点。
 * - 空间映射：实现空间数据点在不同坐标系之间的映射和转换。
 * - 坐标系统变换：支持在各种坐标系统（如笛卡尔坐标系、极坐标系等）之间进行转换。
 * - 数据分析工具：提供基础的空间数据分析功能，如距离计算、面积估算等。
 * <p>
 * 请注意，当前工具类处于开发阶段，部分功能可能还未实现或正在调试中。我们欢迎并鼓励社区贡献代码、提出改进建议，
 * 并报告任何遇到的问题。我们的目标是提供一个强大、灵活且易于使用的空间数据处理工具库。
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/14
 */
public class PMUtil {

    /**
     * 根据OCR的内容初始化一个工程对象
     *
     * @param parseUnitList OCR解析出的内容单元列表，不能为空或包含空地解析单元
     * @return 初始化后的PerspectiveContent对象
     * @throws IllegalArgumentException 如果传入的解析单元列表为空或包含空地解析单元时抛出
     */
    public static PerspectiveContent initializeByOcr(List<ParseUnit> parseUnitList) {
        // 检查传入的解析单元列表是否为空或空列表
        if (parseUnitList == null || parseUnitList.isEmpty()) {
            throw new IllegalArgumentException("parseUnitList is null or empty");
        }
        // 遍历列表，检查每个解析单元是否为空
        parseUnitList.forEach(parseUnit -> {
            if (parseUnit.isEmpty()) {
                throw new IllegalArgumentException("parseUnit is null or empty");
            }
        });
        // 创建并返回PerspectiveContent对象
        return new PerspectiveContent(parseUnitList);
    }

    /**
     * 处理大字符串中与正则表达式匹配的所有子串。
     *
     * @param largeString 需要处理的大字符串。
     * @param regex       用于匹配的正则表达式。
     * @return 匹配到的所有子串列表。
     */
    private static List<String> processLargeString(String largeString, String regex) {
        List<String> matchList = new ArrayList<>();
        // 编译正则表达式，优化匹配性能
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(largeString);

        while (matcher.find()) {
            // 将每次匹配到的子串添加到列表中
            matchList.add(matcher.group());
        }
        return matchList;
    }

}
