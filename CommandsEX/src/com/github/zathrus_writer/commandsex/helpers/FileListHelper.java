package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.scripting.BadScriptException;
import com.github.zathrus_writer.commandsex.helpers.scripting.EchoControl;
import com.github.zathrus_writer.commandsex.helpers.scripting.ReplacementCommand;
import com.github.zathrus_writer.commandsex.helpers.scripting.ReplacementPair;
import com.github.zathrus_writer.commandsex.helpers.scripting.ReplacementRandom;
import com.github.zathrus_writer.commandsex.helpers.scripting.ReplacementScript;
import com.github.zathrus_writer.commandsex.helpers.scripting.ReplacementString;
import com.github.zathrus_writer.commandsex.helpers.scripting.ScriptBlock;

public class FileListHelper {
	
	public static HashMap<String, ScriptBlock> aliasedScripts = null;
	public enum MatchingContext {
		Chat, Command,
	}
	
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
	
	public static List<ReplacementPair> loadListFromFile(File listfile, MatchingContext mc) {
		BufferedReader br = null;
		List<ReplacementPair> ret = new ArrayList<ReplacementPair>();
		try {
			if (!listfile.canRead()){ throw new FileNotFoundException(); }
			br = new BufferedReader(new FileReader(listfile));
			/* Explanation of Regex:
			 *  /hello/o =m=> [opt,opt2] world
			 * \/(.+)\/(\w*)\s*=(\w?)=([>{])\s*(?:\[([^\])]+\])?\s*(.*)
			 *   \--/   \-/      \-/   \--/         \----/     \-/
			 *     |     |        |      |            |       Replacement String/Script Alias
			 *     |     |        |      |        Replacement Options
			 *     |     |        |    Script or Single Mode
			 *     |     |      Method Character
			 *     |   Regex Option Chatacters
			 *   Regex
			 */
			Pattern p = Pattern.compile("\\/(.+)\\/(\\w*)\\s*=(\\w?)=([>{])\\s*(?:\\[([^\\]]+)\\])?\\s*(.*)");
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
					ReplacementPair rp = null;
					success++;
					
					String regex = m.group(1);
					String opts = m.group(2);
					String methodstr = m.group(3);
					String scriptmode = m.group(4);
					String replopts = m.group(5); 
					String repl = m.group(6);
					//LogHelper.logDebug("line: "+line+" > "+regex+" ==> "+repl);
					
					if (scriptmode.equals(">")){
						char method = ' ';
						if (!methodstr.isEmpty()) method = methodstr.charAt(0);
						switch (method){
						case 'c': //command method - to force command instead of chat replacement
							rp = new ReplacementCommand(regex, repl, replopts, caseSensitive, sameOutputCase); break;
						case 'r': //random method - choose from one of the ; separated list
							if (mc != MatchingContext.Chat) {
								LogHelper.logWarning(_("cmdRandomReplacementNotAllowed", "")+lineno);
								continue;
							}
							rp = new ReplacementRandom(regex, repl, caseSensitive, sameOutputCase); break;
						case ' ':
						default:
							switch (mc){
							case Command:
								rp = new ReplacementCommand(regex, repl, replopts, caseSensitive, sameOutputCase); break;
							case Chat: //only use ReplacementString for chat
								rp = new ReplacementString(regex, repl, replopts, caseSensitive, sameOutputCase); break;
							}
							break;
						}
						
					} else if (scriptmode.equals("{")){
						try {
							int blockDepth = 1;
							ArrayList<String> scriptblock = new ArrayList<String>();
							
							while ((line = br.readLine()) != null){
								//loop through input lines to get the script block
								lineno++;
								if (line.isEmpty() || line.startsWith("#")) continue;
								
								if (line.trim().equals("{")){
									blockDepth++;
								} else if (line.trim().equals("}")) {
									blockDepth--;
								} 
								if (blockDepth == 0){
									ScriptBlock block = new ScriptBlock(scriptblock, repl);
									rp = new ReplacementScript(regex, block, replopts, caseSensitive, sameOutputCase);
									setScriptForAlias(repl, block);
									break;
								} else {
									scriptblock.add(line);
								}
							}
							if (blockDepth != 0){
								//if the above loop broke without completing the script, it hit the end of line
								throw new BadScriptException("EOF reached before end of script reached! Please re-balance braces!");
							}
						} catch (BadScriptException e) {
							LogHelper.logWarning("[CommandsEX] " + _("fileListHelperBadScript", "") + lineno);
							LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
						}
					}
					rp.setRegexOptions(opts);
					ret.add(rp);
				} else {
					LogHelper.logWarning("[CommandsEX] " + _("line", "") + " " + lineno + " " + _("fileListHelperLineBadFormat", ""));
				}
			}
			
			LogHelper.logDebug("[CommandsEX] Successfully imported "+success+" patterns from "+listfile.getName());
		} catch (FileNotFoundException ex){
			LogHelper.logWarning("[CommandsEX] " + _("fileListHelperCouldNotOpenRepl", "") + ": "+listfile.getName());
			LogHelper.logDebug("Message: " + ex.getMessage() + ", cause: " + ex.getCause());
		} catch (IOException e) {
			LogHelper.logWarning("[CommandsEX] " + _("fileListHelperIOException2", "") + listfile.getName());
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
		} finally {
			try { if (br != null) br.close(); } catch (IOException e) {}
		}
		
		return ret;
	}
	
	////////////////////////////////////////////////

	public static void setScriptForAlias(String alias, ScriptBlock script){
		if (alias == null || alias.isEmpty()) return;
		aliasedScripts.put(alias, script);
	}
	
	public static ScriptBlock getScript(String alias){
		return aliasedScripts.get(alias);
	}
	
	public class EchoCommand implements CommandExecutor {
		@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (args.length == 0) return false;

			StringBuffer sb = new StringBuffer(args[0]);
			for (int i = 1; i < args.length; i++){
				sb.append(' ').append(args[i]);
			}
			((EchoControl)sender).getWrappedSender().sendMessage(sb.toString());
			//sender.sendMessage(sb.toString());
			return true;
		}
	}
}
