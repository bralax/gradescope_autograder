//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package brandon;

import java.util.Random;

public final class Math {
   public static final double E = 2.718281828459045D;
   public static final double PI = 3.141592653589793D;
   private static long negativeZeroFloatBits = (long)Float.floatToRawIntBits(-0.0F);
   private static long negativeZeroDoubleBits = Double.doubleToRawLongBits(-0.0D);
   static double twoToTheDoubleScaleUp = powerOfTwoD(512);
   static double twoToTheDoubleScaleDown = powerOfTwoD(-512);

   private Math() {
   }

   public static double sin(double var0) {
      return StrictMath.sin(var0);
   }

   public static double cos(double var0) {
      return StrictMath.cos(var0);
   }

   public static double tan(double var0) {
      return StrictMath.tan(var0);
   }

   public static double asin(double var0) {
      return StrictMath.asin(var0);
   }

   public static double acos(double var0) {
      return StrictMath.acos(var0);
   }

   public static double atan(double var0) {
      return StrictMath.atan(var0);
   }

   public static double toRadians(double var0) {
      return var0 / 180.0D * 3.141592653589793D;
   }

   public static double toDegrees(double var0) {
      return var0 * 180.0D / 3.141592653589793D;
   }

   public static double exp(double var0) {
      return StrictMath.exp(var0);
   }

   public static double log(double var0) {
      return StrictMath.log(var0);
   }

   public static double log10(double var0) {
      return StrictMath.log10(var0);
   }

   public static double sqrt(double var0) {
      return StrictMath.sqrt(var0);
   }

   public static double cbrt(double var0) {
      return StrictMath.cbrt(var0);
   }

   public static double IEEEremainder(double var0, double var2) {
      return StrictMath.IEEEremainder(var0, var2);
   }

   public static double ceil(double var0) {
      return StrictMath.ceil(var0);
   }

   public static double floor(double var0) {
      return StrictMath.floor(var0);
   }

   public static double rint(double var0) {
      return StrictMath.rint(var0);
   }

   public static double atan2(double var0, double var2) {
      return StrictMath.atan2(var0, var2);
   }

   public static double pow(double var0, double var2) {
      return StrictMath.pow(var0, var2);
   }

   public static int round(float var0) {
      int var1 = Float.floatToRawIntBits(var0);
      int var2 = (var1 & 2139095040) >> 23;
      int var3 = 149 - var2;
      if ((var3 & -32) == 0) {
         int var4 = var1 & 8388607 | 8388608;
         if (var1 < 0) {
            var4 = -var4;
         }

         return (var4 >> var3) + 1 >> 1;
      } else {
         return (int)var0;
      }
   }

   public static long round(double var0) {
      long var2 = Double.doubleToRawLongBits(var0);
      long var4 = (var2 & 9218868437227405312L) >> 52;
      long var6 = 1074L - var4;
      if ((var6 & -64L) == 0L) {
         long var8 = var2 & 4503599627370495L | 4503599627370496L;
         if (var2 < 0L) {
            var8 = -var8;
         }

         return (var8 >> (int)var6) + 1L >> 1;
      } else {
         return (long)var0;
      }
   }

   public static double random() {
      double d = Math.RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble();
      //System.err.println(d);
      return d;
   }
   
   public static void resetRandom() {
      Math.RandomNumberGeneratorHolder.randomNumberGenerator = new Random(1);
   }

   public static int addExact(int var0, int var1) {
      int var2 = var0 + var1;
      if (((var0 ^ var2) & (var1 ^ var2)) < 0) {
         throw new ArithmeticException("integer overflow");
      } else {
         return var2;
      }
   }

   public static long addExact(long var0, long var2) {
      long var4 = var0 + var2;
      if (((var0 ^ var4) & (var2 ^ var4)) < 0L) {
         throw new ArithmeticException("long overflow");
      } else {
         return var4;
      }
   }

   public static int subtractExact(int var0, int var1) {
      int var2 = var0 - var1;
      if (((var0 ^ var1) & (var0 ^ var2)) < 0) {
         throw new ArithmeticException("integer overflow");
      } else {
         return var2;
      }
   }

   public static long subtractExact(long var0, long var2) {
      long var4 = var0 - var2;
      if (((var0 ^ var2) & (var0 ^ var4)) < 0L) {
         throw new ArithmeticException("long overflow");
      } else {
         return var4;
      }
   }

   public static int multiplyExact(int var0, int var1) {
      long var2 = (long)var0 * (long)var1;
      if ((long)((int)var2) != var2) {
         throw new ArithmeticException("integer overflow");
      } else {
         return (int)var2;
      }
   }

   public static long multiplyExact(long var0, long var2) {
      long var4 = var0 * var2;
      long var6 = abs(var0);
      long var8 = abs(var2);
      if ((var6 | var8) >>> 31 == 0L || (var2 == 0L || var4 / var2 == var0) && (var0 != -9223372036854775808L || var2 != -1L)) {
         return var4;
      } else {
         throw new ArithmeticException("long overflow");
      }
   }

   public static int incrementExact(int var0) {
      if (var0 == 2147483647) {
         throw new ArithmeticException("integer overflow");
      } else {
         return var0 + 1;
      }
   }

   public static long incrementExact(long var0) {
      if (var0 == 9223372036854775807L) {
         throw new ArithmeticException("long overflow");
      } else {
         return var0 + 1L;
      }
   }

   public static int decrementExact(int var0) {
      if (var0 == -2147483648) {
         throw new ArithmeticException("integer overflow");
      } else {
         return var0 - 1;
      }
   }

   public static long decrementExact(long var0) {
      if (var0 == -9223372036854775808L) {
         throw new ArithmeticException("long overflow");
      } else {
         return var0 - 1L;
      }
   }

   public static int negateExact(int var0) {
      if (var0 == -2147483648) {
         throw new ArithmeticException("integer overflow");
      } else {
         return -var0;
      }
   }

   public static long negateExact(long var0) {
      if (var0 == -9223372036854775808L) {
         throw new ArithmeticException("long overflow");
      } else {
         return -var0;
      }
   }

   public static int toIntExact(long var0) {
      if ((long)((int)var0) != var0) {
         throw new ArithmeticException("integer overflow");
      } else {
         return (int)var0;
      }
   }

   public static int floorDiv(int var0, int var1) {
      int var2 = var0 / var1;
      if ((var0 ^ var1) < 0 && var2 * var1 != var0) {
         --var2;
      }

      return var2;
   }

   public static long floorDiv(long var0, long var2) {
      long var4 = var0 / var2;
      if ((var0 ^ var2) < 0L && var4 * var2 != var0) {
         --var4;
      }

      return var4;
   }

   public static int floorMod(int var0, int var1) {
      int var2 = var0 - floorDiv(var0, var1) * var1;
      return var2;
   }

   public static long floorMod(long var0, long var2) {
      return var0 - floorDiv(var0, var2) * var2;
   }

   public static int abs(int var0) {
      return var0 < 0 ? -var0 : var0;
   }

   public static long abs(long var0) {
      return var0 < 0L ? -var0 : var0;
   }

   public static float abs(float var0) {
      return var0 <= 0.0F ? 0.0F - var0 : var0;
   }

   public static double abs(double var0) {
      return var0 <= 0.0D ? 0.0D - var0 : var0;
   }

   public static int max(int var0, int var1) {
      return var0 >= var1 ? var0 : var1;
   }

   public static long max(long var0, long var2) {
      return var0 >= var2 ? var0 : var2;
   }

   public static float max(float var0, float var1) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0F && var1 == 0.0F && (long)Float.floatToRawIntBits(var0) == negativeZeroFloatBits) {
         return var1;
      } else {
         return var0 >= var1 ? var0 : var1;
      }
   }

   public static double max(double var0, double var2) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0D && var2 == 0.0D && Double.doubleToRawLongBits(var0) == negativeZeroDoubleBits) {
         return var2;
      } else {
         return var0 >= var2 ? var0 : var2;
      }
   }

   public static int min(int var0, int var1) {
      return var0 <= var1 ? var0 : var1;
   }

   public static long min(long var0, long var2) {
      return var0 <= var2 ? var0 : var2;
   }

   public static float min(float var0, float var1) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0F && var1 == 0.0F && (long)Float.floatToRawIntBits(var1) == negativeZeroFloatBits) {
         return var1;
      } else {
         return var0 <= var1 ? var0 : var1;
      }
   }

   public static double min(double var0, double var2) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0D && var2 == 0.0D && Double.doubleToRawLongBits(var2) == negativeZeroDoubleBits) {
         return var2;
      } else {
         return var0 <= var2 ? var0 : var2;
      }
   }

   public static double ulp(double var0) {
      int var2 = getExponent(var0);
      switch(var2) {
      case -1023:
         return 4.9E-324D;
      case 1024:
         return abs(var0);
      default:
         assert var2 <= 1023 && var2 >= -1022;

         var2 -= 52;
         return var2 >= -1022 ? powerOfTwoD(var2) : Double.longBitsToDouble(1L << var2 - -1074);
      }
   }

   public static float ulp(float var0) {
      int var1 = getExponent(var0);
      switch(var1) {
      case -127:
         return 1.4E-45F;
      case 128:
         return abs(var0);
      default:
         assert var1 <= 127 && var1 >= -126;

         var1 -= 23;
         return var1 >= -126 ? powerOfTwoF(var1) : Float.intBitsToFloat(1 << var1 - -149);
      }
   }

   public static double signum(double var0) {
      return var0 != 0.0D && !Double.isNaN(var0) ? copySign(1.0D, var0) : var0;
   }

   public static float signum(float var0) {
      return var0 != 0.0F && !Float.isNaN(var0) ? copySign(1.0F, var0) : var0;
   }

   public static double sinh(double var0) {
      return StrictMath.sinh(var0);
   }

   public static double cosh(double var0) {
      return StrictMath.cosh(var0);
   }

   public static double tanh(double var0) {
      return StrictMath.tanh(var0);
   }

   public static double hypot(double var0, double var2) {
      return StrictMath.hypot(var0, var2);
   }

   public static double expm1(double var0) {
      return StrictMath.expm1(var0);
   }

   public static double log1p(double var0) {
      return StrictMath.log1p(var0);
   }

   public static double copySign(double var0, double var2) {
      return Double.longBitsToDouble(Double.doubleToRawLongBits(var2) & -9223372036854775808L | Double.doubleToRawLongBits(var0) & 9223372036854775807L);
   }

   public static float copySign(float var0, float var1) {
      return Float.intBitsToFloat(Float.floatToRawIntBits(var1) & -2147483648 | Float.floatToRawIntBits(var0) & 2147483647);
   }

   public static int getExponent(float var0) {
      return ((Float.floatToRawIntBits(var0) & 2139095040) >> 23) - 127;
   }

   public static int getExponent(double var0) {
      return (int)(((Double.doubleToRawLongBits(var0) & 9218868437227405312L) >> 52) - 1023L);
   }

   public static double nextAfter(double var0, double var2) {
      if (!Double.isNaN(var0) && !Double.isNaN(var2)) {
         if (var0 == var2) {
            return var2;
         } else {
            long var4 = Double.doubleToRawLongBits(var0 + 0.0D);
            if (var2 > var0) {
               var4 += var4 >= 0L ? 1L : -1L;
            } else {
               assert var2 < var0;

               if (var4 > 0L) {
                  --var4;
               } else if (var4 < 0L) {
                  ++var4;
               } else {
                  var4 = -9223372036854775807L;
               }
            }

            return Double.longBitsToDouble(var4);
         }
      } else {
         return var0 + var2;
      }
   }

   public static float nextAfter(float var0, double var1) {
      if (!Float.isNaN(var0) && !Double.isNaN(var1)) {
         if ((double)var0 == var1) {
            return (float)var1;
         } else {
            int var3 = Float.floatToRawIntBits(var0 + 0.0F);
            if (var1 > (double)var0) {
               var3 += var3 >= 0 ? 1 : -1;
            } else {
               assert var1 < (double)var0;

               if (var3 > 0) {
                  --var3;
               } else if (var3 < 0) {
                  ++var3;
               } else {
                  var3 = -2147483647;
               }
            }

            return Float.intBitsToFloat(var3);
         }
      } else {
         return var0 + (float)var1;
      }
   }

   public static double nextUp(double var0) {
      if (!Double.isNaN(var0) && var0 != 1.0D / 0.0) {
         var0 += 0.0D;
         return Double.longBitsToDouble(Double.doubleToRawLongBits(var0) + (var0 >= 0.0D ? 1L : -1L));
      } else {
         return var0;
      }
   }

   public static float nextUp(float var0) {
      if (!Float.isNaN(var0) && var0 != 1.0F / 0.0) {
         var0 += 0.0F;
         return Float.intBitsToFloat(Float.floatToRawIntBits(var0) + (var0 >= 0.0F ? 1 : -1));
      } else {
         return var0;
      }
   }

   public static double nextDown(double var0) {
      if (!Double.isNaN(var0) && var0 != -1.0D / 0.0) {
         return var0 == 0.0D ? -4.9E-324D : Double.longBitsToDouble(Double.doubleToRawLongBits(var0) + (var0 > 0.0D ? -1L : 1L));
      } else {
         return var0;
      }
   }

   public static float nextDown(float var0) {
      if (!Float.isNaN(var0) && var0 != -1.0F / 0.0) {
         return var0 == 0.0F ? -1.4E-45F : Float.intBitsToFloat(Float.floatToRawIntBits(var0) + (var0 > 0.0F ? -1 : 1));
      } else {
         return var0;
      }
   }

   public static double scalb(double var0, int var2) {
      boolean var4 = false;
      boolean var5 = false;
      double var6 = 0.0D / 0.0;
      short var10;
      if (var2 < 0) {
         var2 = max(var2, -2099);
         var10 = -512;
         var6 = twoToTheDoubleScaleDown;
      } else {
         var2 = min(var2, 2099);
         var10 = 512;
         var6 = twoToTheDoubleScaleUp;
      }

      int var8 = var2 >> 8 >>> 23;
      int var9 = (var2 + var8 & 511) - var8;
      var0 *= powerOfTwoD(var9);

      for(var2 -= var9; var2 != 0; var2 -= var10) {
         var0 *= var6;
      }

      return var0;
   }

   public static float scalb(float var0, int var1) {
      var1 = max(min(var1, 278), -278);
      return (float)((double)var0 * powerOfTwoD(var1));
   }

   static double powerOfTwoD(int var0) {
      assert var0 >= -1022 && var0 <= 1023;

      return Double.longBitsToDouble((long)var0 + 1023L << 52 & 9218868437227405312L);
   }

   static float powerOfTwoF(int var0) {
      assert var0 >= -126 && var0 <= 127;

      return Float.intBitsToFloat(var0 + 127 << 23 & 2139095040);
   }


   private static final class RandomNumberGeneratorHolder {
      static  Random randomNumberGenerator = new Random(1);

      private RandomNumberGeneratorHolder() {
      }
   }
}
