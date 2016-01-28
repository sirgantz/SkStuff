package me.TheBukor.SkStuff;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

import com.sk89q.worldedit.EditSession;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.coll.CollectionUtils;
import me.TheBukor.SkStuff.conditions.CondSelectionContains;
import me.TheBukor.SkStuff.effects.EffDrainLiquid;
import me.TheBukor.SkStuff.effects.EffDrawLineWE;
import me.TheBukor.SkStuff.effects.EffMakeCylinder;
import me.TheBukor.SkStuff.effects.EffMakePyramid;
import me.TheBukor.SkStuff.effects.EffMakeSphere;
import me.TheBukor.SkStuff.effects.EffMakeWalls;
import me.TheBukor.SkStuff.effects.EffNaturalize;
import me.TheBukor.SkStuff.effects.EffRememberChanges;
import me.TheBukor.SkStuff.effects.EffReplaceBlocksWE;
import me.TheBukor.SkStuff.effects.EffSetBlocksWE;
import me.TheBukor.SkStuff.effects.EffSimulateSnow;
import me.TheBukor.SkStuff.effects.EffToggleVanish;
import me.TheBukor.SkStuff.effects.EffUndoRedoSession;
import me.TheBukor.SkStuff.events.EvtWorldEditChange;
import me.TheBukor.SkStuff.events.WorldEditChangeHandler;
import me.TheBukor.SkStuff.expressions.ExprChangedBlocksSession;
import me.TheBukor.SkStuff.expressions.ExprEditSessionLimit;
import me.TheBukor.SkStuff.expressions.ExprFileNBT;
import me.TheBukor.SkStuff.expressions.ExprFireProof;
import me.TheBukor.SkStuff.expressions.ExprItemNBT;
import me.TheBukor.SkStuff.expressions.ExprNBTOf;
import me.TheBukor.SkStuff.expressions.ExprNewEditSession;
import me.TheBukor.SkStuff.expressions.ExprNoClip;
import me.TheBukor.SkStuff.expressions.ExprSchematicArea;
import me.TheBukor.SkStuff.expressions.ExprSelectionArea;
import me.TheBukor.SkStuff.expressions.ExprSelectionOfPlayer;
import me.TheBukor.SkStuff.expressions.ExprSelectionPos;
import me.TheBukor.SkStuff.expressions.ExprTagOf;
import me.TheBukor.SkStuff.expressions.ExprTimespanToNumber;
import me.TheBukor.SkStuff.expressions.ExprToLowerCase;
import me.TheBukor.SkStuff.expressions.ExprToUpperCase;
import me.TheBukor.SkStuff.expressions.ExprVanishState;
import me.TheBukor.SkStuff.expressions.ExprWordsToUpperCase;
import me.TheBukor.SkStuff.util.NBTUtil;
import me.TheBukor.SkStuff.util.ReflectionUtils;

public class SkStuff extends JavaPlugin {
	private int condAmount = 0;
	private int effAmount = 0;
	private int evtAmount = 0;
	private int exprAmount = 0;
	private int typeAmount = 0;

	private Class<?> nbtClass = ReflectionUtils.getNMSClass("NBTTagCompound");
	private Class<?> nbtParserClass = ReflectionUtils.getNMSClass("MojangsonParser");

	@SuppressWarnings("unchecked")
	public void onEnable() {
		if (Bukkit.getPluginManager().getPlugin("Skript") != null && Skript.isAcceptRegistrations()) {
			Skript.registerAddon(this);
			getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully enabled!");

			getLogger().info("Registering general non version specific stuff...");
			Skript.registerExpression(ExprToUpperCase.class, String.class, ExpressionType.SIMPLE, "%string% [converted] to [all] (cap[ital]s|upper[ ]case)", "convert %string% to [all] (cap[ital]s|upper[ ]case)", "capitalize [all] [char[acter]s (of|in)] %string%");
			Skript.registerExpression(ExprToLowerCase.class, String.class, ExpressionType.SIMPLE, "%string% [converted] to [all] lower[ ]case", "convert %string% to [all] lower[ ]case", "un[( |-)]capitalize [all] [char[acter]s (of|in)] %string%");
			Skript.registerExpression(ExprWordsToUpperCase.class, String.class, ExpressionType.SIMPLE, "(first|1st) (letter|char[acter]) (of|in) (each word|[all] words) (of|in) %string% [converted] to (cap[ital]s|upper[ ]case) (0¦|1¦ignoring [other] upper[ ]case [(char[acter]s|letters)])", "convert (first|1st) (letter|char[acter]) (of|in) (each word|[all] words) (of|in) %string% to (cap[ital]s|upper[ ]case) (0¦|1¦ignoring [other] upper[ ]case [(char[acter]s|letters)])", "capitalize (first|1st) (letter|char[acter]) (of|in) (each word|[all] words) (of|in) %string% (0¦|1¦ignoring [other] upper[ ]case [(char[acter]s|letters)])");
			Skript.registerExpression(ExprTimespanToNumber.class, Number.class, ExpressionType.SIMPLE, "%timespan% [converted] [in]to (0¦ticks|1¦sec[ond]s|2¦min[ute]s|3¦hours|4¦days)");
			exprAmount += 4;

			getLogger().info("Trying to register version specific stuff...");
			Skript.registerExpression(ExprNBTOf.class, Object.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
			Skript.registerExpression(ExprItemNBT.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
			Skript.registerExpression(ExprTagOf.class, Object.class, ExpressionType.PROPERTY, "[nbt[ ]]tag %string% of [[nbt] compound] %compound%");
			Skript.registerExpression(ExprFileNBT.class, Object.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
			Skript.registerExpression(ExprNoClip.class, Boolean.class, ExpressionType.PROPERTY, "no[( |-)]clip (state|mode) of %entity%", "%entity%'s no[( |-)]clip (state|mode)");
			Skript.registerExpression(ExprFireProof.class, Boolean.class, ExpressionType.PROPERTY, "fire[ ]proof (state|mode) of %entity%", "%entity%'s fire[ ]proof (state|mode)");

			Classes.registerClass(new ClassInfo<Object>((Class<Object>) nbtClass, "compound").user("((nbt)?( ?tag)?) ?compounds?").name("NBT Compound").changer(new Changer<Object>() {

				@Override
				@Nullable
				public Class<?>[] acceptChange(ChangeMode mode) {
					if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
						return CollectionUtils.array(String[].class);
					}
					return null;
				}

				@Override
				public void change(Object[] NBT, @Nullable Object[] delta, ChangeMode mode) {
					if (NBT[0].getClass().getName().contains("NBTTagCompound")) {
						if (!(delta[0] instanceof String))
							return;
						String newTags = (String) delta[0];
						if (mode == ChangeMode.ADD) {
							Object NBT1 = null;
							try {
								NBT1 = nbtParserClass.getMethod("parse", String.class).invoke(NBT1, newTags);
							} catch (Exception ex) {
								if (ex instanceof InvocationTargetException && ex.getCause().getClass().getName().contains("MojangsonParseException")) {
									getLogger().warning(Ansi.ansi().fgBright(Ansi.Color.RED) + "Error when parsing NBT - " + ex.getCause().getMessage() + Ansi.ansi().fgBright(Ansi.Color.DEFAULT));
									return;
								}
								ex.printStackTrace();
							}
							NBTUtil.addCompound(NBT[0], NBT1);
						} else if (mode == ChangeMode.REMOVE) {
							for (Object s : delta) {
								try {
									nbtClass.getMethod("remove", String.class).invoke(NBT[0], (String) s);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				}
			}).parser(new Parser<Object>() {

				@Override
				public String getVariableNamePattern() {
					return "nbt:{.+:.+}";
				}

				@Override
				@Nullable
				public Object parse(String rawNBT, ParseContext context) {
					if (rawNBT.startsWith("{") && rawNBT.contains(":") && rawNBT.endsWith("}")) {
						Object NBT = null;
						try {
							NBT = nbtParserClass.getMethod("parse", String.class).invoke(NBT, rawNBT);
						} catch (Exception ex) {
							if (ex instanceof InvocationTargetException && ex.getCause().getClass().getName().contains("MojangsonParseException")) {
								return null;
							}
							ex.printStackTrace();
						}
						if (NBT.toString().equals("{}") || NBT == null) {
							return null;
						}
						return NBT;
					}
					return null;
				}

				@Override
				public String toString(Object compound, int arg1) {
					return compound.toString();
				}

				@Override
				public String toVariableNameString(Object compound) {
					return "nbt:" + compound.toString();
				}
			}));
			typeAmount += 1;
			exprAmount += 6;
			if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
				getLogger().info("WorldEdit found! Registering WorldEdit stuff...");
				Skript.registerCondition(CondSelectionContains.class, "[(world[ ]edit|we)] selection of %player% (contains|has) %location%", "%player%'s [(world[ ]edit|we)] selection (contains|has) %location%", "[(world[ ]edit|we)] selection of %player% does(n't| not) (contain|have) %location%", "%player%'s [(world[ ]edit|we)] selection does(n't| not) (contain|have) %location%");
				Skript.registerEffect(EffDrawLineWE.class, "(create|draw|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) line from %location% to %location% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks% [with] thick[ness] %double%");
				Skript.registerEffect(EffUndoRedoSession.class, "(0¦undo|1¦redo) (change|edit)s (of|from) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffRememberChanges.class, "make %player% (remember|be able to undo) changes (of|from) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakeSphere.class, "(create|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) (ellipsoid|sphere) [centered] at %location% [with] radius [of] %double%,[ ]%double%(,[ ]| and )%double% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffSimulateSnow.class, "(simulate snow at|place snow over) %location% (in|within) [a] radius [of] %double% (using|with) [edit[ ]session] %editsession%", "make %location% snowy (in|within) [a] radius [of] %double% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakePyramid.class, "(create|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) pyramid at %location% [with] radius [of] %integer% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffDrainLiquid.class, "(drain|remove) [all] liquid[s] at %location% (in|within) [a] radius [of] %double% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffNaturalize.class, "naturalize ([cuboid] region|[all] blocks) (from|between) %location% (to|and) %location% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakeWalls.class, "(create|make) wall[s] from %location% to %location% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffSetBlocksWE.class, "set [all] blocks (from|between) %location% (to|and) %location% to %itemstacks% (using|with) [edit[ ]session] %editsession%");
				Skript.registerEffect(EffMakeCylinder.class, "(create|make) [a[n]] (0¦(no(n|t)(-| )hollow|filled|)|1¦hollow) cylinder at %location% [with] radius [of] %double%,[ ]%integer%(,[ ]| and )%double% (using|with) [edit[ ]session] %editsession% (using|with) [block[s]] %itemstacks%");
				Skript.registerEffect(EffReplaceBlocksWE.class, "replace [all] %itemstacks% (from|between) %location% (to|and) %location% with %itemstacks% (using|with) [edit[ ]session] %editsession%");
				Skript.registerExpression(ExprEditSessionLimit.class, Integer.class, ExpressionType.PROPERTY, "[block] limit [change] of [edit[ ]session] %editsession%");
				Skript.registerExpression(ExprChangedBlocksSession.class, Integer.class, ExpressionType.PROPERTY, "number of [all] changed blocks (in|of) [edit[ ]session] %editsession%");
				Skript.registerExpression(ExprNewEditSession.class, EditSession.class, ExpressionType.PROPERTY, "[new] edit[ ]session in [world] %world% [with] [max[imum]] [block] limit [change] [of] %integer%");
				Skript.registerExpression(ExprSelectionOfPlayer.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection");
				Skript.registerExpression(ExprSelectionPos.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] po(s|int)[ ](0¦1|1¦2) of %player%", "%player%'s [(world[ ]edit|we)] po(s|int)[ ](0¦1|1¦2)");
				Skript.registerExpression(ExprSelectionArea.class, Integer.class, ExpressionType.SIMPLE, "(0¦volume|1¦(x( |-)size|width)|2¦(y( |-)size|height)|3¦(z( |-)size|length)|4¦area) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection (0¦volume|1¦(x( |-)size|width)|2¦(y( |-)size|height)|3¦(z( |-)size|length)|4¦area)");
				Skript.registerExpression(ExprSchematicArea.class, Integer.class, ExpressionType.SIMPLE, "(0¦volume|1¦(x( |-)size|width)|2¦(y( |-)size|height)|3¦(z( |-)size|length)|4¦area) of schem[atic] [from] %string%");
				Classes.registerClass(new ClassInfo<EditSession>(EditSession.class, "editsession").name("Edit Session").user("edit ?sessions?"));
				try {
					Class.forName("com.sk89q.worldedit.extent.logging.AbstractLoggingExtent");
					new WorldEditChangeHandler();
					Skript.registerEvent("WorldEdit block change", SimpleEvent.class, EvtWorldEditChange.class, "world[ ]edit [block] (chang(e|ing)|edit[ing])");
					EventValues.registerEventValue(EvtWorldEditChange.class, Player.class, new Getter<Player, EvtWorldEditChange>() {
						@Override
						@Nullable
						public Player get(EvtWorldEditChange e) {
							return EvtWorldEditChange.getPlayer();
						}
					}, 0);
					EventValues.registerEventValue(EvtWorldEditChange.class, Block.class, new Getter<Block, EvtWorldEditChange>() {
						@Override
						@Nullable
						public Block get(EvtWorldEditChange e) {
							return EvtWorldEditChange.getBlock();
						}
					}, 0);
					evtAmount += 1;
				} catch (ClassNotFoundException ex) {
					Skript.error("Unable to register \"On WorldEdit block change\" event! You will need to upgrade to WorldEdit 6.0");
					return;
				}
				condAmount += 1;
				effAmount += 12;
				exprAmount += 7;
				typeAmount += 1;
			}
			if (Bukkit.getPluginManager().getPlugin("VanishNoPacket") != null) {
				getLogger().info("VanishNoPacket was found! Registering vanishing features...");
				Skript.registerEffect(EffToggleVanish.class, "toggle vanish (state|mode) of %player% (0¦|1¦(silently|quietly))", "toggle %player%'s vanish (state|mode) (0¦|1¦(silently|quietly))");
				Skript.registerExpression(ExprVanishState.class, Boolean.class, ExpressionType.PROPERTY, "vanish (state|mode) of %player%", "%player%'s vanish (state|mode)");
				effAmount += 1;
				exprAmount += 1;
			}
			getLogger().info("Everything ready! Loaded a total of " + condAmount + " conditions, " + effAmount + " effects, " + evtAmount + "events, " + exprAmount + " expressions and " + typeAmount + " types!");
		} else {
			getLogger().info("Unable to find Skript or Skript isn't accepting registrations, disabling SkStuff...");
			Bukkit.getPluginManager().disablePlugin(this);;
		}
	}

	public void onDisable() {
		getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully disabled.");
	}
}