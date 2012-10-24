package com.github.zathrus_writer.commandsex.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Authentication {
	private final static int ITERATION_NUMBER = 1000;
	
	public static void init(CommandsEX plugin){
		SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"userinfo (id_user integer " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") +"NOT NULL" + (SQLManager.sqlType.equals("mysql") ? " AUTO_INCREMENT" : "") +", player_name varchar(32) NOT NULL" + (SQLManager.sqlType.equals("mysql") ? "" : " COLLATE 'NOCASE'") + ", creation_date " + (SQLManager.sqlType.equals("mysql") ? "TIMESTAMP" : "DATETIME") + " NOT NULL DEFAULT CURRENT_TIMESTAMP, expiration_date " + (SQLManager.sqlType.equals("mysql") ? "TIMESTAMP" : "DATETIME") + " NOT NULL DEFAULT '0000-00-00 00:00:00', creator VARCHAR(32) NOT NULL, reason VARCHAR(120) DEFAULT NULL, active BOOLEAN NOT NULL DEFAULT '1', PRIMARY KEY (id_ban)" + (SQLManager.sqlType.equals("mysql") ? ", KEY player_name (player_name), KEY expiration_date (expiration_date), KEY active (active)" : "" ) + ")" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='holds ban records for players along with reasons and ban expiration times' AUTO_INCREMENT=1" : "") + (SQLManager.sqlType.equals("mysql") ? "" : "; CREATE INDEX IF NOT EXISTS player_name ON "+ SQLManager.prefix +"bans (player_name); CREATE INDEX IF NOT EXISTS expiration_date ON "+ SQLManager.prefix +"bans (expiration_date); CREATE INDEX IF NOT EXISTS active ON "+ SQLManager.prefix +"bans (active); COMMIT;"));
		for(int i=0; i< ALPHABET.length; i++){
            toInt[ALPHABET[i]]= i;
        }
	}

	 /**
	    * Authenticates the user with a given login and password
	    * If password and/or login is null then always returns false.
	    * If the user does not exist in the database returns false.
	    * @param con Connection An open connection to a databse
	    * @param login String The login of the user
	    * @param password String The password of the user
	    * @return boolean Returns true if the user is authenticated, false otherwise
	    * @throws SQLException If the database is inconsistent or unavailable (
	    *           (Two users with the same login, salt or digested password altered etc.)
	    * @throws NoSuchAlgorithmException If the algorithm SHA-1 is not supported by the JVM
	    */
	   public boolean authenticate(Connection con, String login, String password)
	           throws SQLException, NoSuchAlgorithmException{
	       @SuppressWarnings("unused")
	       boolean authenticated = false;
	       PreparedStatement ps = null;
	       ResultSet rs = null;
	       try {
	           boolean userExist = true;
	           // INPUT VALIDATION
	           if (login==null||password==null){
	               // TIME RESISTANT ATTACK
	               // Computation time is equal to the time needed by a legitimate user
	               userExist = false;
	               login="";
	               password="";
	           }
	 
	           ps = con.prepareStatement("SELECT PASSWORD, SALT FROM CREDENTIAL WHERE LOGIN = ?");
	           ps.setString(1, login);
	           rs = ps.executeQuery();
	           String digest, salt;
	           if (rs.next()) {
	               digest = rs.getString("PASSWORD");
	               salt = rs.getString("SALT");
	               // DATABASE VALIDATION
	               if (digest == null || salt == null) {
	                   throw new SQLException("Database inconsistant Salt or Digested Password altered");
	               }
	               if (rs.next()) { // Should not append, because login is the primary key
	                   throw new SQLException("Database inconsistent two CREDENTIALS with the same LOGIN");
	               }
	           } else { // TIME RESISTANT ATTACK (Even if the user does not exist the
	               // Computation time is equal to the time needed for a legitimate user
	               digest = "000000000000000000000000000=";
	               salt = "00000000000=";
	               userExist = false;
	           }
	 
	           byte[] bDigest = base64ToByte(digest);
	           byte[] bSalt = base64ToByte(salt);
	 
	           // Compute the new DIGEST
	           byte[] proposedDigest = getHash(ITERATION_NUMBER, password, bSalt);
	 
	           return Arrays.equals(proposedDigest, bDigest) && userExist;
	       } catch (IOException ex){
	           throw new SQLException("Database inconsistant Salt or Digested Password altered");
	       }
	       finally{
	           close(rs);
	           close(ps);
	       }
	   }
	 
	 
	 
	   /**
	    * Inserts a new user in the database
	    * @param con Connection An open connection to a databse
	    * @param login String The login of the user
	    * @param password String The password of the user
	    * @return boolean Returns true if the login and password are ok (not null and length(login)<=100
	    * @throws SQLException If the database is unavailable
	    * @throws NoSuchAlgorithmException If the algorithm SHA-1 or the SecureRandom is not supported by the JVM
	 * @throws UnsupportedEncodingException 
	    */
	   public boolean createUser(Connection con, String login, String password)
	           throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException
	   {
	       PreparedStatement ps = null;
	       try {
	           if (login!=null&&password!=null&&login.length()<=100){
	               // Uses a secure Random not a simple Random
	               SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	               // Salt generation 64 bits long
	               byte[] bSalt = new byte[8];
	               random.nextBytes(bSalt);
	               // Digest computation
	               byte[] bDigest = getHash(ITERATION_NUMBER,password,bSalt);
	               String sDigest = byteToBase64(bDigest);
	               String sSalt = byteToBase64(bSalt);
	 
	               ps = con.prepareStatement("INSERT INTO CREDENTIAL (LOGIN, PASSWORD, SALT) VALUES (?,?,?)");
	               ps.setString(1,login);
	               ps.setString(2,sDigest);
	               ps.setString(3,sSalt);
	               ps.executeUpdate();
	               return true;
	           } else {
	               return false;
	           }
	       } finally {
	           close(ps);
	       }
	   }
	 
	 
	   /**
	    * From a password, a number of iterations and a salt,
	    * returns the corresponding digest
	    * @param iterationNb int The number of iterations of the algorithm
	    * @param password String The password to encrypt
	    * @param salt byte[] The salt
	    * @return byte[] The digested password
	    * @throws NoSuchAlgorithmException If the algorithm doesn't exist
	 * @throws UnsupportedEncodingException 
	    */
	   public byte[] getHash(int iterationNb, String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	       MessageDigest digest = MessageDigest.getInstance("SHA-1");
	       digest.reset();
	       digest.update(salt);
	       byte[] input = digest.digest(password.getBytes("UTF-8"));
	       for (int i = 0; i < iterationNb; i++) {
	           digest.reset();
	           input = digest.digest(input);
	       }
	       return input;
	   }
	 
	 
	   public void creerTable(Connection con) throws SQLException{
	       Statement st = null;
	       try {
	           st = con.createStatement();
	           st.execute((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN;") + "CREATE TABLE CREDENTIAL (LOGIN VARCHAR(100) PRIMARY KEY, PASSWORD VARCHAR(32) NOT NULL, SALT VARCHAR(32) NOT NULL)");
	       } finally {
	           close(st);
	       }
	   }
	 
	 
	 
	   /**
	    * Closes the current statement
	    * @param ps Statement
	    */
	   public void close(Statement ps) {
	       if (ps!=null){
	           try {
	               ps.close();
	           } catch (SQLException ignore) {
	           }
	       }
	   }
	 
	   /**
	    * Closes the current resultset
	    * @param ps Statement
	    */
	   public void close(ResultSet rs) {
	       if (rs!=null){
	           try {
	               rs.close();
	           } catch (SQLException ignore) {
	           }
	       }
	   }
	 
	   
	   private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	   private static int[]  toInt   = new int[128];
	 
	   /**
	    * From a base 64 representation, returns the corresponding byte[]
	    * @author GeorgeK (http://stackoverflow.com/users/518568/georgek)
	    * @param data String The base64 representation
	    * @return byte[]
	    * @throws IOException
	    */
	   public static byte[] base64ToByte(String data) throws IOException {
		   int delta = data.endsWith( "==" ) ? 2 : data.endsWith( "=" ) ? 1 : 0;
	       byte[] buffer = new byte[data.length()*3/4 - delta];
	       int mask = 0xFF;
	       int index = 0;
	       for(int i=0; i< data.length(); i+=4){
	           int c0 = toInt[data.charAt( i )];
	           int c1 = toInt[data.charAt( i + 1)];
	           buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
	           if(index >= buffer.length){
	               return buffer;
	           }
	           int c2 = toInt[data.charAt( i + 2)];
	           buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
	           if(index >= buffer.length){
	               return buffer;
	           }
	           int c3 = toInt[data.charAt( i + 3 )];
	           buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
	       }
	       return buffer;
	   }
	 
	   /**
	    * From a byte[] returns a base 64 representation
	    * @author GeorgeK (http://stackoverflow.com/users/518568/georgek)
	    * @param data byte[]
	    * @return String
	    * @throws IOException
	    */
	   public static String byteToBase64(byte[] data) {
		   int size = data.length;
		   char[] ar = new char[((size + 2) / 3) * 4];
		   int a = 0;
		   int i=0;
		   while(i < size){
		       byte b0 = data[i++];
		       byte b1 = (i < size) ? data[i++] : 0;
		       byte b2 = (i < size) ? data[i++] : 0;
               int mask = 0x3F;
	           ar[a++] = ALPHABET[(b0 >> 2) & mask];
	           ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
	           ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
	           ar[a++] = ALPHABET[b2 & mask];
	       }
	       switch(size % 3){
	           case 1: ar[--a]  = '=';
	           case 2: ar[--a]  = '=';
	       }
	       return new String(ar);
	}
}
