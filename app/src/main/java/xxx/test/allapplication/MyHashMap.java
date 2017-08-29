package xxx.test.allapplication;

import java.util.HashMap;

/**
 * Created by liujun on 17/8/22.
 */

public class MyHashMap<K,V> extends HashMap<K,V> {
  public MyHashMap() {

  }

  @Override public V put(K key, V value) {
    return super.put(key, value);
  }
}
