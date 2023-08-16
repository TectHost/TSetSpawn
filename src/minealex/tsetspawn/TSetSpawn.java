package minealex.tsetspawn;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import minealex.tsetspawn.placeholders.Placeholders;
import minealex.tsetspawn.commands.Commands;
import minealex.tsetspawn.commands.SetSpawn;
import minealex.tsetspawn.commands.Spawn;
import minealex.tsetspawn.events.Enter;
import minealex.tsetspawn.events.Join;
import minealex.tsetspawn.events.Welcome;
import minealex.tsetspawn.events.Death;

public class TSetSpawn extends JavaPlugin{
	public String rutaConfig;
	PluginDescriptionFile pdffile = getDescription();
	public String version = pdffile.getVersion();
	public String nombre = ChatColor.BLUE+"["+pdffile.getName()+ChatColor.BLUE+"]";
	
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(nombre+ChatColor.GREEN+" Has been activated (version: "+version+")");
		registerCommands();
		registerEvents();
		registerConfig();
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        }
	}
	
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(nombre+ChatColor.RED+" Has been deactivated (version: "+version+")");
	}
	
	public void registerCommands() {
		this.getCommand("tsetspawn").setExecutor(new Commands(this));
		this.getCommand("spawn").setExecutor(new Spawn(this));
		this.getCommand("setspawn").setExecutor(new SetSpawn(this));
	}
	
	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new Enter(this), this);
		pm.registerEvents(new Join(this), this);
		pm.registerEvents(new Welcome(this), this);
		pm.registerEvents(new Death(this), this);
	}
	
	public void registerConfig() {
		File config = new File(this.getDataFolder(),"config.yml");
		rutaConfig = config.getPath();
		if(!config.exists()) {
			this.getConfig().options().copyDefaults(true);
			saveConfig();
		}
	}
}
