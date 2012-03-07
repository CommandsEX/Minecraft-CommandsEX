package com.github.zathrus_writer.commandsex;

import static com.github.zathrus_writer.commandsex.CommandsEX._;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

/***
 * Contains functions required for database data manipulation.
 * @author zathrus-writer
 */

public class SQLManager {
	public final static Logger LOGGER = Logger.getLogger("Minecraft");
	
	// the CommandsEx plugin
	public static CommandsEX plugin;
	public static boolean enabled = false;
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
		
		switch (p.getConfig().getString("sqlType").toLowerCase()) {
			case "sqlite":	try {
								Class.forName("org.sqlite.JDBC");
								conn = DriverManager.getConnection("jdbc:sqlite:" + p.getDataFolder() + File.separatorChar + ((p.getConfig().getString("database") != null) ? p.getConfig().getString("database") : "data") + ".db");
								enabled = true;
								sqlType = "sqlite";
							} catch (Exception e) {
								LOGGER.severe("[CommandsEX] " + _("dbSQLiteNotUsable", ""));
							}
							break;

			case "mysql":	try {
								Class.forName("com.mysql.jdbc.Driver");
								conn = DriverManager.getConnection("jdbc:mysql://" 
																		+ ((p.getConfig().getString("host") != null) ? p.getConfig().getString("host") : "localhost")
																		+ ":" + ((p.getConfig().getString("port") != null) ? p.getConfig().getString("port") : "3306")
																		+ "/" + ((p.getConfig().getString("database") != null) ? p.getConfig().getString("database") : "minecraft"),
																		((p.getConfig().getString("name") != null) ? p.getConfig().getString("name") : "root"),
																		((p.getConfig().getString("password") != null) ? p.getConfig().getString("password") : ""));
								prefix = ((p.getConfig().getString("prefix") != null) ? p.getConfig().getString("prefix") : "");
								enabled = true;
								sqlType = "mysql";
							} catch (Exception e) {
								LOGGER.severe("[CommandsEX] " + _("dbMySQLNotUsable", ""));
							}
							break;
		}
	}


	/***
	 * Executes a prepared query that doesn't return a resultset.
	 * @param query
	 * @param params
	 * @return
	 */
	public static Boolean query(String query, Object... params) {
		if (!enabled) {
			return false;
		}

		if (params.length == 0) {
			try {
				Statement stat = conn.createStatement();
				stat.executeUpdate(query);
				stat.close();
			} catch (Exception e) {
				LOGGER.severe("[CommandsEX] " + _("dbWriteError", "") + query + " (Msg: "+ e.getMessage() +")");
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
					} else if (o.equals(null)) {
						prep.setNull(i, (int)o);
					} else {
						// unhandled variable type
						LOGGER.severe("[CommandsEX]" +  _("dbQueryParamError", "") + query + ", " + _("variable", "") + ": " + o.toString());
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
			} catch (Exception e) {
				String params_str = "";
				if (params.length > 0) {
					for (Object o : params) {
						params_str = params_str + ", " + o.toString();
					}
				}
				LOGGER.severe("[CommandsEX] " + _("dbWriteError", "") + query + " ... parameters: " + params_str + " (Error: "+ e.getMessage() + ")");
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
		if (!enabled) {
			return null;
		}

		if (params.length == 0) {
			try {
				Statement stat = conn.createStatement();
				ResultSet res = stat.executeQuery(query);
				return res;
			} catch (Exception e) {
				LOGGER.severe("[CommandsEX] " + _("dbWriteError", "") + query);
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
					} else if (o.equals(null)) {
						prep.setNull(i, (int)o);
					} else {
						// unhandled variable type
						LOGGER.severe("[CommandsEX] " + _("dbQueryParamError", "") + query + ", " + _("variable", "") + ": " + o.toString());
						prep.close();
						return null;
					}
					i++;
				}
				return prep.executeQuery();
			} catch (Exception e) {
				LOGGER.severe("[CommandsEX] " + _("dbWriteError", "") + query);
			}
		}

		return null;
	}
	
	
	/***
	 * closes any DB connection that's still open
	 * (used when disabling the plugin)
	 */
	public static void close() {
		if (enabled) {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
}
