package com.aopie.jpm.model;

import java.util.List;

/**
 * 空间映射使用的基础工程类
 * <p>
 * 存储所有位置信息和文本信息
 * </p>
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/17
 */
public class PerspectiveContent {

    /**
     * OCR文字位置信息
     */
    public List<ParseUnit> parseUnitList;

    /**
     * 构造函数
     *
     * @param parseUnitList OCR文字位置信息
     */
    public PerspectiveContent(List<ParseUnit> parseUnitList) {
        this.parseUnitList = parseUnitList;
    }
}
