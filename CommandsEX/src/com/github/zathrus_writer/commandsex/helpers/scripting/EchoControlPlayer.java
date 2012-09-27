package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 * A wrapper around a Player object that allows control of messages, or echoing to the screen.
 * @author timpittman
 */
public class EchoControlPlayer extends EchoControl implements Player {

		public EchoControlPlayer(Player wrappedPlayer) {
			super(wrappedPlayer);
		}

		@Override public void closeInventory() {
			((Player)wrappedSender).closeInventory();
		}

		@Override public GameMode getGameMode() {
			return ((Player)wrappedSender).getGameMode();
		}

		@Override public PlayerInventory getInventory() {
			return ((Player)wrappedSender).getInventory();
		}

		@Override public ItemStack getItemInHand() {
			return ((Player)wrappedSender).getItemInHand();
		}

		@Override public ItemStack getItemOnCursor() {
			return ((Player)wrappedSender).getItemOnCursor();
		}

		@Override public String getName() {
			return ((Player)wrappedSender).getName();
		}

		@Override public InventoryView getOpenInventory() {
			return ((Player)wrappedSender).getOpenInventory();
		}

		@Override public int getSleepTicks() {
			return ((Player)wrappedSender).getSleepTicks();
		}

		@Override public boolean isBlocking() {
			return ((Player)wrappedSender).isBlocking();
		}

		@Override public boolean isSleeping() {
			return ((Player)wrappedSender).isSleeping();
		}

		@Override public InventoryView openEnchanting(Location arg0, boolean arg1) {
			return ((Player)wrappedSender).openEnchanting(arg0, arg1);
		}

		@Override public InventoryView openInventory(Inventory arg0) {
			return ((Player)wrappedSender).openInventory(arg0);
		}

		@Override public void openInventory(InventoryView arg0) {
			((Player)wrappedSender).openInventory(arg0);
		}

		@Override public InventoryView openWorkbench(Location arg0, boolean arg1) {
			return ((Player)wrappedSender).openWorkbench(arg0, arg1);
		}

		@Override public void setGameMode(GameMode arg0) {
			((Player)wrappedSender).setGameMode(arg0);
		}

		@Override public void setItemInHand(ItemStack arg0) {
			((Player)wrappedSender).setItemInHand(arg0);
		}

		@Override public void setItemOnCursor(ItemStack arg0) {
			((Player)wrappedSender).setItemOnCursor(arg0);
		}

		@Override public boolean setWindowProperty(Property arg0, int arg1) {
			return ((Player)wrappedSender).setWindowProperty(arg0, arg1);
		}

		@Override public boolean addPotionEffect(PotionEffect arg0) {
			return ((Player)wrappedSender).addPotionEffect(arg0);
		}

		@Override public boolean addPotionEffect(PotionEffect arg0, boolean arg1) {
			return ((Player)wrappedSender).addPotionEffect(arg0, arg1);
		}

		@Override public boolean addPotionEffects(Collection<PotionEffect> arg0) {
			return ((Player)wrappedSender).addPotionEffects(arg0);
		}

		@Override public void damage(int arg0) {
			((Player)wrappedSender).damage(arg0);
		}

		@Override public void damage(int arg0, Entity arg1) {
			((Player)wrappedSender).damage(arg0, arg1);
		}

		@Override public Collection<PotionEffect> getActivePotionEffects() {
			return ((Player)wrappedSender).getActivePotionEffects();
		}

		@Override public double getEyeHeight() {
			return ((Player)wrappedSender).getEyeHeight();
		}

		@Override public double getEyeHeight(boolean arg0) {
			return ((Player)wrappedSender).getEyeHeight(arg0);
		}

		@Override public Location getEyeLocation() {
			return ((Player)wrappedSender).getEyeLocation();
		}

		@Override public int getHealth() {
			return ((Player)wrappedSender).getHealth();
		}

		@Override public Player getKiller() {
			return ((Player)wrappedSender).getKiller();
		}

		@Override public int getLastDamage() {
			return ((Player)wrappedSender).getLastDamage();
		}

		@Override public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0, int arg1) {
			return ((Player)wrappedSender).getLastTwoTargetBlocks(arg0, arg1);
		}

		@Override public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1) {
			return ((Player)wrappedSender).getLineOfSight(arg0, arg1);
		}

		@Override public int getMaxHealth() {
			return ((Player)wrappedSender).getMaxHealth();
		}

		@Override public int getMaximumAir() {
			return ((Player)wrappedSender).getMaximumAir();
		}

		@Override public int getMaximumNoDamageTicks() {
			return ((Player)wrappedSender).getMaximumNoDamageTicks();
		}

		@Override public int getNoDamageTicks() {
			return ((Player)wrappedSender).getNoDamageTicks();
		}

		@Override public int getRemainingAir() {
			return ((Player)wrappedSender).getRemainingAir();
		}

		@Override public Block getTargetBlock(HashSet<Byte> arg0, int arg1) {
			return ((Player)wrappedSender).getTargetBlock(arg0, arg1);
		}

		@Override public boolean hasPotionEffect(PotionEffectType arg0) {
			return ((Player)wrappedSender).hasPotionEffect(arg0);
		}

		@Override public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
			return ((Player)wrappedSender).launchProjectile(arg0);
		}

		@Override public void removePotionEffect(PotionEffectType arg0) {
			((Player)wrappedSender).removePotionEffect(arg0);
		}

		@Override public void setHealth(int arg0) {
			((Player)wrappedSender).setHealth(arg0);
		}

		@Override public void setLastDamage(int arg0) {
			((Player)wrappedSender).setLastDamage(arg0);
		}

		@Override public void setMaximumAir(int arg0) {
			((Player)wrappedSender).setMaximumAir(arg0);
		}

		@Override public void setMaximumNoDamageTicks(int arg0) {
			((Player)wrappedSender).setMaximumNoDamageTicks(arg0);
		}

		@Override public void setNoDamageTicks(int arg0) {
			((Player)wrappedSender).setNoDamageTicks(arg0);
		}

		@Override public void setRemainingAir(int arg0) {
			((Player)wrappedSender).setRemainingAir(arg0);
		}

		@SuppressWarnings("deprecation") @Override public Arrow shootArrow() {
			return ((Player)wrappedSender).shootArrow();
		}

		@SuppressWarnings("deprecation") @Override public Egg throwEgg() {
			return ((Player)wrappedSender).throwEgg();
		}

		@SuppressWarnings("deprecation") @Override public Snowball throwSnowball() {
			return ((Player)wrappedSender).throwSnowball();
		}

		@Override public boolean eject() {
			return ((Player)wrappedSender).eject();
		}

		@Override public int getEntityId() {
			return ((Player)wrappedSender).getEntityId();
		}

		@Override public float getFallDistance() {
			return ((Player)wrappedSender).getFallDistance();
		}

		@Override public int getFireTicks() {
			return ((Player)wrappedSender).getFireTicks();
		}

		@Override public EntityDamageEvent getLastDamageCause() {
			return ((Player)wrappedSender).getLastDamageCause();
		}

		@Override public Location getLocation() {
			return ((Player)wrappedSender).getLocation();
		}

		@Override public int getMaxFireTicks() {
			return ((Player)wrappedSender).getMaxFireTicks();
		}

		@Override public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
			return ((Player)wrappedSender).getNearbyEntities(arg0, arg1, arg2);
		}

		@Override public Entity getPassenger() {
			return ((Player)wrappedSender).getPassenger();
		}

		@Override public Server getServer() {
			return ((Player)wrappedSender).getServer();
		}

		@Override public int getTicksLived() {
			return ((Player)wrappedSender).getTicksLived();
		}

		@Override public EntityType getType() {
			return ((Player)wrappedSender).getType();
		}

		@Override public UUID getUniqueId() {
			return ((Player)wrappedSender).getUniqueId();
		}

		@Override public Entity getVehicle() {
			return ((Player)wrappedSender).getVehicle();
		}

		@Override public Vector getVelocity() {
			return ((Player)wrappedSender).getVelocity();
		}

		@Override public World getWorld() {
			return ((Player)wrappedSender).getWorld();
		}

		@Override public boolean isDead() {
			return ((Player)wrappedSender).isDead();
		}

		@Override public boolean isEmpty() {
			return ((Player)wrappedSender).isEmpty();
		}

		@Override public boolean isInsideVehicle() {
			return ((Player)wrappedSender).isInsideVehicle();
		}

		@Override public boolean leaveVehicle() {
			return ((Player)wrappedSender).leaveVehicle();
		}

		@Override public void playEffect(EntityEffect arg0) {
			((Player)wrappedSender).playEffect(arg0);
		}

		@Override public void remove() {
			((Player)wrappedSender).remove();
		}

		@Override public void setFallDistance(float arg0) {
			((Player)wrappedSender).setFallDistance(arg0);
		}

		@Override public void setFireTicks(int arg0) {
			((Player)wrappedSender).setFireTicks(arg0);
		}

		@Override public void setLastDamageCause(EntityDamageEvent arg0) {
			((Player)wrappedSender).setLastDamageCause(arg0);
		}

		@Override public boolean setPassenger(Entity arg0) {
			return ((Player)wrappedSender).setPassenger(arg0);
		}

		@Override public void setTicksLived(int arg0) {
			((Player)wrappedSender).setTicksLived(arg0);
		}

		@Override public void setVelocity(Vector arg0) {
			((Player)wrappedSender).setVelocity(arg0);
		}

		@Override public boolean teleport(Location arg0) {
			return ((Player)wrappedSender).teleport(arg0);
		}

		@Override public boolean teleport(Entity arg0) {
			return ((Player)wrappedSender).teleport(arg0);
		}

		@Override public boolean teleport(Location arg0, TeleportCause arg1) {
			return ((Player)wrappedSender).teleport(arg0, arg1);
		}

		@Override public boolean teleport(Entity arg0, TeleportCause arg1) {
			return ((Player)wrappedSender).teleport(arg0, arg1);
		}

		@Override public List<MetadataValue> getMetadata(String arg0) {
			return ((Player)wrappedSender).getMetadata(arg0);
		}

		@Override public boolean hasMetadata(String arg0) {
			return ((Player)wrappedSender).hasMetadata(arg0);
		}

		@Override public void removeMetadata(String arg0, Plugin arg1) {
			((Player)wrappedSender).removeMetadata(arg0, arg1);
		}

		@Override public void setMetadata(String arg0, MetadataValue arg1) {
			((Player)wrappedSender).setMetadata(arg0, arg1);
		}

		@Override public PermissionAttachment addAttachment(Plugin arg0) {
			return ((Player)wrappedSender).addAttachment(arg0);
		}

		@Override public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
			return ((Player)wrappedSender).addAttachment(arg0, arg1);
		}

		@Override public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
			return ((Player)wrappedSender).addAttachment(arg0, arg1, arg2);
		}

		@Override public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2,
				int arg3) {
			return ((Player)wrappedSender).addAttachment(arg0, arg1, arg2, arg3);
		}

		@Override public Set<PermissionAttachmentInfo> getEffectivePermissions() {
			return ((Player)wrappedSender).getEffectivePermissions();
		}

		@Override public boolean hasPermission(String arg0) {
			return ((Player)wrappedSender).hasPermission(arg0);
		}

		@Override public boolean hasPermission(Permission arg0) {
			return ((Player)wrappedSender).hasPermission(arg0);
		}

		@Override public boolean isPermissionSet(String arg0) {
			return ((Player)wrappedSender).isPermissionSet(arg0);
		}

		@Override public boolean isPermissionSet(Permission arg0) {
			return ((Player)wrappedSender).isPermissionSet(arg0);
		}

		@Override public void recalculatePermissions() {
			((Player)wrappedSender).recalculatePermissions();
		}

		@Override public void removeAttachment(PermissionAttachment arg0) {
			((Player)wrappedSender).removeAttachment(arg0);
		}

		@Override public boolean isOp() {
			return ((Player)wrappedSender).isOp();
		}

		@Override public void setOp(boolean arg0) {
			((Player)wrappedSender).setOp(arg0);
		}

		@Override public void abandonConversation(Conversation arg0) {
			((Player)wrappedSender).abandonConversation(arg0);
		}

		@Override public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
			((Player)wrappedSender).abandonConversation(arg0, arg1);
		}

		@Override public void acceptConversationInput(String arg0) {
			((Player)wrappedSender).acceptConversationInput(arg0);
		}

		@Override public boolean beginConversation(Conversation arg0) {
			return ((Player)wrappedSender).beginConversation(arg0);
		}

		@Override public boolean isConversing() {
			return ((Player)wrappedSender).isConversing();
		}

		@Override public long getFirstPlayed() {
			return ((Player)wrappedSender).getFirstPlayed();
		}

		@Override public long getLastPlayed() {
			return ((Player)wrappedSender).getLastPlayed();
		}

		@Override public Player getPlayer() {
			return ((Player)wrappedSender).getPlayer();
		}

		@Override public boolean hasPlayedBefore() {
			return ((Player)wrappedSender).hasPlayedBefore();
		}

		@Override public boolean isBanned() {
			return ((Player)wrappedSender).isBanned();
		}

		@Override public boolean isOnline() {
			return ((Player)wrappedSender).isOnline();
		}

		@Override public boolean isWhitelisted() {
			return ((Player)wrappedSender).isWhitelisted();
		}

		@Override public void setBanned(boolean arg0) {
			((Player)wrappedSender).setBanned(arg0);
		}

		@Override public void setWhitelisted(boolean arg0) {
			((Player)wrappedSender).setWhitelisted(arg0);
		}

		@Override public Map<String, Object> serialize() {
			return ((Player)wrappedSender).serialize();
		}

		@Override public Set<String> getListeningPluginChannels() {
			return ((Player)wrappedSender).getListeningPluginChannels();
		}

		@Override public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
			((Player)wrappedSender).sendPluginMessage(arg0, arg1, arg2);
		}

		@Override public void awardAchievement(Achievement arg0) {
			((Player)wrappedSender).awardAchievement(arg0);
		}

		@Override public boolean canSee(Player arg0) {
			return ((Player)wrappedSender).canSee(arg0);
		}

		@Override public void chat(String arg0) {
			((Player)wrappedSender).chat(arg0);
		}

		@Override public InetSocketAddress getAddress() {
			return ((Player)wrappedSender).getAddress();
		}

		@Override public boolean getAllowFlight() {
			return ((Player)wrappedSender).getAllowFlight();
		}

		@Override public Location getBedSpawnLocation() {
			return ((Player)wrappedSender).getBedSpawnLocation();
		}

		@Override public Location getCompassTarget() {
			return ((Player)wrappedSender).getCompassTarget();
		}

		@Override public String getDisplayName() {
			return ((Player)wrappedSender).getDisplayName();
		}

		@Override public float getExhaustion() {
			return ((Player)wrappedSender).getExhaustion();
		}

		@Override public float getExp() {
			return ((Player)wrappedSender).getExp();
		}

		@Override public int getFoodLevel() {
			return ((Player)wrappedSender).getFoodLevel();
		}

		@Override public int getLevel() {
			return ((Player)wrappedSender).getLevel();
		}

		@Override public String getPlayerListName() {
			return ((Player)wrappedSender).getPlayerListName();
		}

		@Override public long getPlayerTime() {
			return ((Player)wrappedSender).getPlayerTime();
		}

		@Override public long getPlayerTimeOffset() {
			return ((Player)wrappedSender).getPlayerTimeOffset();
		}

		@Override public float getSaturation() {
			return ((Player)wrappedSender).getSaturation();
		}

		@Override public int getTotalExperience() {
			return ((Player)wrappedSender).getTotalExperience();
		}

		@Override public void giveExp(int arg0) {
			((Player)wrappedSender).giveExp(arg0);
		}

		@Override public void hidePlayer(Player arg0) {
			((Player)wrappedSender).hidePlayer(arg0);
		}

		@Override public void incrementStatistic(Statistic arg0) {
			((Player)wrappedSender).incrementStatistic(arg0);
		}

		@Override public void incrementStatistic(Statistic arg0, int arg1) {
			((Player)wrappedSender).incrementStatistic(arg0, arg1);
		}

		@Override public void incrementStatistic(Statistic arg0, Material arg1) {
			((Player)wrappedSender).incrementStatistic(arg0, arg1);
		}

		@Override public void incrementStatistic(Statistic arg0, Material arg1, int arg2) {
			((Player)wrappedSender).incrementStatistic(arg0, arg1, arg2);
		}

		@Override public boolean isFlying() {
			return ((Player)wrappedSender).isFlying();
		}

		@Override public boolean isPlayerTimeRelative() {
			return ((Player)wrappedSender).isPlayerTimeRelative();
		}

		@Override public boolean isSleepingIgnored() {
			return ((Player)wrappedSender).isSleepingIgnored();
		}

		@Override public boolean isSneaking() {
			return ((Player)wrappedSender).isSneaking();
		}

		@Override public boolean isSprinting() {
			return ((Player)wrappedSender).isSprinting();
		}

		@Override public void kickPlayer(String arg0) {
			((Player)wrappedSender).kickPlayer(arg0);
		}

		@Override public void loadData() {
			((Player)wrappedSender).loadData();
		}

		@Override public boolean performCommand(String arg0) {
			return ((Player)wrappedSender).performCommand(arg0);
		}

		@Override public void playEffect(Location arg0, Effect arg1, int arg2) {
			((Player)wrappedSender).playEffect(arg0, arg1, arg2);
		}

		@Override public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
			((Player)wrappedSender).playEffect(arg0, arg1, arg2);
		}

		@Override public void playNote(Location arg0, byte arg1, byte arg2) {
			((Player)wrappedSender).playNote(arg0, arg1, arg2);
		}

		@Override public void playNote(Location arg0, Instrument arg1, Note arg2) {
			((Player)wrappedSender).playNote(arg0, arg1, arg2);
		}

		@Override public void resetPlayerTime() {
			((Player)wrappedSender).resetPlayerTime();
		}

		@Override public void saveData() {
			((Player)wrappedSender).saveData();
		}

		@Override public void sendBlockChange(Location arg0, Material arg1, byte arg2) {
			((Player)wrappedSender).sendBlockChange(arg0, arg1, arg2);
		}

		@Override public void sendBlockChange(Location arg0, int arg1, byte arg2) {
			((Player)wrappedSender).sendBlockChange(arg0, arg1, arg2);
		}

		@Override public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3,
				byte[] arg4) {
			return ((Player)wrappedSender).sendChunkChange(arg0, arg1, arg2, arg3, arg4);
		}

		@Override public void sendMap(MapView arg0) {
			((Player)wrappedSender).sendMap(arg0);
		}

		@Override public void sendRawMessage(String arg0) {
			((Player)wrappedSender).sendRawMessage(arg0);
		}

		@Override public void setAllowFlight(boolean arg0) {
			((Player)wrappedSender).setAllowFlight(arg0);
		}

		@Override public void setBedSpawnLocation(Location arg0) {
			((Player)wrappedSender).setBedSpawnLocation(arg0);
		}

		@Override public void setCompassTarget(Location arg0) {
			((Player)wrappedSender).setCompassTarget(arg0);
		}

		@Override public void setDisplayName(String arg0) {
			((Player)wrappedSender).setDisplayName(arg0);
		}

		@Override public void setExhaustion(float arg0) {
			((Player)wrappedSender).setExhaustion(arg0);
		}

		@Override public void setExp(float arg0) {
			((Player)wrappedSender).setExp(arg0);
		}

		@Override public void setFlying(boolean arg0) {
			((Player)wrappedSender).setFlying(arg0);
		}

		@Override public void setFoodLevel(int arg0) {
			((Player)wrappedSender).setFoodLevel(arg0);
		}

		@Override public void setLevel(int arg0) {
			((Player)wrappedSender).setLevel(arg0);
		}

		@Override public void setPlayerListName(String arg0) {
			((Player)wrappedSender).setPlayerListName(arg0);
		}

		@Override public void setPlayerTime(long time, boolean relative) {
			((Player)wrappedSender).setPlayerTime(time, relative);
		}

		@Override public void setSaturation(float value) {
			((Player)wrappedSender).setSaturation(value);
		}

		@Override public void setSleepingIgnored(boolean isSleeping) {
			((Player)wrappedSender).setSleepingIgnored(isSleeping);
		}

		@Override public void setSneaking(boolean sneak) {
			((Player)wrappedSender).setSneaking(sneak);
		}

		@Override public void setSprinting(boolean sprinting) {
			((Player)wrappedSender).setSprinting(sprinting);
		}

		@Override public void setTotalExperience(int exp) {
			((Player)wrappedSender).setTotalExperience(exp);
		}

		@Override public void showPlayer(Player player) {
			((Player)wrappedSender).showPlayer(player);
		}

		@SuppressWarnings("deprecation") @Override public void updateInventory() {
			((Player)wrappedSender).updateInventory();
		}

		@Override public int getExpToLevel() {
			return ((Player)wrappedSender).getExpToLevel();
		}

		@Override public boolean hasLineOfSight(Entity arg0) {
			return ((Player)wrappedSender).hasLineOfSight(arg0);
		}

		@Override public boolean isValid() {
			return ((Player)wrappedSender).isValid();
		}

		@Override
		public float getFlySpeed() {
			return ((Player)wrappedSender).getFlySpeed();
		}

		@Override
		public float getWalkSpeed() {
			return ((Player)wrappedSender).getWalkSpeed();
		}

		@Override
		public void setFlySpeed(float arg0) throws IllegalArgumentException {
			((Player)wrappedSender).setFlySpeed(arg0);
		}

		@Override
		public void setWalkSpeed(float arg0) throws IllegalArgumentException {
			((Player)wrappedSender).setWalkSpeed(arg0);
		}

		@Override
		public Inventory getEnderChest() {
			return ((Player)wrappedSender).getEnderChest();
		}

		@Override
		public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
			((Player) wrappedSender).playSound(arg0, arg1, arg2, arg3);
		}
}
