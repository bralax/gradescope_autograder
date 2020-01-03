package brandon.convert;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


public class ClassConverterList implements Iterable<ClassConverter> {
   public List<ClassConverter> allConversions;

   public ClassConverterList() {
      this.allConversions = new ArrayList<>();
      this.allConversions.add(new CharConverter());
      this.allConversions.add(new StringConverter());
      this.allConversions.add(new FloatConverter());
      this.allConversions.add(new DoubleConverter());
      this.allConversions.add(new IntConverter());
      this.allConversions.add(new LongConverter());
      this.allConversions.add(new BooleanConverter());
      this.allConversions.add(new CharArrayConverter());
      this.allConversions.add(new StringArrayConverter());
      this.allConversions.add(new FloatArrayConverter());
      this.allConversions.add(new DoubleArrayConverter());
      this.allConversions.add(new IntArrayConverter());
      this.allConversions.add(new LongArrayConverter());
      this.allConversions.add(new BooleanArrayConverter());
   }

   public void add(ClassConverter c) {
      this.allConversions.add(c);
   }

   public Iterator<ClassConverter> iterator() {
      return this.allConversions.iterator();
   }
}
