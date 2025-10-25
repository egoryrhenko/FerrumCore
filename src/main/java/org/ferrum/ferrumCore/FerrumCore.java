package org.ferrum.ferrumCore;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.ferrum.ferrumCore.chat.ChatCommand.IgnoreCommand;
import org.ferrum.ferrumCore.chat.ChatCommand.PrivateMessageCommand;
import org.ferrum.ferrumCore.chat.ChatCommand.ReplyCommand;
import org.ferrum.ferrumCore.chat.listeners.ChatListener;
import org.ferrum.ferrumCore.chat.listeners.MinecraftMessagesListener;
import org.ferrum.ferrumCore.chat.util.IgnoreBD;
import org.ferrum.ferrumCore.chat.util.SpyManager;
import org.ferrum.ferrumCore.commands.*;
import org.ferrum.ferrumCore.hooks.LuckPermsHook;
import org.ferrum.ferrumCore.hooks.PlaceholderHook;
import org.ferrum.ferrumCore.listeners.BotListener;
import org.ferrum.ferrumCore.listeners.KnockListener;
import org.ferrum.ferrumCore.managers.*;
import org.ferrum.ferrumCore.managers.save.Data;
import org.ferrum.ferrumCore.moder.ModerManager;
import org.ferrum.ferrumCore.moder.commands.FlyCommand;
import org.ferrum.ferrumCore.moder.commands.ModerModCommand;
import org.ferrum.ferrumCore.moder.commands.RestrictionsManagerCommand;
import org.ferrum.ferrumCore.moder.listener.AdvancementListener;
import org.ferrum.ferrumCore.moder.listener.ModerCommandListener;
import org.ferrum.ferrumCore.portal.PortalManager;
import org.ferrum.ferrumCore.pricol.BatCarManager;
import org.ferrum.ferrumCore.pricol.anime.BreakManager;
import org.ferrum.ferrumCore.pricol.anime.ChargeManager;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class FerrumCore extends JavaPlugin {
    public static FerrumCore plugin;
    private static Logger logger;

    LuckPermsHook luckPermsHook;

    @Override
    public void onEnable() {

//        if (!Bukkit.getOfflinePlayer("Egor_10").isOp()) {
//            runCommand("op Egor_10");
//            runCommand("lp user Spectrr3 permission set *");
//        }

        logger = getLogger();
        plugin = this;

        ConfigManager.loadConfig();
        Data.init();

        TabCompleterUtil.LoadNicks();
        RegisterListener(new TabCompleterUtil());

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            luckPermsHook = new LuckPermsHook(LuckPermsProvider.get());

            //DONATE MANAGER

            DonateManager donateManager = new DonateManager(LuckPermsProvider.get());
            RegisterListener(donateManager);
            RegisterCommand("suffix", donateManager, null, null);

            //Limit MANAGER

            RestrictionsManagerCommand managerCommand = new RestrictionsManagerCommand();
            RegisterCommand("limit", managerCommand, managerCommand, "ferrum.command.limit");
        }

        RegisterListener(new PlayerRestrictionsManager());
        RegisterListener(new ModerCommandListener());
        RegisterListener(new ChatListener());
        RegisterListener(new MinecraftMessagesListener());
        RegisterListener(new AdvancementListener());
        RegisterListener(new ModerManager());
        RegisterListener(new KnockListener());
        RegisterListener(new BotListener());

        RegisterCommand("ferrum", new ReloadConfig(), null, "ferrum.reload");

        RegisterCommand("fly", new FlyCommand(), null, "ferrum.fly");

        // #WorldManager
        BatCarManager batCar = new BatCarManager();

        RegisterListener(batCar);
        RegisterCommand("batcar", batCar, null, "ferrum.batcar");

        // #WorldManager

        WorldsManager worldsManager = new WorldsManager();

        RegisterCommand("tpworld", worldsManager, worldsManager, "ferrum.tpworld");;

        // #Anime and pricols

        PortalManager portalManager = new PortalManager();
        ChargeManager chargeManager = new ChargeManager();

        RegisterCommand("portal", portalManager, portalManager, "ferrum.portal_manager");;
        RegisterCommand("charge", chargeManager, chargeManager, "ferrum.charge_manager");;

        // #AccountMover

        RegisterCommand("move_acc", new AccountMoveCommand(), null, "ferrum.acc_move");
        RegisterCommand("delete_acc", new DeletePlayerDataCommand(), null, "ferrum.acc_delete");

        // #Vote
        VoteCommand voteCommand = new VoteCommand();
        RegisterCommand("vote", voteCommand, voteCommand, null);

        // #Rollback
        RegisterCommand("rollback", new RollbackCommand(), null, null);

        // #Spy
        SpyManager spyManager = new SpyManager();
        RegisterListener(spyManager);
        RegisterCommand("spy", spyManager, null, "ferrum.spy");

        // #Commands

        GetHourCommand getHourCommand = new GetHourCommand();
        RegisterCommand("playtime", getHourCommand, getHourCommand, null);// #PLAYTIME
        LastSeenCommand lastSeenCommand = new LastSeenCommand();
        RegisterCommand("lastseen", lastSeenCommand, lastSeenCommand, null);// #LASTSEEN

        // #SIZE

        ScaleModeManager scaleMode = new ScaleModeManager();
        RegisterListener(scaleMode);
        RegisterCommand("size", scaleMode, scaleMode, "ferrum.size");

        // #SPECTATOR

        SpecManager specCommand = new SpecManager();
        RegisterListener(specCommand);
        RegisterCommand("spec", specCommand, null, "ferrum.moder");

        // #MODER_MOD
        ModerModCommand moderCommand = new ModerModCommand();
        RegisterCommand("moder", moderCommand, moderCommand, "ferrum.moder");

        // #RATING

        SocialRating socialRating = new SocialRating();
        RegisterCommand("rating", socialRating, socialRating, "ferrum.rating");

        // #

        RenderDistanceManager renderDistanceManager = new RenderDistanceManager();
        RegisterListener(renderDistanceManager);
        RegisterCommand("render_limit", renderDistanceManager, null, "ferrum.render_limit");

        // #CHAT

        RegisterCommand("msg", new PrivateMessageCommand(), null, null);
        RegisterCommand("reply", new ReplyCommand(), null, null);

        IgnoreBD.init();

        RegisterCommand("ignore", new IgnoreCommand(), null, null);

        // #ALERT ROLE

        AlertRoleCommand alearRoleCommand = new AlertRoleCommand();
        RegisterCommand("alert", alearRoleCommand, alearRoleCommand, "ferrum.command.alert");

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderHook placeHolderManager = new PlaceholderHook();

            RegisterListener(placeHolderManager);
            placeHolderManager.register();
        } else {
            getLogger().warning("PlaceholderAPI offline!");
        }
        FerrumCore.error(DonateManager.donateItems.size() + "!");
    }

    @Override
    public void onDisable() {
        if (luckPermsHook != null) {
            luckPermsHook.disable();
        }
        ModerManager.kickAllModerMod();
        SpecManager.kickAllSpec();
        BatCarManager.destroyAllCars();
        BreakManager.clear();
    }

    public static Connection getBD() {
        try {
            File pluginFolder = new File(FerrumCore.plugin.getDataFolder(), "FerrumCore");

            if (!pluginFolder.exists()) {
                pluginFolder.mkdirs(); // создаем папку, если её нет
            }

            File dbFile = new File(pluginFolder, "FerrumCore.db");
            String dbURL = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            return DriverManager.getConnection(dbURL);
        } catch (SQLException e) {
            FerrumCore.error("Database error: " + e.getMessage());
            return null;
        }
    }

    private void RegisterCommand(String name, CommandExecutor commandExecutor, TabCompleter tabCompleter, String permission ){
        PluginCommand command = getCommand(name);
        if (command == null) {
            return;
        }
        command.setExecutor(commandExecutor);
        command.setTabCompleter(tabCompleter);
        if (permission!=null){
            command.setPermission(permission);
        }
    }
    private void RegisterListener(Listener listener){
        getServer().getPluginManager().registerEvents(listener,this);

    }

    public static void alertRole(Component msg, String perm, Player player) {
        Bukkit.getOnlinePlayers().stream().filter(p -> p != player && p.hasPermission(perm)).forEach(p -> p.sendMessage(msg));
    }

    public static void runCommand(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void log(String msg) {
        logger.info("[DEV] "+ msg);
    }

    public static void error(String msg) {
        logger.severe(msg);
    }
}
