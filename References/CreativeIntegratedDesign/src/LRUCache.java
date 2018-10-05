import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class LRUCache {
	int capacity = 20;
	LinkedHashMap<String, ByteBuffer> Cache = new LinkedHashMap<String, ByteBuffer>();
	int size = 500 * 1024;
	int current_size = 0;
	
	LRUCache() {
		this.capacity = 50;	
		this.Cache = new LinkedHashMap<String, ByteBuffer>();
		this.size = 50 * 1024 * 1024;
		this.current_size = 0;
	}
	
	LRUCache(int capacity, int size) {
		this.capacity = capacity;
		this.size = size;
	}
	
	public ByteBuffer get(String uri) {
		ByteBuffer value = Cache.get(uri);
		
		if (value == null) {
			//System.out.println("Cache Miss");
		} else {
			Cache.remove(uri);
			Cache.put(uri, value);
			//System.out.println("Cache Hit");
		}
		return value;
	}
	
	public void set(String uri, ByteBuffer value) {
		if (value.capacity() > 10 * 1024 * 1024) {
			return;
		}
		 
		if (Cache.remove(uri) == null) {
			if (Cache.size() >= capacity) {
				RemoveLastElement();
			}
			while (current_size + value.capacity() > size) {
				RemoveLastElement();
			}	
		}
		
		Cache.put(uri, value);
		current_size += value.capacity();
	}
	
	public void RemoveLastElement() {
		String lastKey = (String) Cache.keySet().toArray()[(Cache.size()-1)];
		ByteBuffer lastValue = Cache.get(lastKey);
		Cache.remove(lastKey);
		current_size = current_size - lastValue.capacity();
	}
}
