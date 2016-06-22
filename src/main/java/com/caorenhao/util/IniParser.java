package com.caorenhao.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Parse the Ini File
 * 
 * @author vernkin
 * 
 */
public class IniParser {

    public IniParser() {

    }

    @SuppressWarnings("resource")
	public void parse(String path) throws IniParserException {
        sectionMap.clear();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            IniSection iniSection = null;
            while ((line = br.readLine()) != null) {
                line = removeCommentAndSpace(line);
                if (line == null || line.isEmpty() == true)
                    continue;

                if (line.charAt(0) == '[') {
                    int maxOffset = line.length() - 1;
                    if (line.charAt(maxOffset) != ']')
                        throw new IniParserException("Invalid section line: "
                                + line);
                    String sectionName = line.substring(1, maxOffset).trim();
                    // find previous IniSection with the same name
                    iniSection = sectionMap.get(sectionName);
                    if (iniSection == null) {
                        iniSection = new IniSection(sectionName);
                        sectionMap.put(sectionName, iniSection);
                    }
                    continue;
                }

                if (iniSection == null) {
                    throw new IniParserException(
                            "No section is given when parsing: " + line);
                }

                parseValueLine(line, iniSection);
            }

        } catch (Throwable t) {
            throw new IniParserException("In Parsing file: " + path, t);
        } finally {
            IOUtil.forceClose(br);
        }
    }

    public IniSection getSection(String name) {
        return sectionMap.get(name);
    }
    
    public Set<String> keys() {
        return sectionMap.keySet();
    }
    
    public Collection<IniSection> values() {
        return sectionMap.values();
    }

    protected String removeCommentAndSpace(String in) {
        if (in.isEmpty() == true)
            return null;
        int len = in.length();
        // remove heading spaces
        int beginIdx = 0;
        while (beginIdx < len && in.charAt(beginIdx) == ' ')
            ++beginIdx;

        if (beginIdx == len)
            return null;

        // remove comments
        int endIdx = beginIdx;
        for (; endIdx < len; ++endIdx) {
            if (in.charAt(endIdx) == ';') {
                if (endIdx > 0 && in.charAt(endIdx - 1) == '\\')
                    continue;
                break;
            }
        }

        // comment founded
        if (endIdx < len) {
            return in.substring(beginIdx, endIdx);
        }

        // or remove tailing spaces
        endIdx = len - 1;
        while (endIdx >= 0 && in.charAt(endIdx) == ' ')
            --endIdx;
        // The last space is reserved if it is behind the '\'
        if (endIdx + 1 != len && in.charAt(endIdx) == '\\')
            ++endIdx;

        return in.substring(beginIdx, endIdx + 1);
    }

    protected void parseValueLine(String in, IniSection section)
            throws IniParserException {
        int len = in.length();
        int equalIdx = 0;
        for (; equalIdx < len; ++equalIdx) {
            if (in.charAt(equalIdx) == '=') {
                if (equalIdx > 0 && in.charAt(equalIdx - 1) == '\\')
                    continue;
                break;
            }
        }

        if (equalIdx == len || equalIdx == 0)
            throw new IniParserException("Invalid INI equal line: " + in);

        try {
            String key = StrUtil
                    .convertEscapeSequnce(in.substring(0, equalIdx)).trim();
            String value = StrUtil.convertEscapeSequnce(
                    in.substring(equalIdx + 1)).trim();
            section.put(key, value);
        } catch (Throwable t) {
            throw new IniParserException("In Parsing line " + in, t);
        }
    }

    public Map<String, IniSection> getSectionMap() {
    	return sectionMap;
    }
    
    private Map<String, IniSection> sectionMap = new HashMap<String, IniSection>();
}
