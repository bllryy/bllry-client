package me.lily.bllry.commands.impl;

import me.lily.bllry.Bllry;
import me.lily.bllry.commands.Command;
import me.lily.bllry.commands.RegisterCommand;
import me.lily.bllry.utils.chat.ChatUtils;
import me.lily.bllry.utils.system.FileUtils;

import java.io.IOException;

@RegisterCommand(name = "config", tag = "Config", description = "Allows you to manage the client's configuration system.", syntax = "<load|save> <[name]> | <reload|save|current>")
public class ConfigCommand extends Command {
    @Override
    public void execute(String[] args) {
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "load" -> {
                    if (!FileUtils.fileExists(Bllry.MOD_NAME + "/Configs/" + args[1] + ".json")) {
                        Bllry.CHAT_MANAGER.tagged("The specified configuration does not exist.", getTag(), getName());
                        return;
                    }

                    try {
                        Bllry.CONFIG_MANAGER.loadModules(args[1]);
                        Bllry.CHAT_MANAGER.tagged("Successfully loaded the " + ChatUtils.getPrimary() + args[1] + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                    } catch (IOException exception) {
                        Bllry.CHAT_MANAGER.tagged("Failed to load the " + ChatUtils.getPrimary() + args[1] + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                    }
                }
                case "save" -> {
                    try {
                        Bllry.CONFIG_MANAGER.saveModules(args[1]);
                        Bllry.CHAT_MANAGER.tagged("Successfully saved the configuration to " + ChatUtils.getPrimary() + args[1] + ".json" + ChatUtils.getSecondary() + ".", getTag(), getName());
                    } catch (IOException exception) {
                        Bllry.CHAT_MANAGER.tagged("Failed to save the " + ChatUtils.getPrimary() + args[1] + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                    }
                }
                default -> messageSyntax();
            }
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    Bllry.CONFIG_MANAGER.loadConfig();
                    Bllry.CHAT_MANAGER.tagged("Successfully reloaded the current configuration.", getTag(), getName());
                }
                case "save" -> {
                    Bllry.CONFIG_MANAGER.saveConfig();
                    Bllry.CHAT_MANAGER.tagged("Successfully saved the current configuration.", getTag(), getName());
                }
                case "current" -> Bllry.CHAT_MANAGER.tagged("The client is currently using the " + ChatUtils.getPrimary() + Bllry.CONFIG_MANAGER.getCurrentConfig() + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                default -> messageSyntax();
            }
        } else {
            messageSyntax();
        }
    }
}
