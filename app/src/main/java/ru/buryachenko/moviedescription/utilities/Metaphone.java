package ru.buryachenko.moviedescription.utilities;

public class Metaphone {

    public static String code(String txt) {
        boolean hard;
        if ((txt == null) || (txt.length() == 0)) {
            return "";
        }
        txt = cyr2lat(txt);
        // single character is itself
        if (txt.length() == 1) {
            return txt.toUpperCase();
        }

        char[] inwd = txt.toUpperCase().toCharArray();

        StringBuffer local = new StringBuffer(40); // manipulate
        StringBuilder code = new StringBuilder(10); //   output
        // handle initial 2 characters exceptions
        switch (inwd[0]) {
            case 'K':
            case 'G':
            case 'P': /* looking for KN, etc*/
                if (inwd[1] == 'N') {
                    local.append(inwd, 1, inwd.length - 1);
                } else {
                    local.append(inwd);
                }
                break;
            case 'A': /* looking for AE */
                if (inwd[1] == 'E') {
                    local.append(inwd, 1, inwd.length - 1);
                } else {
                    local.append(inwd);
                }
                break;
            case 'W': /* looking for WR or WH */
                if (inwd[1] == 'R') {   // WR -> R
                    local.append(inwd, 1, inwd.length - 1);
                    break;
                }
                if (inwd[1] == 'H') {
                    local.append(inwd, 1, inwd.length - 1);
                    local.setCharAt(0, 'W'); // WH -> W
                } else {
                    local.append(inwd);
                }
                break;
            case 'X': /* initial X becomes S */
                inwd[0] = 'S';
                local.append(inwd);
                break;
            default:
                local.append(inwd);
        } // now local has working string with initials fixed
        int wdsz = local.length();
        int n = 0;
        while ((code.length() < getMaxCodeLen()) &&
                (n < wdsz)) { // max code size of 4 works well
            char symb = local.charAt(n);
            // remove duplicate letters except C
            if ((symb != 'C') && (isPreviousChar(local, n, symb))) {
                n++;
            } else { // not dup
                String varson = "CSPTG";
                String frontv = "EIY";
                switch (symb) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        code.append(symb);
                        break;
                    case 'A':
                    case 'E':
                    case 'I':
                    case 'O':
                    case 'U':
                        if (n == 0) {
                            code.append(symb);
                        }
                        break; // only use vowel if leading char
                    case 'B':
                        if (isPreviousChar(local, n, 'M') &&
                                isLastChar(wdsz, n)) { // B is silent if word ends in MB
                            break;
                        }
                        code.append(symb);
                        break;
                    case 'C': // lots of C special cases
                        /* discard if SCI, SCE or SCY */
                        if (isPreviousChar(local, n, 'S') &&
                                !isLastChar(wdsz, n) &&
                                (frontv.indexOf(local.charAt(n + 1)) >= 0)) {
                            break;
                        }
                        if (regionMatch(local, n, "CIA")) { // "CIA" -> X
                            code.append('X');
                            break;
                        }
                        if (!isLastChar(wdsz, n) &&
                                (frontv.indexOf(local.charAt(n + 1)) >= 0)) {
                            code.append('S');
                            break; // CI,CE,CY -> S
                        }
                        if (isPreviousChar(local, n, 'S') &&
                                isNextChar(local, n, 'H')) { // SCH->sk
                            code.append('K');
                            break;
                        }
                        if (isNextChar(local, n, 'H')) { // detect CH
                            if ((n == 0) &&
                                    (wdsz >= 3) &&
                                    isVowel(local, 2)) { // CH consonant -> K consonant
                                code.append('K');
                            } else {
                                code.append('X'); // CHvowel -> X
                            }
                        } else {
                            code.append('K');
                        }
                        break;
                    case 'D':
                        if (!isLastChar(wdsz, n + 1) &&
                                isNextChar(local, n, 'G') &&
                                (frontv.indexOf(local.charAt(n + 2)) >= 0)) { // DGE DGI DGY -> J
                            code.append('J');
                            n += 2;
                        } else {
                            code.append('T');
                        }
                        break;
                    case 'G': // GH silent at end or before consonant
                        if (isLastChar(wdsz, n + 1) &&
                                isNextChar(local, n, 'H')) {
                            break;
                        }
                        if (!isLastChar(wdsz, n + 1) &&
                                isNextChar(local, n, 'H') &&
                                !isVowel(local, n + 2)) {
                            break;
                        }
                        if ((n > 0) &&
                                (regionMatch(local, n, "GN") ||
                                        regionMatch(local, n, "GNED"))) {
                            break; // silent G
                        }
                        hard = isPreviousChar(local, n, 'G');
                        if (!isLastChar(wdsz, n) &&
                                (frontv.indexOf(local.charAt(n + 1)) >= 0) &&
                                (!hard)) {
                            code.append('J');
                        } else {
                            code.append('K');
                        }
                        break;
                    case 'H':
                        if (isLastChar(wdsz, n)) {
                            break; // terminal H
                        }
                        if ((n > 0) &&
                                (varson.indexOf(local.charAt(n - 1)) >= 0)) {
                            break;
                        }
                        if (isVowel(local, n + 1)) {
                            code.append('H'); // Hvowel
                        }
                        break;
                    case 'F':
                    case 'J':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'R':
                        code.append(symb);
                        break;
                    case 'K':
                        if (n > 0) { // not initial
                            if (!isPreviousChar(local, n, 'C')) {
                                code.append(symb);
                            }
                        } else {
                            code.append(symb); // initial K
                        }
                        break;
                    case 'P':
                        if (isNextChar(local, n, 'H')) {
                            // PH -> F
                            code.append('F');
                        } else {
                            code.append(symb);
                        }
                        break;
                    case 'Q':
                        code.append('K');
                        break;
                    case 'S':
                        if (regionMatch(local, n, "SH") ||
                                regionMatch(local, n, "SIO") ||
                                regionMatch(local, n, "SIA")) {
                            code.append('X');
                        } else {
                            code.append('S');
                        }
                        break;
                    case 'T':
                        if (regionMatch(local, n, "TIA") ||
                                regionMatch(local, n, "TIO")) {
                            code.append('X');
                            break;
                        }
                        if (regionMatch(local, n, "TCH")) {
                            // Silent if in "TCH"
                            break;
                        }
                        // substitute numeral 0 for TH (resembles theta after all)
                        if (regionMatch(local, n, "TH")) {
                            code.append('0');
                        } else {
                            code.append('T');
                        }
                        break;
                    case 'V':
                        code.append('F');
                        break;
                    case 'W':
                    case 'Y': // silent if not followed by vowel
                        if (!isLastChar(wdsz, n) &&
                                isVowel(local, n + 1)) {
                            code.append(symb);
                        }
                        break;
                    case 'X':
                        code.append('K');
                        code.append('S');
                        break;
                    case 'Z':
                        code.append('S');
                        break;
                } // end switch
                n++;
            } // end else from symb != 'C'
            if (code.length() > getMaxCodeLen()) {
                code.setLength(getMaxCodeLen());
            }
        }
        return code.toString();
    }

    private static boolean isVowel(StringBuffer string, int index) {
        String vowels = "AEIOU";
        return (vowels.indexOf(string.charAt(index)) >= 0);
    }

    private static boolean isPreviousChar(StringBuffer string, int index, char c) {
        boolean matches = false;
        if (index > 0 &&
                index < string.length()) {
            matches = string.charAt(index - 1) == c;
        }
        return matches;
    }

    private static boolean isNextChar(StringBuffer string, int index, char c) {
        boolean matches = false;
        if (index >= 0 &&
                index < string.length() - 1) {
            matches = string.charAt(index + 1) == c;
        }
        return matches;
    }

    private static boolean regionMatch(StringBuffer string, int index, String test) {
        boolean matches = false;
        if (index >= 0 &&
                (index + test.length() - 1) < string.length()) {
            String substring = string.substring(index, index + test.length());
            matches = substring.equals(test);
        }
        return matches;
    }

    private static boolean isLastChar(int wdsz, int n) {
        return n + 1 == wdsz;
    }

    private static int getMaxCodeLen() {
        return 5;
    }

    private static String cyr2lat(char ch) {
        switch (ch) {
            case 'А':
                return "A";
            case 'Б':
                return "B";
            case 'В':
                return "V";
            case 'Г':
                return "G";
            case 'Д':
                return "D";
            case 'Е':
                return "E";
            case 'Ё':
                return "JE";
            case 'Ж':
                return "ZH";
            case 'З':
                return "Z";
            case 'И':
                return "I";
            case 'Й':
                return "Y";
            case 'К':
                return "K";
            case 'Л':
                return "L";
            case 'М':
                return "M";
            case 'Н':
                return "N";
            case 'О':
                return "O";
            case 'П':
                return "P";
            case 'Р':
                return "R";
            case 'С':
                return "S";
            case 'Т':
                return "T";
            case 'У':
                return "U";
            case 'Ф':
                return "F";
            case 'Х':
                return "KH";
            case 'Ц':
                return "C";
            case 'Ч':
                return "CH";
            case 'Ш':
                return "SH";
            case 'Щ':
                return "JSH";
            case 'Ъ':
                return "HH";
            case 'Ы':
                return "IH";
            case 'Ь':
                return "JH";
            case 'Э':
                return "EH";
            case 'Ю':
                return "JU";
            case 'Я':
                return "JA";
            default:
                return String.valueOf(ch);
        }
    }

    private static String cyr2lat(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (char ch : s.toCharArray()) {
            sb.append(cyr2lat(ch));
        }
        return sb.toString();
    }
}
