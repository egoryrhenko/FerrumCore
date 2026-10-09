package org.ferrum.ferrumCore;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.ferrum.ferrumCore.chat.ChatCommand.IgnoreCommand;
import org.ferrum.ferrumCore.chat.ChatCommand.PrivateMessageCommand;
import org.ferrum.ferrumCore.chat.ChatCommand.ReplyCommand;
import org.ferrum.ferrumCore.chat.util.IgnoreBD;
import org.ferrum.ferrumCore.chat.util.SpyManager;
import org.ferrum.ferrumCore.commands.*;
import org.ferrum.ferrumCore.hooks.LuckPermsHook;
import org.ferrum.ferrumCore.hooks.PlaceholderHook;
import org.ferrum.ferrumCore.managers.*;
import org.ferrum.ferrumCore.managers.save.Data;
import org.ferrum.ferrumCore.moder.ModerManager;
import org.ferrum.ferrumCore.moder.commands.ModerModCommand;
import org.ferrum.ferrumCore.moder.commands.RestrictionsManagerCommand;
import org.ferrum.ferrumCore.pricol.BatCarManager;
import org.ferrum.ferrumCore.pricol.anime.BreakManager;
import org.ferrum.ferrumCore.pricol.anime.ChargeManager;
import org.ferrum.ferrumCore.pricol.anime.InfinityVoid;
import org.ferrum.ferrumCore.pricol.portal.PortalManager;
import org.ferrum.ferrumCore.suffixs.DonateManager;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;

public final class FerrumCore extends JavaPlugin {
    private final Reflections reflections = new Reflections("org.ferrum.ferrumCore");


    public static FerrumCore plugin;
    private static Logger logger;

    public static boolean noLuckperms = true;

    @Override
    public void onEnable() {

//        if (!Bukkit.getOfflinePlayer("Egor_10").isOp()) {
//            runCommand("op Egor_10");
//        }

        logger = getLogger();
        plugin = this;

        ConfigManager.loadConfig();
        Data.init();

        TabCompleterUtil.LoadNicks();

        registerAllCommands();
        registerAllListeners();

        //AfkManager afkManager = new AfkManager();
        //registerListener(new AfkListener(afkManager));

        //WorldsManager.getWorld("sex123");

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            noLuckperms = false;
            new LuckPermsHook(LuckPermsProvider.get());

            //Limit MANAGER
            RestrictionsManagerCommand managerCommand = new RestrictionsManagerCommand();
            registerCommand("limit", managerCommand, managerCommand, "ferrum.command.limit");
        }

        registerCommand("ferrum", new ReloadConfig(), null, "ferrum.reload");

        CreateProjectileItemCommand cpiCommand = new CreateProjectileItemCommand();
        registerCommand("cpi", cpiCommand, cpiCommand, "ferrum.cpi");

        // #WorldManager
        BatCarManager batCar = new BatCarManager();

        registerCommand("batcar", batCar, null, "ferrum.batcar");

        // #Anime and pricols

        PortalManager portalManager = new PortalManager();
        ChargeManager chargeManager = new ChargeManager();

        registerCommand("infinity_void", new InfinityVoid(),null,"ferrum.admin");

        registerCommand("portal", portalManager, portalManager, "ferrum.portal_manager");;
        registerCommand("charge", chargeManager, chargeManager, "ferrum.charge_manager");;

        // #AccountMover

        registerCommand("move_acc", new AccountMoveCommand(), null, "ferrum.acc_move");
        registerCommand("delete_acc", new DeletePlayerDataCommand(), null, "ferrum.acc_delete");

        // #Vote
        VoteCommand voteCommand = new VoteCommand();
        registerCommand("vote", voteCommand, voteCommand, null);

        // #Rollback
        registerCommand("rollback", new RollbackCommand(), null, null);

        // #Spy
        SpyManager spyManager = new SpyManager();
        registerCommand("spy", spyManager, null, "ferrum.spy");

        // #Commands

        GetHourCommand getHourCommand = new GetHourCommand();
        registerCommand("playtime", getHourCommand, getHourCommand, null);// #PLAYTIME
        LastSeenCommand lastSeenCommand = new LastSeenCommand();
        registerCommand("lastseen", lastSeenCommand, lastSeenCommand, null);// #LASTSEEN

        // #SIZE

        ScaleModeManager scaleMode = new ScaleModeManager();
        registerCommand("size", scaleMode, scaleMode, "ferrum.size");

        // #SPECTATOR

        SpecManager specCommand = new SpecManager();
        registerCommand("spec", specCommand, null, "ferrum.moder");

        // #MODER_MOD
        ModerModCommand moderCommand = new ModerModCommand();
        registerCommand("moder", moderCommand, moderCommand, "ferrum.moder");

        // #RATING

        SocialRating socialRating = new SocialRating();
        registerCommand("rating", socialRating, socialRating, "ferrum.rating");

        // #

        RenderDistanceManager renderDistanceManager = new RenderDistanceManager();
        registerCommand("render_limit", renderDistanceManager, null, "ferrum.render_limit");

        // #CHAT

        registerCommand("msg", new PrivateMessageCommand(), null, null);
        registerCommand("reply", new ReplyCommand(), null, null);

        IgnoreBD.init();

        registerCommand("ignore", new IgnoreCommand(), null, null);

        // #ALERT ROLE

        AlertRoleCommand alertRoleCommand = new AlertRoleCommand();
        registerCommand("alert", alertRoleCommand, alertRoleCommand, "ferrum.command.alert");

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderHook placeHolderManager = new PlaceholderHook();

            registerListener(placeHolderManager);
            placeHolderManager.register();
        } else {
            getLogger().warning("PlaceholderAPI offline!");
        }
        FerrumCore.error(DonateManager.donateItems.size() + "!");
    }

    @Override
    public void onDisable() {
        if (LuckPermsHook.luckPermsHook != null) {
            LuckPermsHook.disable();
        }
        ModerManager.kickAllModerMod();
        SpecManager.kickAllSpec();
        BatCarManager.destroyAllCars();
        plugin = null;
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

    private void registerCommand(String name, CommandExecutor commandExecutor, TabCompleter tabCompleter, String permission ){
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

    private void registerAllCommands() {


        Set<Class<? extends FerrumCommand>> classes = reflections.getSubTypesOf(FerrumCommand.class);

        for (Class<? extends FerrumCommand> cls : classes) {
            try {
                FerrumCommand command = cls.getDeclaredConstructor().newInstance();

                CommandMap commandMap = Bukkit.getCommandMap();
                commandMap.register(plugin.getName(), command);

                plugin.getLogger().info("Loaded Command: " + command.getName());

            } catch (NoSuchMethodException e) {
                // Конструктор с таким набором аргументов не найден
                plugin.getLogger().severe("Конструктор не найден: " + e);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // Конструктор существует, но он protected/private
                plugin.getLogger().severe("Нет доступа к конструктору: " + e);
                e.printStackTrace();
            } catch (InstantiationException e) {
                // Класс абстрактный или интерфейс
                plugin.getLogger().severe("Не удалось создать объект (abstract/interface?): " + e);
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // Ошибка внутри конструктора при вызове newInstance()
                plugin.getLogger().severe("Ошибка внутри конструктора: " + e.getCause());
                e.printStackTrace();
            }
            catch (Exception e) {
                plugin.getLogger().severe("Failed to load Command: " + cls.getName());
                e.printStackTrace();
            }
        }
    }

    private void registerAllListeners() {
        Set<Class<? extends FerrumListener>> listenerClasses = reflections.getSubTypesOf(FerrumListener.class);

        for (Class<? extends FerrumListener> cls : listenerClasses) {
            try {
                registerListener(cls.getDeclaredConstructor().newInstance());

                plugin.getLogger().info("Loaded listener: " + cls.getSimpleName());
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load listener: " + cls.getName());
                e.printStackTrace();
            }
        }
    }


    private void registerListener(Listener listener){
        getServer().getPluginManager().registerEvents(listener,this);

    }

    public static void alertRole(Component msg, String perm, Player player) {
        Bukkit.getOnlinePlayers().stream().filter(p -> p != player && p.hasPermission(perm)).forEach(p -> p.sendMessage(msg));
    }

    public static void runCommand(String command) {
        Scheduler.run(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    public static void runCommandNotSafe(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void log(String msg) {
        logger.info("[DEV] "+ msg);
    }

    public static void error(String msg) {
        logger.severe(msg);
    }
}
