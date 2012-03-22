package com.github.zathrus_writer.commandsex;

import static com.github.zathrus_writer.commandsex.Language._;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;

/***
 * Contains functions required for database data manipulation.
 * @author zathrus-writer
 */

public class SQLManager {
	// the CommandsEx plugin
	public static CommandsEX plugin;
	public transient static Connection conn;
	public transient static String prefix = "cex_";
	public transient static String sqlType;
	
	/***
	 * Constructor, sets the main plugin class locally and initiates a connection
	 * based on config settings.
	 * @param plugin
	 */
	public static void init(CommandsEX p) {
		plugin = p;
		
		String v = p.getConfig().getString("sqlType").toLowerCase();
		if (v.equals("sqlite")) {
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:" + p.getDataFolder() + File.separatorChar + ((p.getConfig().getString("database") != null) ? p.getConfig().getString("database") : "data") + ".db");
				CommandsEX.sqlEnabled = true;
				sqlType = "sqlite";
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbSQLiteNotUsable", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		} else if (v.equals("mysql")) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://" 
														+ ((p.getConfig().getString("host") != null) ? p.getConfig().getString("host") : "localhost")
														+ ":" + ((p.getConfig().getString("port") != null) ? p.getConfig().getString("port") : "3306")
														+ "/" + ((p.getConfig().getString("database") != null) ? p.getConfig().getString("database") : "minecraft"),
														((p.getConfig().getString("name") != null) ? p.getConfig().getString("name") : "root"),
														((p.getConfig().getString("password") != null) ? p.getConfig().getString("password") : ""));
				prefix = ((p.getConfig().getString("prefix") != null) ? p.getConfig().getString("prefix") : "");
				CommandsEX.sqlEnabled = true;
				sqlType = "mysql";
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbMySQLNotUsable", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
	}


	/***
	 * Executes a prepared query that doesn't return a resultset.
	 * @param query
	 * @param params
	 * @return
	 */
	public static Boolean query(String query, Object... params) {
		if (!CommandsEX.sqlEnabled) {
			return false;
		}

		if (params.length == 0) {
			try {
				Statement stat = conn.createStatement();
				stat.executeUpdate(query);
				stat.close();
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Query: " + query);
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		} else {
			try {
				PreparedStatement prep = conn.prepareStatement(query);
				Integer i = 1;
				for (Object o : params) {
					if (o instanceof Integer) {
						prep.setInt(i, (Integer)o);
					} else if (o instanceof String) {
						prep.setString(i, (String)o);
					} else if (o instanceof Double) {
						prep.setDouble(i, (Double)o);
					} else if (o instanceof Float) {
						prep.setFloat(i, (Float)o);
					} else if (o instanceof Long) {
						prep.setLong(i, (Long)o);
					} else if (o == null) {
						prep.setNull(i, 0);
					} else {
						// unhandled variable type
						LogHelper.logSevere("[CommandsEX]" +  _("dbQueryParamError", ""));
						LogHelper.logDebug("Query: " + query + ", variable: " + o.toString());
						prep.clearBatch();
						prep.close();
						return false;
					}
					i++;
				}
				prep.addBatch();
				conn.setAutoCommit(false);
				prep.executeBatch();
				conn.commit();
				prep.close();
				prep = null;
			} catch (Throwable e) {
				String params_str = "";
				if (params.length > 0) {
					for (Object o : params) {
						params_str = params_str + ", " + o.toString();
					}
				}
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Query: " + query + ", parameters: " + params_str);
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
		
		return true;
	}
	
	
	/***
	 * Executes a selection SQL statement and returns a resultset.
	 * @param query
	 * @param params
	 * @return
	 */
	public static ResultSet query_res(String query, Object... params) {
		if (!CommandsEX.sqlEnabled) {
			return null;
		}

		if (params.length == 0) {
			try {
				Statement stat = conn.createStatement();
				ResultSet res = stat.executeQuery(query);
				return res;
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Query: " + query);
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		} else {
			try {
				PreparedStatement prep = conn.prepareStatement(query);
				Integer i = 1;
				for (Object o : params) {
					if (o instanceof Integer) {
						prep.setInt(i, (Integer)o);
					} else if (o instanceof String) {
						prep.setString(i, (String)o);
					} else if (o instanceof Double) {
						prep.setDouble(i, (Double)o);
					} else if (o instanceof Float) {
						prep.setFloat(i, (Float)o);
					} else if (o instanceof Long) {
						prep.setLong(i, (Long)o);
					} else if (o == null) {
						prep.setNull(i, 0);
					} else {
						// unhandled variable type
						LogHelper.logSevere("[CommandsEX] " + _("dbQueryParamError", ""));
						LogHelper.logDebug("Query: " + query + ", variable: " + o.toString());
						prep.close();
						return null;
					}
					i++;
				}
				return prep.executeQuery();
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Query: " + query);
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}

		return null;
	}
	
	
	/***
	 * closes any DB connection that's still open
	 * (used when disabling the plugin)
	 */
	public static void close() {
		if (CommandsEX.sqlEnabled) {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
}
