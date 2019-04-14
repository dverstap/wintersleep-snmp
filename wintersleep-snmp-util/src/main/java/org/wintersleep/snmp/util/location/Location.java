/*
 * Copyright 2005 Davy Verstappen.
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
package org.wintersleep.snmp.util.location;

/**
 * Line and column numbers are 1-based (not 0-based).
 */
public class Location {

    public static final char SEPARATOR = ':';
    public static final int INVALID_LINE = -1;
    public static final int INVALID_COLUMN = -1;

    private String source;
    private int line = INVALID_LINE;
    private int column = INVALID_COLUMN;

    public Location(String file, int line, int column) {
        source = file;
        this.line = line;
        this.column = column;
    }

    public Location(String source, int line) {
        this.source = source;
        this.line = line;
    }

    public Location(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if (source != null) {
            result.append(source);
        }
        result.append(SEPARATOR);
        if (line > INVALID_LINE) {
            result.append(line);
        }
        result.append(SEPARATOR);
        if (column > INVALID_LINE) {
            result.append(column);
        }
        return result.toString();
    }
}
