package com.vk.vsvans.BlogShop.util

import android.content.Context
import android.util.DisplayMetrics

object UtilsString {

    private val DECIMAL_SEPARATOR = '.'
    private val mNumberFilterRegex = "[^\\d\\-$DECIMAL_SEPARATOR]"
    private val FRACTION_LENGTH = 2
    private val GROUPING_SEPARATOR = ' '
    private val mDecimalSeparator = DECIMAL_SEPARATOR
    private val LEADING_ZERO_FILTER_REGEX = "^0+(?!$)"

    fun format_string(original: String): String {
        val parts = original.split( mDecimalSeparator)
        var number: String =
            parts[0] // since we split with limit -1 there will always be at least 1 part
                .replace(mNumberFilterRegex.toRegex(), "")
                .replaceFirst(LEADING_ZERO_FILTER_REGEX.toRegex(), "")

        // only add grouping separators for non custom decimal separator
//        if (!hasCustomDecimalSeparator) {
//            // add grouping separators, need to reverse back and forth since Java regex does not support
//            // right to left matching
//            number = StringUtils.reverse(
//                StringUtils.reverse(number).replace(
//                    "(.{3})".toRegex(),
//                    "$1$GROUPING_SEPARATOR"
//                )
//            )
//            // remove leading grouping separator if any
//            number = StringUtils.removeStart(number, GROUPING_SEPARATOR.toString())
//            //number = StringUtils.remove(number, String.valueOf(GROUPING_SEPARATOR));
//        }

        // add fraction part if any
        if (parts.size > 1) {
            if (parts[1].length > FRACTION_LENGTH) {
                number += mDecimalSeparator.toString() + parts[1].substring(0, FRACTION_LENGTH - 1)
            } else {
                number += mDecimalSeparator + parts[1]
            }
        }
        return number
    }
    fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}