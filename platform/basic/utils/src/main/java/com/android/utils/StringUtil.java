package com.android.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import com.android.utils.font.VerticalCenterSpan;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by jiangshide on 2019-08-05.
 * email:18311271399@163.com
 */
public final class StringUtil {

  /**
   *
   */
  public static boolean isEmail(String email) {
    if (TextUtils.isEmpty(email)) {
      return false;
    }
    return Pattern.compile(
        "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")
        .matcher(email)
        .matches();
  }

  public static boolean isPsw(String psw) {
    //return Pattern.compile("/?=.*a-z(?=.\\d)[\\s\\S]{8,}$/").matcher(psw).matches();
    if (TextUtils.isEmpty(psw)) return false;
    if (psw.length() < 8) return false;
    if (!containUpperCase(psw)) return false;
    return true;
  }

  public static boolean containUpperCase(String str) {
    char ch;
    boolean capitalFlag = false;
    boolean lowerCaseFlag = false;
    boolean numberFlag = false;
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if (Character.isDigit(ch)) {
        numberFlag = true;
      } else if (Character.isUpperCase(ch)) {
        capitalFlag = true;
      } else if (Character.isLowerCase(ch)) {
        lowerCaseFlag = true;
      }
      if (numberFlag && capitalFlag && lowerCaseFlag) {
        return true;
      }
    }
    return false;
  }

  /**
   * 转换小写
   */
  public static String toLowerCase(String var0) {
    return var0.toLowerCase(Locale.US);
  }

  /**
   * 转换为大写
   */
  public static String toUpperCase(String var0) {
    return var0.toUpperCase(Locale.US);
  }

  /**
   * 查找，不区分大小写,没有找到返回-1
   */
  public static int indexOfIgnoreCase(String var0, String var1) {
    return indexOfIgnoreCase(var0, var1, 0);
  }

  /**
   * 从指定位置开始查找，不区分大小写--返回-1
   */
  public static int indexOfIgnoreCase(String var0, String var1, int var2) {
    Matcher var3 = Pattern.compile(Pattern.quote(var1), 2).matcher(var0);
    return var3.find(var2) ? var3.start() : -1;
  }

  /**
   * 获取随机昵称
   *
   * @return 随机昵称
   */
  public static String getRandomNick() {
    Random random = new Random();
    int len = random.nextInt(12) + 1;
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < len; i++) {
      builder.append(getRandomSingleCharacter());
    }
    return builder.toString();
  }

  /**
   * 获取随机单个汉字
   *
   * @return 随机单个汉字
   */
  public static String getRandomSingleCharacter() {
    String str = "";
    int heightPos;
    int lowPos;
    Random rd = new Random();
    heightPos = 176 + Math.abs(rd.nextInt(39));
    lowPos = 161 + Math.abs(rd.nextInt(93));
    byte[] bt = new byte[2];
    bt[0] = Integer.valueOf(heightPos).byteValue();
    bt[1] = Integer.valueOf(lowPos).byteValue();
    try {
      str = new String(bt, "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return str;
  }

  /**
   * 多字符组装
   */
  public static String getAppend(String... params) {
    StringBuffer stringBuffer = new StringBuffer();
    for (String param : params) {
      stringBuffer.append(param);
    }
    return stringBuffer.toString();
  }

  /**
   *
   */
  public static int strToInt(String str) {
    str = str.trim();
    StringBuffer stringBuffer = new StringBuffer(1024);
    if (str != null && !"".equals(str)) {
      for (int i = 0; i < str.length(); i++) {
        if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
          stringBuffer.append(str.charAt(i));
        }
      }
    }
    return Integer.parseInt(stringBuffer.toString());
  }

  public static void setDot(TextView textView) {
    setDot(textView, "·");
  }

  public static void setDot(TextView textView, String dot) {
    if (textView == null) return;
    String str = textView.getText().toString();
    if (TextUtils.isEmpty(str)) return;
    if (TextUtils.isEmpty(dot)) {
      dot = "·";
    }
    SpannableString spannableString = new SpannableString(str);
    if (!TextUtils.isEmpty(str)) {
      if (str.contains(dot)) {
        for (int i = 0; i < str.length(); i++) {
          if (String.valueOf(str.charAt(i)).equals(dot)) {
            int fontSizePx1 = (int) ScreenUtil.sp2px(AppUtil.getApplicationContext(), 14);
            spannableString.setSpan(new VerticalCenterSpan(fontSizePx1), i, i + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(
                    Color.parseColor("#8C8D93")), i, i + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          }
        }
      }
      textView.setText(spannableString);
    }
  }
}
