package org.ferrum.ferrumCore.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.managers.DonateManager;
import org.ferrum.ferrumCore.moder.ModerManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LuckPermsHook implements ContextCalculator<Player> {

    public static LuckPerms luckPerms;
    private static boolean isLuckPerms = false;
    private static final Set<EventSubscription<?>> subscriptions = new HashSet<>();

    private static final String CONTEXT_MODERMOD = "modermod";
    public static LuckPermsHook luckPermsHook;

    public LuckPermsHook(LuckPerms luckPerms) {

        LuckPermsHook.luckPermsHook = this;
        LuckPermsHook.luckPerms = luckPerms;

        subscriptions.add(luckPerms.getEventBus().subscribe(net.luckperms.api.event.node.NodeAddEvent.class, event -> DonateManager.handle(event, event.getNode(), true)));
        subscriptions.add(luckPerms.getEventBus().subscribe(net.luckperms.api.event.node.NodeRemoveEvent.class, event -> DonateManager.handle(event, event.getNode(), false)));

        luckPerms.getContextManager().registerCalculator(this);
        isLuckPerms = true;

    }


    public static void disable() {
        subscriptions.forEach(net.luckperms.api.event.EventSubscription::close);
        luckPerms.getContextManager().unregisterCalculator(luckPermsHook);
    }

    public static boolean isLuckPerms() {
        return isLuckPerms;
    }

    @Override
    public void calculate(@NotNull Player player, ContextConsumer consumer) {

        consumer.accept(CONTEXT_MODERMOD, Boolean.toString(ModerManager.inModerMod(player)));
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        net.luckperms.api.context.ImmutableContextSet.Builder builder = net.luckperms.api.context.ImmutableContextSet.builder();

        builder.add(CONTEXT_MODERMOD, "true");
        builder.add(CONTEXT_MODERMOD, "false");

        return builder.build();
    }
}