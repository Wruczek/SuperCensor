package mr.wruczek.supercensor3.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import mr.wruczek.supercensor3.utils.LoggerUtils;
import mr.wruczek.supercensor3.utils.SCUtils;
import mr.wruczek.supercensor3.utils.classes.SCPermissionsEnum;

/**
 * This work is licensed under a Creative Commons Attribution-NoDerivatives 4.0
 * International License. http://creativecommons.org/licenses/by-nd/4.0/
 *
 * @author Wruczek
 */
public class SCReportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!SCUtils.checkPermissions(sender, SCPermissionsEnum.BASICADMIN.toString())) {
            return false;
        }

        if (LoggerUtils.lastError == null || LoggerUtils.lastError.isEmpty()) {
            sender.sendMessage("[SC] No last error found. Nothing to report.");
            return false;
        }

        StringBuilder sb = new StringBuilder();

        for (String str : LoggerUtils.lastError) {
            sb.append(str).append("\n");
        }

        try {
            sender.sendMessage("[SC] Link to report: " + hastebinPost(sb.toString()));
            sender.sendMessage("Please use this link when creating new issue.");
        } catch (Exception e) {
            sender.sendMessage("[SC] Cannot send error report! " + e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Simple API for uploading data to hastebin.com<br>
     * (c) 2016 Wruczek<br>
     * MIT license
     *
     * @param data
     *            Message body
     * @return URL to paste
     * @throws ParseException
     */
    public static URL hastebinPost(String data) throws IOException, ParseException {
        URL url = new URL("https://hastebin.com/documents");

        byte[] postData = data.getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postData);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char) c);

        String response = sb.toString();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response);

        String id = (String) jsonObject.get("key");

        return new URL("http://hastebin.com/" + id);
    }

}
