package mr.wruczek.supercensor3.commands;

import mr.wruczek.supercensor3.SCMain;
import mr.wruczek.supercensor3.utils.StringUtils;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0 International License.
 * http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCCommandHeader {

    private static String headerChar;
    private static String header = "";

    public SCCommandHeader() {
        headerChar = StringUtils.color("&8&m-&r");
        header = StringUtils.color(getHeaderChar(5) + " &7SuperCensor &c"
                + SCMain.getInstance().getDescription().getVersion()
                + "&7 by &6Wruczek " + getHeaderChar(5));
    }

    public static String getHeaderChar() {
        return getHeaderChar(1);
    }

    public static String getHeaderChar(int howmany) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < howmany; i++) {
            sb.append(headerChar);
        }

        return sb.toString();
    }

    public static String getHeader() {
        return header;
    }

}
