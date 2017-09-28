package com.bj.eduteacher.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorUtils {

	private static final String TAG = "SensorUtils";

	private static SensorUtils instance;
	private Context mContext;
	private SensorManager sm;
	// 加速度传感器
	private Sensor acc_sensor;
	// 地磁传感器
	private Sensor mag_sensor;

	// 加速度传感器数据
	private float accValues[] = new float[3];
	// 地磁传感器数据
	private float magValues[] = new float[3];
	// 旋转矩阵，用来保存磁场和加速度的数据
	private float r[] = new float[9];
	// 模拟方向传感器的数据（原始数据为弧度）
	private float values[] = new float[3];

	private SensorUtils(Context context) {
		mContext = context;
		sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
	}

	public static SensorUtils getInstance(Context context) {

		if (null == instance) {
			instance = new SensorUtils(context);
		}
		return instance;
	}

	/**
	 * 注册方向监听器
	 */
	public void registerOrienListener() {
		acc_sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mag_sensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sm.registerListener(listener, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(listener, mag_sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * 取消注册
	 */
	public void unRegisterOrienListener() {
		sm.unregisterListener(listener, acc_sensor);
		sm.unregisterListener(listener, mag_sensor);
		acc_sensor = null;
		mag_sensor = null;
	}

	/**
	 * 获取方向值
	 * 
	 * @return
	 */
	public float[] getOrientationDegree() {

		float result[] = new float[3];
		// 转换成角度输出
		for (int i = 0; i < 3; i++) {
			result[i] = (float) Math.toDegrees(values[i]);
		}

		LL.i(TAG, "方向角：" + result[0] + "  倾斜角：" + result[1] + "  旋转角：" + result[2]);
		return result;

		// return values;
	}

	/**
	 * 监听器
	 */
	private SensorEventListener listener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accValues = event.values.clone();// 这里是对象，需要克隆一份，否则共用一份数据
			} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magValues = event.values.clone();// 这里是对象，需要克隆一份，否则共用一份数据
			}

			/**
			 * public static boolean getRotationMatrix (float[] R, float[] I,
			 * float[] gravity, float[] geomagnetic) 填充旋转数组r r：要填充的旋转数组
			 * I:将磁场数据转换进实际的重力坐标中 一般默认情况下可以设置为null gravity:加速度传感器数据
			 * geomagnetic：地磁传感器数据
			 */
			SensorManager.getRotationMatrix(r, null, accValues, magValues);

			/**
			 * public static float[] getOrientation (float[] R, float[] values)
			 * R：旋转数组 values ：模拟方向传感器的数据
			 */
			SensorManager.getOrientation(r, values);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};
}
