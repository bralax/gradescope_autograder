//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package brandon.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream.PutField;
import java.util.Spliterator.OfDouble;
import java.util.Spliterator.OfInt;
import java.util.Spliterator.OfLong;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class Random implements Serializable {
   static final long serialVersionUID = 3905348978240129619L;
   private final AtomicLong seed;
   private static final long multiplier = 25214903917L;
   private static final long addend = 11L;
   private static final long mask = 281474976710655L;
   private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
   static final String BadBound = "bound must be positive";
   static final String BadRange = "bound must be greater than origin";
   static final String BadSize = "size must be non-negative";
   private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
   private double nextNextGaussian;
   private boolean haveNextNextGaussian;
   private static final ObjectStreamField[] serialPersistentFields;
   private static final Unsafe unsafe;
   private static final long seedOffset;

   public Random() {
      this(1);
      //this(seedUniquifier() ^ System.nanoTime());
   }

   private static long seedUniquifier() {
      long var0;
      long var2;
      do {
         var0 = seedUniquifier.get();
         var2 = var0 * 181783497276652981L;
      } while(!seedUniquifier.compareAndSet(var0, var2));

      return var2;
   }

   public Random(long var1) {
      this.haveNextNextGaussian = false;
      if (this.getClass() == Random.class) {
         this.seed = new AtomicLong(initialScramble(var1));
      } else {
         this.seed = new AtomicLong();
         this.setSeed(var1);
      }

   }

   private static long initialScramble(long var0) {
      return (var0 ^ 25214903917L) & 281474976710655L;
   }

   public synchronized void setSeed(long var1) {
      this.seed.set(initialScramble(var1));
      this.haveNextNextGaussian = false;
   }

   protected int next(int var1) {
      AtomicLong var6 = this.seed;

      long var2;
      long var4;
      do {
         var2 = var6.get();
         var4 = var2 * 25214903917L + 11L & 281474976710655L;
      } while(!var6.compareAndSet(var2, var4));

      return (int)(var4 >>> 48 - var1);
   }

   public void nextBytes(byte[] var1) {
      int var2 = 0;
      int var3 = var1.length;

      while(var2 < var3) {
         int var4 = this.nextInt();

         for(int var5 = Math.min(var3 - var2, 4); var5-- > 0; var4 >>= 8) {
            var1[var2++] = (byte)var4;
         }
      }

   }

   final long internalNextLong(long var1, long var3) {
      long var5 = this.nextLong();
      if (var1 < var3) {
         long var7 = var3 - var1;
         long var9 = var7 - 1L;
         if ((var7 & var9) == 0L) {
            var5 = (var5 & var9) + var1;
         } else if (var7 > 0L) {
            for(long var11 = var5 >>> 1; var11 + var9 - (var5 = var11 % var7) < 0L; var11 = this.nextLong() >>> 1) {
            }

            var5 += var1;
         } else {
            while(var5 < var1 || var5 >= var3) {
               var5 = this.nextLong();
            }
         }
      }

      return var5;
   }

   final int internalNextInt(int var1, int var2) {
      if (var1 >= var2) {
         return this.nextInt();
      } else {
         int var3 = var2 - var1;
         if (var3 > 0) {
            return this.nextInt(var3) + var1;
         } else {
            int var4;
            do {
               do {
                  var4 = this.nextInt();
               } while(var4 < var1);
            } while(var4 >= var2);

            return var4;
         }
      }
   }

   final double internalNextDouble(double var1, double var3) {
      double var5 = this.nextDouble();
      if (var1 < var3) {
         var5 = var5 * (var3 - var1) + var1;
         if (var5 >= var3) {
            var5 = Double.longBitsToDouble(Double.doubleToLongBits(var3) - 1L);
         }
      }

      return var5;
   }

   public int nextInt() {
      return this.next(32);
   }

   public int nextInt(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("bound must be positive");
      } else {
         int var2 = this.next(31);
         int var3 = var1 - 1;
         if ((var1 & var3) == 0) {
            var2 = (int)((long)var1 * (long)var2 >> 31);
         } else {
            for(int var4 = var2; var4 - (var2 = var4 % var1) + var3 < 0; var4 = this.next(31)) {
            }
         }

         return var2;
      }
   }

   public long nextLong() {
      return ((long)this.next(32) << 32) + (long)this.next(32);
   }

   public boolean nextBoolean() {
      return this.next(1) != 0;
   }

   public float nextFloat() {
      return (float)this.next(24) / 1.6777216E7F;
   }

   public double nextDouble() {
      return (double)(((long)this.next(26) << 27) + (long)this.next(27)) * 1.1102230246251565E-16D;
   }

   public synchronized double nextGaussian() {
      if (this.haveNextNextGaussian) {
         this.haveNextNextGaussian = false;
         return this.nextNextGaussian;
      } else {
         double var1;
         double var3;
         double var5;
         do {
            do {
               var1 = 2.0D * this.nextDouble() - 1.0D;
               var3 = 2.0D * this.nextDouble() - 1.0D;
               var5 = var1 * var1 + var3 * var3;
            } while(var5 >= 1.0D);
         } while(var5 == 0.0D);

         double var7 = StrictMath.sqrt(-2.0D * StrictMath.log(var5) / var5);
         this.nextNextGaussian = var3 * var7;
         this.haveNextNextGaussian = true;
         return var1 * var7;
      }
   }

   public IntStream ints(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.intStream(new Random.RandomIntsSpliterator(this, 0L, var1, 2147483647, 0), false);
      }
   }

   public IntStream ints() {
      return StreamSupport.intStream(new Random.RandomIntsSpliterator(this, 0L, 9223372036854775807L, 2147483647, 0), false);
   }

   public IntStream ints(long var1, int var3, int var4) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var4) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.intStream(new Random.RandomIntsSpliterator(this, 0L, var1, var3, var4), false);
      }
   }

   public IntStream ints(int var1, int var2) {
      if (var1 >= var2) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.intStream(new Random.RandomIntsSpliterator(this, 0L, 9223372036854775807L, var1, var2), false);
      }
   }

   public LongStream longs(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.longStream(new Random.RandomLongsSpliterator(this, 0L, var1, 9223372036854775807L, 0L), false);
      }
   }

   public LongStream longs() {
      return StreamSupport.longStream(new Random.RandomLongsSpliterator(this, 0L, 9223372036854775807L, 9223372036854775807L, 0L), false);
   }

   public LongStream longs(long var1, long var3, long var5) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var5) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.longStream(new Random.RandomLongsSpliterator(this, 0L, var1, var3, var5), false);
      }
   }

   public LongStream longs(long var1, long var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.longStream(new Random.RandomLongsSpliterator(this, 0L, 9223372036854775807L, var1, var3), false);
      }
   }

   public DoubleStream doubles(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else {
         return StreamSupport.doubleStream(new Random.RandomDoublesSpliterator(this, 0L, var1, 1.7976931348623157E308D, 0.0D), false);
      }
   }

   public DoubleStream doubles() {
      return StreamSupport.doubleStream(new Random.RandomDoublesSpliterator(this, 0L, 9223372036854775807L, 1.7976931348623157E308D, 0.0D), false);
   }

   public DoubleStream doubles(long var1, double var3, double var5) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("size must be non-negative");
      } else if (var3 >= var5) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.doubleStream(new Random.RandomDoublesSpliterator(this, 0L, var1, var3, var5), false);
      }
   }

   public DoubleStream doubles(double var1, double var3) {
      if (var1 >= var3) {
         throw new IllegalArgumentException("bound must be greater than origin");
      } else {
         return StreamSupport.doubleStream(new Random.RandomDoublesSpliterator(this, 0L, 9223372036854775807L, var1, var3), false);
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      GetField var2 = var1.readFields();
      long var3 = var2.get("seed", -1L);
      if (var3 < 0L) {
         throw new StreamCorruptedException("Random: invalid seed");
      } else {
         this.resetSeed(var3);
         this.nextNextGaussian = var2.get("nextNextGaussian", 0.0D);
         this.haveNextNextGaussian = var2.get("haveNextNextGaussian", false);
      }
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      PutField var2 = var1.putFields();
      var2.put("seed", this.seed.get());
      var2.put("nextNextGaussian", this.nextNextGaussian);
      var2.put("haveNextNextGaussian", this.haveNextNextGaussian);
      var1.writeFields();
   }

   private void resetSeed(long var1) {
      unsafe.putObjectVolatile(this, seedOffset, new AtomicLong(var1));
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("seed", Long.TYPE), new ObjectStreamField("nextNextGaussian", Double.TYPE), new ObjectStreamField("haveNextNextGaussian", Boolean.TYPE)};
      //unsafe = Unsafe.getUnsafe();
      
      Field f;
      try {
         f = Unsafe.class.getDeclaredField("theUnsafe");
         f.setAccessible(true);
         unsafe = (Unsafe) f.get(null);
      } catch (Exception e) {
         throw new Error(e);
      }
      try {
         seedOffset = unsafe.objectFieldOffset(Random.class.getDeclaredField("seed"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class RandomDoublesSpliterator implements OfDouble {
      final Random rng;
      long index;
      final long fence;
      final double origin;
      final double bound;

      RandomDoublesSpliterator(Random var1, long var2, long var4, double var6, double var8) {
         this.rng = var1;
         this.index = var2;
         this.fence = var4;
         this.origin = var6;
         this.bound = var8;
      }

      public Random.RandomDoublesSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new Random.RandomDoublesSpliterator(this.rng, var1, this.index = var3, this.origin, this.bound);
      }

      public long estimateSize() {
         return this.fence - this.index;
      }

      public int characteristics() {
         return 17728;
      }

      public boolean tryAdvance(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               var1.accept(this.rng.internalNextDouble(this.origin, this.bound));
               this.index = var2 + 1L;
               return true;
            } else {
               return false;
            }
         }
      }

      public void forEachRemaining(DoubleConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               this.index = var4;
               Random var6 = this.rng;
               double var7 = this.origin;
               double var9 = this.bound;

               do {
                  var1.accept(var6.internalNextDouble(var7, var9));
               } while(++var2 < var4);
            }

         }
      }
   }

   static final class RandomLongsSpliterator implements OfLong {
      final Random rng;
      long index;
      final long fence;
      final long origin;
      final long bound;

      RandomLongsSpliterator(Random var1, long var2, long var4, long var6, long var8) {
         this.rng = var1;
         this.index = var2;
         this.fence = var4;
         this.origin = var6;
         this.bound = var8;
      }

      public Random.RandomLongsSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new Random.RandomLongsSpliterator(this.rng, var1, this.index = var3, this.origin, this.bound);
      }

      public long estimateSize() {
         return this.fence - this.index;
      }

      public int characteristics() {
         return 17728;
      }

      public boolean tryAdvance(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               var1.accept(this.rng.internalNextLong(this.origin, this.bound));
               this.index = var2 + 1L;
               return true;
            } else {
               return false;
            }
         }
      }

      public void forEachRemaining(LongConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               this.index = var4;
               Random var6 = this.rng;
               long var7 = this.origin;
               long var9 = this.bound;

               do {
                  var1.accept(var6.internalNextLong(var7, var9));
               } while(++var2 < var4);
            }

         }
      }
   }

   static final class RandomIntsSpliterator implements OfInt {
      final Random rng;
      long index;
      final long fence;
      final int origin;
      final int bound;

      RandomIntsSpliterator(Random var1, long var2, long var4, int var6, int var7) {
         this.rng = var1;
         this.index = var2;
         this.fence = var4;
         this.origin = var6;
         this.bound = var7;
      }

      public Random.RandomIntsSpliterator trySplit() {
         long var1 = this.index;
         long var3 = var1 + this.fence >>> 1;
         return var3 <= var1 ? null : new Random.RandomIntsSpliterator(this.rng, var1, this.index = var3, this.origin, this.bound);
      }

      public long estimateSize() {
         return this.fence - this.index;
      }

      public int characteristics() {
         return 17728;
      }

      public boolean tryAdvance(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               var1.accept(this.rng.internalNextInt(this.origin, this.bound));
               this.index = var2 + 1L;
               return true;
            } else {
               return false;
            }
         }
      }

      public void forEachRemaining(IntConsumer var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            long var2 = this.index;
            long var4 = this.fence;
            if (var2 < var4) {
               this.index = var4;
               Random var6 = this.rng;
               int var7 = this.origin;
               int var8 = this.bound;

               do {
                  var1.accept(var6.internalNextInt(var7, var8));
               } while(++var2 < var4);
            }

         }
      }
   }
}
