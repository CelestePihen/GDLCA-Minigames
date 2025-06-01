package fr.cel.gameapi.manager.npc;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.UUID;

@Getter
public class NPC {

    // TODO https://github.com/FancyMcPlugins/FancyNpcs/blob/main/implementation_1_21_4/src/main/java/de/oliver/fancynpcs/v1_21_4/Npc_1_21_4.java#L373

    private final UUID uuid;
    private final String name;
    private final String displayName;
    private final Location location;

    private final Skin skin;

    private ServerPlayer serverPlayer;
    private GameProfile gameProfile;

    public NPC(String name, String displayName, Location location, Skin skin) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.displayName = displayName;
        this.location = location;
        this.skin = skin;
    }

    public void create() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        gameProfile = new GameProfile(uuid, displayName);

        serverPlayer = new ServerPlayer(server, level, gameProfile, ClientInformation.createDefault());
    }

    public void spawn(Player player) {
        if (!location.getWorld().getName().equalsIgnoreCase(serverPlayer.level().getWorld().getName())) return;

        if (skin != null) {
            serverPlayer.getGameProfile().getProperties().replaceValues("textures", ImmutableSet.of(new Property("textures", skin.value(), skin.signature())));
        }

        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);

        ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(actions, serverPlayer.getServer().getPlayerList().getPlayers());
        serverPlayer.connection.send(playerInfoPacket);

        serverPlayer.setPos(location.getX(), location.getY(), location.getZ());

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                serverPlayer.getId(),
                serverPlayer.getUUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getPitch(),
                location.getYaw(),
                serverPlayer.getType(),
                0,
                Vec3.ZERO,
                location.getYaw()
        );
        serverPlayer.connection.send(addEntityPacket);

        update(player);
    }

    public void remove(Player player) {

    }

    private void update(Player player) {

    }

    private ClientboundPlayerInfoUpdatePacket.Entry getEntry() {
        return new ClientboundPlayerInfoUpdatePacket.Entry(
                uuid, gameProfile, false, 0,
                serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.getTabListDisplayName(),
                true, -1, Optionull.map(serverPlayer.getChatSession(), RemoteChatSession::asData)
        );
    }

}