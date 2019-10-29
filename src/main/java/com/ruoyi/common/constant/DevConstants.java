package com.ruoyi.common.constant;

/**
 * 硬件相关常量
 */
public class DevConstants {
    /**
     * 验证通过
     */
    public static final int DEV_VALIDATE_TRUE = 0;
    /**
     * 硬件不存在
     */
    public static final int DEV_NOT_EXSIT=1;
    /**
     * 硬件正在使用
     */
    public static final int DEV_BEING_USE = 2;

    /**
     * 硬件是否被配置sign标记
     * 0、未配置，1、被配置
     */
    public static final int DEV_SIGN_NOT_USE = 0;
    public static final int DEV_SIGN_USED = 1;

    /**
     * 硬件配置对象标记车间或者流水线
     * 0、流水线，1、车间
     */
    public static final int DEV_TYPE_LINE = 0;
    public static final int DEV_TYPE_HOUSE = 1;

    /**
     * 硬件禁用启用状态
     * 0、禁用，1、启用
     */
    public static final int DEV_STATUS_NO = 0;
    public static final int DEV_STATUS_YES = 1;

    /**
     * 硬件类型 </br>
     *  1、看板硬件KB开头  2、计数器硬件JS开头
     */
    public static final Integer DEV_MODEL_KB = 1;
    public static final Integer DEV_MODEL_JS = 2;

    /**
     * 硬件计数器配置产线确认标记常量 </br>
     *  1、确认更新配置  0、未确认
     */
    public static final Integer DEV_CONFIRMTAG_YES = 1;
    public static final Integer DEV_CONFIRMTAG_NO = 0;
}
