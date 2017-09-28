package com.bj.eduteacher.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 保留小数点的位数
 * 
 * @author Administrator
 * 
 */
public class DecimalFormatUtil {

	private DecimalFormatUtil() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 保留两位小数
	 * 
	 * @param value
	 */
	public static double formatByDecimal(double value) {

		DecimalFormat format = new DecimalFormat("#.00");
		String result = format.format(value);

		return Double.valueOf(result);
	}

	/**
	 * 基于String类型的保留两位小数
	 * 
	 * @param value
	 * @return
	 */
	public static double formatByString(double value) {

		String result = String.format("%.2f", value);

		return Double.valueOf(result);
	}

	/**
	 * 保留任意位小数
	 * 
	 * @param value
	 * @param number
	 * @return
	 */
	public static double formatByNumberFormat(double value, int number) {

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(number);
		String result = nf.format(value);

		return Double.valueOf(result);
	}

	/**
	 * 保留任意位小数
	 * 
	 * @param value
	 * @param number
	 * @return
	 */
	public static double formatByBigDecimal(double value, int number) {

		BigDecimal bg = new BigDecimal(value);
		double result = bg.setScale(number, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return result;
	}

	public static final int MATH_DOWN_FLOOR = 0;
	public static final int MATH_UP_CEIL = 1;

	/**
	 * 根据传入类型选择向上取整还是向下取整
	 * 
	 * @param value
	 * @param type
	 * @return
	 * 
	 * @如：Math.floor(3.2)返回3 Math.floor(3.9)返回3 Math.floor(3.0)返回3
	 * @如：Math.ceil(3.2)返回4 Math.ceil(3.9)返回4 Math.ceil(3.0)返回3
	 */
	public static double formatToInt(double value, int type) {
		if (type == MATH_UP_CEIL) {
			return Math.ceil(value); // 否则向上取整
		} else {
			return Math.floor(value); // 默认向下取整
		}
	}
}
