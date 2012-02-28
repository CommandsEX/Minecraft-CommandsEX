package com.github.zathrus_writer.commandsex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/***
 * Credits: Essentials Bukkit plugin http://dev.bukkit.org/server-mods/essentials
 * @author Seraph
 *
 */
public class FileResClassLoader extends ClassLoader
{
	private final transient File dataFolder;

	public FileResClassLoader(final ClassLoader classLoader, final CommandsEX c)
	{
		super(classLoader);
		this.dataFolder = c.getDataFolder();
	}

	@Override
	public URL getResource(final String string)
	{
		final File file = new File(dataFolder, string);
		if (file.exists())
		{
			try
			{
				return file.toURI().toURL();
			}
			catch (MalformedURLException ex)
			{
			}
		}
		return super.getResource(string);
	}

	@Override
	public InputStream getResourceAsStream(final String string)
	{
		final File file = new File(dataFolder, string);
		if (file.exists())
		{
			try
			{
				return new FileInputStream(file);
			}
			catch (FileNotFoundException ex)
			{
			}
		}
		return super.getResourceAsStream(string);
	}
}
