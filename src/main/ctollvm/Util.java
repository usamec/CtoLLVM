package ctollvm;
import java.util.StringTokenizer;

/* Copyright (c) 2008 Google Inc.
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
//package com.google.gdata.util.common.base;


/**
 * Some common string manipulation utilities.
 */
public class Util{

    /**
     * Unescape any C escape sequences (\n, \r, \\, \ooo, etc) and return the
     * resulting string.
     */
    public static String unescapeCString(String s) {
      if (s.indexOf('\\') < 0) {
        // Fast path: nothing to unescape
        return s;
      }

      StringBuilder sb = new StringBuilder();
      int len = s.length();
      for (int i = 0; i < len;) {
        char c = s.charAt(i++);
        if (c == '\\' && (i < len)) {
          c = s.charAt(i++);
          switch (c) {
            case 'a':  c = '\007';  break;
            case 'b':  c = '\b';    break;
            case 'f':  c = '\f';    break;
            case 'n':  c = '\n';    break;
            case 'r':  c = '\r';    break;
            case 't':  c = '\t';    break;
            case 'v':  c = '\013';  break;
            case '\\': c = '\\';    break;
            case '?':  c = '?';     break;
            case '\'': c = '\'';    break;
            case '"':  c = '\"';    break;

            default: {
              // TODO: toto je myslim zle, ma to matchovat, kym vidi hex znaky
              if ((c == 'x') && (i < len) && isHex(s.charAt(i))) {
                // "\xXX"
                int v = hexValue(s.charAt(i++));
                if ((i < len) && isHex(s.charAt(i))) {
                  v = v*16 + hexValue(s.charAt(i++));
                }
                c = (char)v;
              } else if (isOctal(c)) {
                // "\OOO"
                int v = (c - '0');
                if ((i < len) && isOctal(s.charAt(i))) {
                  v = v*8 + (s.charAt(i++) - '0');
                }
                if ((i < len) && isOctal(s.charAt(i))) {
                  v = v*8 + (s.charAt(i++) - '0');
                }
                c = (char)v;
              } else {
                // Propagate unknown escape sequences.
                sb.append('\\');
              }
              break;
            }
          }
        }
        sb.append(c);
      }
      return sb.toString();
    }

    private static boolean isOctal(char c) {
      return (c >= '0') && (c <= '7');
    }

    private static boolean isHex(char c) {
      return ((c >= '0') && (c <= '9')) ||
             ((c >= 'a') && (c <= 'f')) ||
             ((c >= 'A') && (c <= 'F'));
    }

    private static int hexValue(char c) {
      if ((c >= '0') && (c <= '9')) {
        return (c - '0');
      } else if ((c >= 'a') && (c <= 'f')) {
        return (c - 'a') + 10;
      } else {
        return (c - 'A') + 10;
      }
    }

    public static String escapeStringForLLVM(String s) {
      StringBuffer buf = new StringBuffer();
      byte bytes[] = s.getBytes();
      for (int i = 0; i < bytes.length; i++) {
        buf.append(String.format("\\%02X", bytes[i])); 
      }
      buf.append("\\00");
      return buf.toString();
    }
}
