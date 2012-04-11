package com.github.zathrus_writer.commandsex;

import static com.github.zathrus_writer.commandsex.Language._;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

/***
 * Contains functions required for database data manipulation.
 * @author zathrus-writer
 */

public class SQLManager {
	// the CommandsEx plugin
	public static CommandsEX plugin;
	public transient static Connection conn;
	public transient static Connection altConn; // used for sqlite to MySQL conversions
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

		// create core tables
		query("CREATE TABLE IF NOT EXISTS "+ prefix +"playtime (player_name varchar(32) NOT NULL, seconds_played int(10) " + (sqlType.equals("mysql") ? "unsigned " : "") + "NOT NULL DEFAULT '0', PRIMARY KEY (player_name))" + (sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='holds playtime of each player on the server'" : ""));
		query("CREATE TABLE IF NOT EXISTS "+ prefix +"user2lang (username varchar(50) NOT NULL, lang varchar(5) NOT NULL, PRIMARY KEY (`username`))" + (sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores per-user selected plugin language'" : ""));
	}

	/***
	 * Initializes requested connection type. Used for sqlite to MySQL and vice-versa conversions.
	 * @param sqlType
	 */
	public static Boolean init_alt(String v, String altFileName) {
		v = v.toLowerCase();

		// use our default DB file if not set manually
		if (altFileName.equals("")) {
			altFileName = ((plugin.getConfig().getString("database") != null) ? plugin.getConfig().getString("database") : "data") + ".db";
		}

		if (v.equals("sqlite")) {
			try {
				Class.forName("org.sqlite.JDBC");
				altConn = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separatorChar + altFileName);
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbSQLiteNotUsable", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return false;
			}
		} else if (v.equals("mysql")) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				altConn = DriverManager.getConnection("jdbc:mysql://" 
														+ ((plugin.getConfig().getString("host") != null) ? plugin.getConfig().getString("host") : "localhost")
														+ ":" + ((plugin.getConfig().getString("port") != null) ? plugin.getConfig().getString("port") : "3306")
														+ "/" + ((plugin.getConfig().getString("database") != null) ? plugin.getConfig().getString("database") : "minecraft"),
														((plugin.getConfig().getString("name") != null) ? plugin.getConfig().getString("name") : "root"),
														((plugin.getConfig().getString("password") != null) ? plugin.getConfig().getString("password") : ""));
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbMySQLNotUsable", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return false;
			}
		}

		return true;
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
				return false;
			}
		} else {
			// if we have only 1 parameter that is an ArrayList, make an array of objects out of it
			if ((params.length == 1) && ((params[0] instanceof List) || (params[0] instanceof ArrayList))) {
				params = ((List<?>) params[0]).toArray();
			}

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
					} else if (o instanceof Boolean) {
						prep.setBoolean(i, (Boolean)o);
					} else if (o instanceof Date) {
						prep.setTimestamp(i, new Timestamp(((Date) o).getTime()));
					} else if (o instanceof Timestamp) {
						prep.setTimestamp(i, (Timestamp) o);
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
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Query: " + query + ", parameters: " + Utils.implode(params, ", "));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return false;
			}
		}
		
		return true;
	}
	
	/***
	 * Executes prepared query using alternative connection. Used for conversion purposes.
	 * @param query
	 * @param params
	 * @return
	 */
	public static Boolean query_alt(String query, Object... params) {
		Connection c = conn;
		conn = altConn;
		Boolean ret = query(query, params);
		conn = c;
		return ret;
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
			// if we have only 1 parameter that is an ArrayList, make an array of objects out of it
			if ((params.length == 1) && ((params[0] instanceof List) || (params[0] instanceof ArrayList))) {
				params = ((List<?>) params[0]).toArray();
			}

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
	 * Executes a selection SQL statement using alternative connection and returns a resultset.
	 * Used for conversion purposes.
	 * @param query
	 * @param params
	 * @return
	 */
	public static ResultSet query_res_alt(String query, Object... params) {
		Connection c = conn;
		conn = altConn;
		ResultSet ret = query_res(query, params);
		conn = c;
		return ret;
	}
	
	/***
	 * closes any DB connection that's still open
	 * (used when disabling the plugin)
	 */
	public static void close() {
		if (CommandsEX.sqlEnabled) {
			try {
				conn.close();
				altConn.close();
			} catch (Exception e) {}
		}
	}
	
	/***
	 * closes alternative DB connection
	 */
	public static void close_alt() {
		if (CommandsEX.sqlEnabled) {
			try {
				altConn.close();
			} catch (Exception e) {}
		}
	}
}
