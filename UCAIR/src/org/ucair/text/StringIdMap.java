package org.ucair.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

public class StringIdMap {

    private final List<String> names = new ArrayList<String>();

    private final Map<String, Integer> nameToId = new HashMap<String, Integer>();

    public StringIdMap() {
        names.add("");
    }

    public String get(final int id) {
        Preconditions.checkArgument(id >= 1 && id <= names.size());
        return names.get(id);
    }

    public Integer get(final String name) {
        return nameToId.get(name);
    }

    public int size() {
        return names.size() - 1;
    }

    public int add(final String name) {
        Integer id = nameToId.get(name);
        if (id != null) {
            return id;
        }
        id = names.size();
        names.add(name);
        nameToId.put(name, id);
        return id;
    }
}
