/*
 * Copyright 2012-2016 Davy Verstappen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wintersleep.snmp.anttasks.mib2java;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.wintersleep.snmp.util.url.URLListFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileSetURLListFactory implements URLListFactory {

    private final FileSet fileSet;

    public FileSetURLListFactory(FileSet fileSet) {
        this.fileSet = fileSet;
    }

    public List<URL> create() throws Exception {
        List<URL> result = new ArrayList<>();
        for (File file : listFiles(fileSet)) {
            result.add(file.toURI().toURL());
        }
        return result;
    }

    public static List<File> listFiles(FileSet fileSet) {
        List<File> result = new ArrayList<>();
        DirectoryScanner scanner = fileSet.getDirectoryScanner();
        for (String name : scanner.getIncludedFiles()) {
            result.add(new File(fileSet.getDir(), name));
        }
        return result;

    }
}
