package com.aopie.jpm.util;

import java.awt.*;
import java.util.List;

/**
 * MathToolkit 是一个综合性的数学工具类，提供了一系列静态方法来执行常见的数学运算和高级数学操作。
 * 这个类旨在作为一个中央化的数学函数库，供任何需要进行数学计算的Java应用使用。
 * <p>
 * 本工具类覆盖的功能范围广泛，包括但不限于基础算术运算、几何运算、代数函数、统计分析以及更高级的数学公式处理。
 * 它旨在为开发人员提供一个方便、高效的方式来处理复杂的数学计算，无需从头编写这些常用的数学函数。
 * <p>
 * 主要功能包括：
 * 1. 基础算术运算：提供加、减、乘、除等基础运算。
 * 2. 几何运算：包括面积计算、体积计算以及其他与几何形状相关的计算。
 * 3. 代数函数：实现如解方程、多项式运算等高级代数功能。
 * 4. 统计分析：提供均值、中位数、标准差等统计量的计算。
 * 5. 高级数学公式处理：涉及更复杂的数学操作，如微积分运算、线性代数等。
 * <p>
 * 使用说明：
 * 所有方法均为静态方法，可以直接通过类名调用，无需实例化对象。
 * <p>
 * 注意事项：
 * - 该类仅用于教育和研究目的，未经全面测试，不保证完全无误。
 * - 部分高级数学功能可能依赖于外部数学库。
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/15
 */
public class MathToolkit {

    /**
     * 计算两个点组之间的空间相似性。
     *
     * @param group1 第一个点组，包含一系列的点。
     * @param group2 第二个点组，包含一系列与第一个点组对应位置的点。
     * @return 返回两个点组之间的空间相似性，相似度范围在0到1之间，其中1表示完全相似。
     */
    public static double calculateSpatialSimilarity(java.util.List<Point> group1, List<Point> group2) {
        // 记录两点之间距离的总差异
        double totalDistanceDifference = 0;
        // 记录两点之间角度的总差异
        double totalAngleDifference = 0;

        for (int i = 0; i < group1.size(); i++) {
            // 对每对对应点计算距离和角度的差异
            double distance1 = calculateDistance(group1.get(i), group2.get(i));
            double angle1 = calculateAngle(group1.get(i), group2.get(i));

            totalDistanceDifference += distance1;
            totalAngleDifference += angle1;
        }

        // 对距离和角度的差异进行标准化处理
        double normalizedDistanceDiff = totalDistanceDifference / group1.size();
        double normalizedAngleDiff = totalAngleDifference / group1.size();

        // 根据标准化后的差异计算相似度，差异越小相似度越高
        return 1 / (1 + normalizedDistanceDiff + normalizedAngleDiff);
    }

    /**
     * 计算两个匹配点组之间的相似性。
     *
     * @param group1 第一个点组，包含一系列Point对象。
     * @param group2 第二个点组，包含一系列Point对象。
     * @return 返回两个点组的相似度，相似度值越大表示两个点组越相似。
     */
    private static double calculateMatchedGroupSimilarity(List<Point> group1, List<Point> group2) {
        double distanceRatioSum = 0;
        double angleDifferenceSum = 0;

        // 计算组1的平均距离，用于后续距离比的计算
        double averageDistanceGroup1 = calculateAverageDistance(group1);

        // 遍历每对匹配点，计算它们之间的距离比和角度差异
        for (int i = 0; i < group1.size(); i++) {
            for (int j = i + 1; j < group1.size(); j++) {
                double distance1 = calculateDistance(group1.get(i), group1.get(j));
                double distance2 = calculateDistance(group2.get(i), group2.get(j));
                double angle1 = calculateAngle(group1.get(i), group1.get(j));
                double angle2 = calculateAngle(group2.get(i), group2.get(j));

                // 计算并累加每对点的标准化距离比
                double distanceRatio = (distance1 / averageDistanceGroup1) / (distance2 / averageDistanceGroup1);
                distanceRatioSum += distanceRatio;

                // 计算并累加每对点的角度差异
                double angleDifference = Math.abs(angle1 - angle2);
                angleDifferenceSum += angleDifference;
            }
        }

        // 计算标准化后的距离比和角度差异
        int totalComparisons = group1.size() * (group1.size() - 1) / 2;
        double normalizedDistanceRatio = distanceRatioSum / totalComparisons;
        double normalizedAngleDiff = angleDifferenceSum / totalComparisons;

        // 根据标准化后的距离比和角度差异计算相似度
        return 1 / (1 + Math.abs(1 - normalizedDistanceRatio) + normalizedAngleDiff);
    }

    /**
     * 计算给定点组中所有点之间平均距离的函数。
     *
     * @param group 包含多个点的列表。每个点都应该是类Point的实例。
     * @return 如果点组中至少包含两个点，则返回所有点之间平均距离；如果点组中点的数量小于等于1，则返回0。
     */
    private static double calculateAverageDistance(List<Point> group) {
        // 总距离
        double totalDistance = 0;
        // 计算距离的次数
        int count = 0;

        // 双重循环，计算每对点之间的距离并累加到totalDistance，同时计数
        for (int i = 0; i < group.size(); i++) {
            for (int j = i + 1; j < group.size(); j++) {
                totalDistance += calculateDistance(group.get(i), group.get(j));
                count++;
            }
        }

        // 根据计数，计算平均距离并返回
        return count > 0 ? totalDistance / count : 0;
    }

    /**
     * 计算两个点之间的距离
     *
     * @param p1 点P1的坐标，包含x和y坐标值
     * @param p2 点P2的坐标，包含x和y坐标值
     * @return 两点之间的距离
     */
    private static double calculateDistance(Point p1, Point p2) {
        // 计算两点之间的直线距离
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }


    /**
     * 计算两个点之间的角度
     * <p>
     * 该方法通过计算两个点构成的向量的弧度值，来确定这两个点之间的角度。
     * 这个角度是从第一个点指向第二个点的方向逆时针旋转到水平正向方向之间形成的夹角。
     *
     * @param p1 第一个点，作为角度计算的起点
     * @param p2 第二个点，作为角度计算的终点
     * @return 两点之间的角度（弧度），返回的弧度值范围在负无穷到正无穷之间
     */
    private static double calculateAngle(Point p1, Point p2) {
        // 使用反正切函数计算两点之间的角度
        return Math.atan2(p2.y - p1.y, p2.x - p1.x);
    }



}
