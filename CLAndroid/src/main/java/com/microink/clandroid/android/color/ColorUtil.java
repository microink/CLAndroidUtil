package com.microink.clandroid.android.color;

import android.graphics.Color;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 11:09 AM
 *
 * Color Util
 */
public class ColorUtil {

    /**
     * The current color is calculated from the fraction value
     * 根据fraction百分比值来计算当前的颜色
     *
     * @param fraction Fraction 百分比
     * @param startColor Start color 起始颜色
     * @param endColor End color 目标颜色
     * @return Result color
     */
    public static int getCurrentColor(int startColor, int endColor, float fraction) {
        if (0 == fraction) {
            return startColor;
        }
        if (1 == fraction) {
            return endColor;
        }
        int redCurrent;
        int blueCurrent;
        int greenCurrent;
        int alphaCurrent;

        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaStart = Color.alpha(startColor);

        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaEnd = Color.alpha(endColor);

        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaDifference = alphaEnd - alphaStart;

        redCurrent = (int) (redStart + fraction * redDifference);
        blueCurrent = (int) (blueStart + fraction * blueDifference);
        greenCurrent = (int) (greenStart + fraction * greenDifference);
        alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }
}
