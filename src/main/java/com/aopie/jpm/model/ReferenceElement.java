package com.aopie.jpm.model;

import lombok.Data;

/**
 * ReferenceElement 类用于在进行空间透视变换时提供参考区域定位。
 * 这个类能够在模板图片上确定参考位置，为待变换的图片提供位置定位参考点。
 * 它主要用于图像处理领域中，当需要进行准确的空间变换以匹配或对齐图像时非常有用。
 * <p>
 * 使用这个类时，首先需要创建一个ReferenceElement实例，并传入模板图片的参考位置信息。
 * 然后，可以利用该实例对目标图片进行位置定位和空间透视变换操作。
 *
 * @author JohnsonGee
 * @version 1.0
 * @since 2024/3/25
 */
@Data
public class ReferenceElement extends ParseUnit {

    /**
     * 参考区位置信息
     */
    private Quadrilateral referencePosition;

}
