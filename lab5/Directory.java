
import java.util.HashMap;
import java.util.Map;

class Directory {
    private final Map<String, Integer> entries = new HashMap<>();

    public void addEntry(String name, int descriptorIndex) {
        entries.put(name, descriptorIndex);
    }

    public void removeEntry(String name) {
        entries.remove(name);
    }

    public Integer getEntry(String name) {
        return entries.get(name);
    }

    public Boolean isEmpty() {
            return entries.isEmpty();
        
    }

    public Map<String, Integer> listEntries() {
        return new HashMap<>(entries);
    }
}
