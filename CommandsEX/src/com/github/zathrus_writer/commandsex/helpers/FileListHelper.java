package com.github.zathrus_writer.commandsex.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.github.zathrus_writer.commandsex.Language._;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class FileListHelper {
	
	public static void checkListFile(File listfile, String defresource) {
		if (!listfile.exists()){
			LogHelper.logInfo("[CommandsEX] " + _("fileListHelperCouldNotFind", "") + " " +listfile.getName()+", " + _("fileListHelperCreatingDefaultFile", "") + ".");
			try {
				InputStream in = CommandsEX.plugin.getResource(defresource);
				FileOutputStream out = new FileOutputStream(listfile);
				
				// Transfer bytes from in to out
			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = in.read(buf)) > 0) {
			        out.write(buf, 0, len);
			    }
			    in.close();
			    out.close();
			} catch (IOException e) {
				LogHelper.logWarning("[CommandsEX] " + _("fileListHelperIOException", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
	}

	public static List<ReplacementPair> loadListFromFile(File listfile) {
		BufferedReader br = null;
		List<ReplacementPair> res = new ArrayList<ReplacementPair>();
		try {
			if (!listfile.canRead()){ throw new FileNotFoundException(); }
			
			br = new BufferedReader(new FileReader(listfile));
			Pattern p = Pattern.compile("\\/(.+)\\/(\\w*)\\s*==>\\s*(.*)");
			String line;
			
			int success = 0, lineno = 0;
			while ((line = br.readLine()) != null) {
				lineno++;
				if (line.isEmpty() || line.startsWith("#")) continue;
				
				// pre-process line to determine case-sensitivity options
				Boolean sameOutputCase = false;
				if (line.contains("{cc}")) {
					line = line.replace("{cc}", "");
					sameOutputCase = true;
				}
				
				Boolean caseSensitive = true;
				if (line.contains("{ci}")) {
					line = line.replace("{ci}", "");
					caseSensitive = false;
				}
				
				Matcher m = p.matcher(line);
				if (m.matches()) {
					success++;
					
					String regex = m.group(1);
					//String opts = m.group(2);
					String repl = m.group(3);
					//Log.info("line: "+line+" > "+regex+" ==> "+repl);

					res.add(new ReplacementPair(regex, repl, caseSensitive, sameOutputCase));
				} else {
					LogHelper.logWarning("[CommandsEX] " + _("line", "") + " " + lineno + " " + _("fileListHelperLineBadFormat", ""));
				}
			}
			LogHelper.logDebug("[CommandsEX] Successfully imported "+success+" patterns from "+listfile.getName());
		} catch (FileNotFoundException ex){
			LogHelper.logWarning("[CommandsEX] " + _("fileListHelperCouldNotOpenRepl", "") + ": "+listfile.getName());
		} catch (IOException e) {
			LogHelper.logWarning("[CommandsEX] " + _("fileListHelperIOException2", "") + listfile.getName());
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
		} finally {
			try { if (br != null) br.close(); } catch (IOException e) {}
		}
		
		return res;
	}
}
