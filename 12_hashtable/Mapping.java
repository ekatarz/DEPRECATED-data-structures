
import java.util.*;

interface IMap <K, V> {

  public List<K> keys();
  public List<V> values();
  public List<Entry<K,V>> entries();

  public boolean containsKey(K key);
  public void inc(K key);
  public void put( K key, V value);

  public V get(K key);
  public V remove(K key);

  public int size();
  public void clear();
  public boolean isEmpty();

}

class Entry <K, V> {

  int hash;
  boolean isNumber;
  K key; V value;
  public Entry(K key, V value) {
    this.key = key;
    this.value = value;
    this.hash = key != null ? key.hashCode() : -1; // Compute hash and store it
    this.isNumber = Number.class.isInstance(value);
  }

  // No casting required with this method. This does not override the Object method
  public boolean equals(Entry <K,V> other) {
    if (other == null) return false;
    if ( hash != other.hash ) return false;
    return key.equals( other.key );
  }

  @Override public String toString() {
    return key + " => " + value; 
  }

}

@SuppressWarnings("unchecked")
public class Mapping <K, V> implements IMap<K, V>, Iterable <K> {

  // Make these private
  int capacity, threshold, size = 0;
  double loadFactor = 0.70;

  private Entry <K,V> table[];
  private static final java.math.BigInteger maxTableSZ = new java.math.BigInteger(String.valueOf(Integer.MAX_VALUE));

  public Mapping (  ) { this(11); }

  // Designated constructor
  public Mapping (int capacity) {
    if (capacity < 0) throw new IllegalArgumentException("Capacity cannot be less than zero");
    this.capacity = Math.max(1, capacity);
    table = (Entry<K,V>[]) java.lang.reflect.Array.newInstance(Entry.class, capacity);
    threshold = (int) (capacity * loadFactor);
  }

  public Mapping (int capacity, double loadFactor) {
    this(capacity);
    if (loadFactor <= 0 || Double.isNaN(loadFactor) ) 
      throw new IllegalArgumentException("Illegal loadFactor: " + loadFactor);
    this.loadFactor = loadFactor;
  }

  private int hash1(int keyHash ) {
    // Avoids negative index problem
    return (keyHash & 0x7fffffff) % capacity;
  }

  // Returns a positive number
  private int hash2(int keyHash ) {
    return 31 - keyHash % 31;
  }

  public List<K> keys() {
    List<K> keys = new ArrayList<>();
    for(int i = 0; i < capacity; i++)
      if (table[i] != null)
        keys.add(table[i].key);
    return keys;
  }

  public List<V> values() {
    List<V> values = new ArrayList<>();
    for(int i = 0; i < capacity; i++)
      if (table[i] != null)
        values.add(table[i].value);
    return values;
  }

  public List<Entry<K,V>> entries() {
    List<Entry<K,V>> entries = new ArrayList<>();
    for(int i = 0; i < capacity; i++)
      if (table[i] != null)
        entries.add(table[i]);
    return entries;
  }

  public boolean containsKey(K key) {
    
    int index = hash1(key.hashCode());
    int step  = hash2(index);
    while( table[index] != null ) {
      if(table[index].key.equals(key))
        return true;
      index += step;
      index = (index & 0x7fffffff) % capacity;
    }
    return false;

  }

  // Applies only if V is a number
  public void inc(K key) {

    /*
    int index = hash1(key.hashCode());
    int step  = hash2(index); 

    while ( table[index] == DELETED_ENTRY ) {
      index += step;
      index = (index & 0x7fffffff) % capacity;
    }

    // Check if it is a number type
    if (table[index] != null)
      table[index].value = table[index].value + 1;
    */

  } 

  public void put( K key, V value) {

    Entry <K,V> newEntry = new Entry<>(key, value);
    int index = hash1(newEntry.hash);
    int step  = hash2(index);
    boolean insertedElem = false;

    while (!insertedElem) {

      Entry <K,V> entry = table[index];

      // Found empty slot for this Key
      if (entry == null) {
        table[index] = newEntry;
        insertedElem = true;
        size++;

      // Update existing key with new value
      } else if ( entry.equals(newEntry) ) {
        entry.value = value;
        insertedElem = true;

      // Keep searching
      } else {
        index += step;
        index = (index & 0x7fffffff) % capacity;
      }

    }

  }

  // Searches table for key
  public V get(K key) {

    int index = hash1(key.hashCode());
    int step  = hash2(index); 

    while ( table[index] != null ) {
      if (table[index].equals(key))
        return table[index].value;
      index += step;
      index = (index & 0x7fffffff) % capacity;
    }

    return null;

  }

  public V remove(K key) {
    
    return null;

  }

  public int size() {
    return size;
  }

  public void clear() {

  }

  private void increaseCapacity( ) {

    // Computes the next table size keeping it prime
    java.math.BigInteger bigCapacity = new java.math.BigInteger(String.valueOf(capacity));
    bigCapacity = bigCapacity.shiftLeft(1); // Multiply by two
    capacity =  bigCapacity.nextProbablePrime().min( maxTableSZ ).intValue();      
    
  }

  // should be private
  void rehash() {
    
    increaseCapacity( );

  }

  public boolean isEmpty() {
    return size == 0;
  }

  public Iterator <K> iterator() {
    return null;
  }

}

