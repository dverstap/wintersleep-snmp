package org.wintersleep.snmp.mib.smi;

import org.wintersleep.snmp.util.multimap.GenMultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/*
* Copyright 2007 Davy Verstappen.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
class SmiSymbolMapImpl<T extends SmiSymbol> extends GenMultiMap<String, T> implements SmiSymbolMap<T> {

    private final Class<T> symbolClass;
    private final Map<String, SmiModule> moduleMap;

    public SmiSymbolMapImpl(Class<T> symbolClass, Map<String, SmiModule> moduleMap) {
        super(MultiValueMap.decorate(new HashMap(), ArrayList.class));
        this.symbolClass = symbolClass;
        this.moduleMap = moduleMap;
    }

    public T find(String symbolId) throws IllegalArgumentException {
        return getOne(symbolId);
    }

    public T find(String moduleId, String symbolId) throws IllegalArgumentException {
        if (moduleId != null) {
            SmiModule module = moduleMap.get(moduleId);
            if (module == null) {
                throw new IllegalArgumentException("Module " + moduleId + " could not be found.");
            }
            SmiSymbol symbol = module.findSymbol(symbolId);
            if (symbol == null) {
                return null;
            }
            if (symbolClass.isAssignableFrom(symbol.getClass())) {
                return symbolClass.cast(symbol);
            } else {
                //throw new IllegalArgumentException(symbol.getClass().getSimpleName() + " with id " + symbolId + " is not an instance of " + symbolClass);
                return null; // when the lookup is through the global map, we also return null for an object that is of the wrong type.
            }
        } else {
            return getOne(symbolId);
        }
    }

    public List<T> findAll(String symbolId) {
        return getAll(symbolId);
    }

    @SuppressWarnings("unchecked")
    public Collection<T> getAll() {
        return impl.values();
    }

    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
        return impl.values().iterator();
    }

}
