package com.utils;

import cn.hutool.core.util.ObjectUtil;
import com.constants.Constants;
import com.enums.MeasureEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @ClassName AmountChangeUtils
 * @Description
 * @Author renBo
 * @Date 2022年8月25日13:30:30
 */
@Slf4j
public class AmountChangeUtils {
    /**
     * 一百
     */
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    /**
     * 精度为2位
     */
    private static final Integer TWO = 2;

    /**
     * 金额最大长度为32位
     */
    private static final Integer MAX_LENGTH = 32;

    /**
     * 小数点
     */
    private static final String decimal_point = ".";

    /**
     * 判断小数点后8位的数字的正则表达式
     */
    private static final Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,8})?$");

    /**
     * Functional description:  金额单位转换,分转换为元【截取至2位精度】
     *
     * @param * @param d1
     * @return boolean
     * @throws
     * @author renBo
     * @date 2022年8月25日13:30:00
     **/
    public static String amountChangeToYuan(String amount) {
        if (StringUtils.isEmpty(amount)) {
            amount = "0";
        }
        try {
            String result = "";
            BigDecimal bd = str2BigDecimal(amount);
            if (equalCompare(bd, BigDecimal.ZERO)) {
                result = "0";
            } else {
                bd = bd.divide(ONE_HUNDRED).setScale(TWO, RoundingMode.FLOOR);
                result = bd.toString();
            }
            return result;
        } catch (Exception e) {
            log.error("amountChangeToYuan error,param:【{}】,message:", amount, e);
            throw new RuntimeException("error number data");
        }

    }

    /**
     * Functional description:  元转换为分
     *
     * @param * @param d1
     * @return boolean
     * @throws
     * @author renBo
     * @date 2022年8月25日13:30:00
     **/
    public static String amountChangeToCent(String amount) {
        try {
            String result = "";
            BigDecimal bd = str2BigDecimal(amount);
            if (equalCompare(bd, BigDecimal.ZERO)) {
                result = "0";
            } else {
                bd = bd.multiply(ONE_HUNDRED);
                result = bd.stripTrailingZeros().toPlainString();
            }
            if (result.length() > MAX_LENGTH) {
                //需要转换的金额长度大于32位时，需要判断整数位是否大于32位，如果是小数，这截取前32位
                if (result.indexOf(decimal_point) != -1) {
                    String temp = result.substring(0, result.indexOf(decimal_point));
                    if (temp.length() > MAX_LENGTH) {
                        throw new Exception("The data length is greater than 32");
                    } else {
                        result = result.substring(0, MAX_LENGTH);
                    }
                } else {
                    throw new Exception("The data length is greater than 32");
                }
            }
            return result;
        } catch (Exception e) {
            log.error("amountChangeToYuan error,param:【{}】,message:", amount, e);
            throw new RuntimeException("error number Data");
        }
    }

    /**
     * Functional description:  字符串转BigDecimal
     *
     * @param * @param d1
     * @return boolean
     * @throws
     * @author renBo
     * @date 2022年8月25日13:30:00
     **/
    public static BigDecimal str2BigDecimal(String str) {
        if (StringUtils.isEmpty(str)) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(str);
        }
    }


    public static String add(String... str) {
        BigDecimal bd = BigDecimal.ZERO;
        for (String amount : str) {
            if (StringUtils.isEmpty(amount)) {
                amount = "0";
            }
            bd = bd.add(new BigDecimal(amount));
        }
        return bd.toPlainString();
    }

    public static String subtract(String s1, String s2) {
        return new BigDecimal(StringUtils.isEmpty(s1) ? "0" : s1)
                .subtract(new BigDecimal(StringUtils.isEmpty(s2) ? "0" : s2))
                .toPlainString();
    }

    /**
     * Functional description: 相等比较：d1是否等于d2；true：d1等于d2；false：d1不等于d2
     *
     * @param *  @param d1
     * @param d2
     * @return boolean
     * @throws
     * @author renBo
     * @date 2022年8月25日13:30:00
     **/
    public static boolean equalCompare(BigDecimal d1, BigDecimal d2) {
        if (d1 == null) {
            d1 = BigDecimal.ZERO;
        }
        if (d2 == null) {
            d2 = BigDecimal.ZERO;
        }
        return d1.compareTo(d2) == 0;
    }

    public static boolean equalCompare(String d1, String d2) {
        if (d1 == null) {
            d1 = "0";
        }
        if (d2 == null) {
            d2 = "0";
        }
        return new BigDecimal(d1).compareTo(new BigDecimal(d2)) == 0;
    }

    public static boolean compare(String d1, String d2) {
        if (d1 == null) {
            d1 = "0";
        }
        if (d2 == null) {
            d2 = "0";
        }
        return new BigDecimal(d1).compareTo(new BigDecimal(d2)) > 0;
    }

    /**
     * 判断金额是否为合法金额
     */
    public static boolean validMoneyParam(String amount) {
        if (StringUtils.isEmpty(amount)) {
            return true;
        }
        Matcher match = pattern.matcher(amount);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Functional description:  转换金额
     *
     * @author renBo
     * @date 2022年8月25日13:28:58
     **/
    public static Object convert(Object obj, MeasureEnum originMeasure, MeasureEnum targetMeasure) {
        if (ObjectUtil.isNull(obj)) {
            obj = "0";
        }
        if (originMeasure.equals(targetMeasure)) {
            return obj;
        }
        if (originMeasure.equals(MeasureEnum.CENT)) {
            //需要转换为元
            return amountChangeToYuan(obj.toString());
        }
        //需要转换为分
        return amountChangeToCent(obj.toString());
    }


    /**
     * @return
     * @Author renBo.ren renbo.ren@cyberklick.com.cn
     * @Description 乘法
     * @Date 14:01 2022/8/29
     * @Param
     **/
    public static String multiply(String a, String b) {
        return new BigDecimal(StringUtils.isEmpty(a) ? "0" : a)
                .multiply(new BigDecimal(StringUtils.isEmpty(b) ? "0" : b))
                .toPlainString();
    }


    public static String multiplyFLOOR(String a, String b) {
        return (new BigDecimal(StringUtils.isEmpty(a) ? "0" : a)
                .multiply(new BigDecimal(StringUtils.isEmpty(b) ? "0" : b)))
                .setScale(0, BigDecimal.ROUND_DOWN).toString(); // 向下取整
    }

    /**
     * @return
     * @Author renBo.ren renbo.ren@cyberklick.com.cn
     * @Description 除法
     * @Date 14:01 2022/8/29
     * @Param
     **/
    public static String divide_round_ceiling(String a, String b) {
        if (StringUtils.isEmpty(b) || equalCompare(Constants.ZERO_STR, b)) {
            return "0";
        }
        return new BigDecimal(StringUtils.isEmpty(a) ? "0" : a)
                .divide(new BigDecimal(b), BigDecimal.ROUND_CEILING)
                .toPlainString();
    }

    public static String divide_round_floor(String a, String b) {
        if (StringUtils.isEmpty(b) || equalCompare(Constants.ZERO_STR, b)) {
            return "0";
        }
        return new BigDecimal(StringUtils.isEmpty(a) ? "0" : a)
                .divide(new BigDecimal(b), BigDecimal.ROUND_FLOOR)
                .toPlainString();
    }

    public static String divide(String a, String b) {
        if (StringUtils.isEmpty(b) || equalCompare(Constants.ZERO_STR, b)) {
            return "0";
        }
        return new BigDecimal(StringUtils.isEmpty(a) ? "0" : a)
                .divide(new BigDecimal(b))
                .toPlainString();
    }


    public static String percentageDivide(String a, String b) {
        //除法结果保留4位小数，
        double per = new BigDecimal(divide(a, b)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        //BigDecimal 格式化工具    保留两位小数
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        //格式化为百分比字符串（自带百分号）
        return percent.format(per);
    }

    public static String percentageDivide(Integer a, Integer b) {
        if (0 == b) {
            return "-";
        }
        //除法结果保留4位小数，
        double per = new BigDecimal((float) a / b).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        //BigDecimal 格式化工具    保留两位小数
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        //格式化为百分比字符串（自带百分号）
        return percent.format(per);
    }


}