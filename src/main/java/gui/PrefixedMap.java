package gui;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Представление подсловаря с заданным префиксом
 * Реализует Фильтр на основе AbstractMap
 */
public class PrefixedMap extends AbstractMap<String, String> {
    private final Map<String, String> sourceMap;
    private final String prefix;
    private final boolean flagPrefix;

    /**
     * Создает подсловарь с префиксом
     * @param sourceMap исходный словарь
     * @param prefix префикс
     * @param flagPrefix флаг удаления/добавления префикса для чтения/записи из общего словаря
     */
    public PrefixedMap(Map<String, String> sourceMap, String prefix, boolean flagPrefix) {
        this.sourceMap = sourceMap;
        this.prefix = prefix + ".";
        this.flagPrefix = flagPrefix;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return new PrefixedEntrySet();
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        String stringKey = (String) key;
        if (flagPrefix) {
            return sourceMap.get(prefix + stringKey);
        } else {
            return sourceMap.get(stringKey);
        }
    }

    @Override
    public String put(String key, String value) {
        if (flagPrefix){
            return sourceMap.put(prefix + key, value);
        } else {
            if (!key.startsWith(prefix)) {
                return sourceMap.put(prefix + key, value);
            }
            return sourceMap.put(key, value);
        }
    }

    /**
     * Множество записей для подсловаря
     */
    private class PrefixedEntrySet extends AbstractSet<Entry<String, String>> {
        @Override
        public Iterator<Entry<String, String>> iterator() {
            return new PrefixIterator();
        }

        @Override
        public int size() {
            int count = 0;
            for (String key : sourceMap.keySet()) {
                if (key.startsWith(prefix)) {
                    count++;
                }
            }
            return count;
        }
    }

    /**
     * Итератор для записей подсловоря
     */
    private class PrefixIterator implements Iterator<Entry<String, String>>{
        private final Iterator<Entry<String, String>> sourceIterator;
        private Entry<String, String> nextEntry;

        public PrefixIterator() {
            this.sourceIterator = sourceMap.entrySet().iterator();
            findNext();
        }

        private void findNext() {
            nextEntry = null;
            while (sourceIterator.hasNext()) {
                Entry<String, String> entry = sourceIterator.next();
                if (entry.getKey().startsWith(prefix)) {
                    nextEntry = entry;
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return nextEntry != null;
        }

        @Override
        public Entry<String, String> next() {
            Entry<String, String> current = nextEntry;
            String key = current.getKey();

            if (flagPrefix) {
                String strippedKey = key.substring(prefix.length());
                findNext();
                return new SimpleEntry<>(strippedKey, current.getValue());
            } else {
                findNext();
                return new SimpleEntry<>(key, current.getValue());
            }
        }
    }
}
