package org.wintersleep.snmp.mib.smi;

import org.wintersleep.snmp.util.token.IdToken;
import org.wintersleep.snmp.mib.phase.xref.XRefProblemReporter;

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
public class ScopedId {

    private final SmiModule localModule;
    private final IdToken moduleToken;
    private final IdToken symbolToken;
    private SmiModule module;
    private SmiSymbol symbol;

    public ScopedId(SmiModule localModule, IdToken moduleToken, IdToken symbolToken) {
        this.localModule = localModule;
        this.moduleToken = moduleToken;
        this.symbolToken = symbolToken;
    }


    public SmiModule getLocalModule() {
        return localModule;
    }

    public IdToken getModuleToken() {
        return moduleToken;
    }

    public IdToken getSymbolToken() {
        return symbolToken;
    }

    public SmiModule getModule() {
        return module;
    }

    public SmiSymbol getSymbol() {
        return symbol;
    }

    public void resolveReferences(XRefProblemReporter reporter) {
        if (moduleToken != null) {
            module = localModule.getMib().resolveModule(moduleToken, reporter);
        }
        if (module != null) {
            symbol = module.resolveReference(symbolToken, reporter);
        } else {
            symbol = localModule.resolveReference(symbolToken, reporter);
        }
    }
}
