package me.TheBukor.SkStuff;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.EditSession;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
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
import me.TheBukor.SkStuff.effects.EffUndoRedoSession;
import me.TheBukor.SkStuff.events.EvtWorldEditChange;
import me.TheBukor.SkStuff.events.WorldEditChangeHandler;
import me.TheBukor.SkStuff.expressions.ExprAreaOfSchematic;
import me.TheBukor.SkStuff.expressions.ExprAreaOfSelection;
import me.TheBukor.SkStuff.expressions.ExprChangedBlocksSession;
import me.TheBukor.SkStuff.expressions.ExprEditSessionLimit;
import me.TheBukor.SkStuff.expressions.ExprFileNBTv1_8_R1;
import me.TheBukor.SkStuff.expressions.ExprFileNBTv1_8_R2;
import me.TheBukor.SkStuff.expressions.ExprFileNBTv1_8_R3;
import me.TheBukor.SkStuff.expressions.ExprHeightOfSchematic;
import me.TheBukor.SkStuff.expressions.ExprHeightOfSelection;
import me.TheBukor.SkStuff.expressions.ExprItemNBTv1_7_R1;
import me.TheBukor.SkStuff.expressions.ExprItemNBTv1_7_R2;
import me.TheBukor.SkStuff.expressions.ExprItemNBTv1_7_R3;
import me.TheBukor.SkStuff.expressions.ExprItemNBTv1_7_R4;
import me.TheBukor.SkStuff.expressions.ExprItemNBTv1_8_R1;
import me.TheBukor.SkStuff.expressions.ExprItemNBTv1_8_R2;
import me.TheBukor.SkStuff.expressions.ExprItemNBTv1_8_R3;
import me.TheBukor.SkStuff.expressions.ExprLengthOfSchematic;
import me.TheBukor.SkStuff.expressions.ExprLengthOfSelection;
import me.TheBukor.SkStuff.expressions.ExprNBTv1_7_R1;
import me.TheBukor.SkStuff.expressions.ExprNBTv1_7_R2;
import me.TheBukor.SkStuff.expressions.ExprNBTv1_7_R3;
import me.TheBukor.SkStuff.expressions.ExprNBTv1_7_R4;
import me.TheBukor.SkStuff.expressions.ExprNBTv1_8_R1;
import me.TheBukor.SkStuff.expressions.ExprNBTv1_8_R2;
import me.TheBukor.SkStuff.expressions.ExprNBTv1_8_R3;
import me.TheBukor.SkStuff.expressions.ExprNewEditSession;
import me.TheBukor.SkStuff.expressions.ExprSelectionOfPlayer;
import me.TheBukor.SkStuff.expressions.ExprSelectionPos1;
import me.TheBukor.SkStuff.expressions.ExprSelectionPos2;
import me.TheBukor.SkStuff.expressions.ExprTagOfv1_7_R1;
import me.TheBukor.SkStuff.expressions.ExprTagOfv1_7_R2;
import me.TheBukor.SkStuff.expressions.ExprTagOfv1_7_R3;
import me.TheBukor.SkStuff.expressions.ExprTagOfv1_7_R4;
import me.TheBukor.SkStuff.expressions.ExprTagOfv1_8_R1;
import me.TheBukor.SkStuff.expressions.ExprTagOfv1_8_R2;
import me.TheBukor.SkStuff.expressions.ExprTagOfv1_8_R3;
import me.TheBukor.SkStuff.expressions.ExprVolumeOfSchematic;
import me.TheBukor.SkStuff.expressions.ExprVolumeOfSelection;
import me.TheBukor.SkStuff.expressions.ExprWidthOfSchematic;
import me.TheBukor.SkStuff.expressions.ExprWidthOfSelection;
import net.minecraft.server.v1_8_R1.MojangsonParser;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R2.MojangsonParseException;

public class SkStuff extends JavaPlugin {
	private int condAmount = 0;
	private int exprAmount = 0;
	private int typeAmount = 0;
	private int effAmount = 0;
	private boolean evtWE = false;

	public void onEnable() {
		if (Bukkit.getPluginManager().getPlugin("Skript") != null) {
			Skript.registerAddon(this);
			getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully enabled!");
			if (Bukkit.getVersion().contains("(MC: 1.7.2)")) {
				getLogger().info("Successfully found 1.7.2! Registering version specific expressions...");
				exprAmount += 3;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_7_R1.class, net.minecraft.server.v1_7_R1.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_7_R1.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_7_R1.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				//WARNING! HIGHLY EXPERIMENTAL, IT CAN CORRUPT WORLDS AT CURRENT VERSION, USE AT YOUR OWN RISK!
				//Skript.registerExpression(ExprFileNBTv1_7_R1.class, net.minecraft.server.v1_7_R1.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_7_R1.NBTTagCompound>(net.minecraft.server.v1_7_R1.NBTTagCompound.class, "compound").name("NBT Tag Compound").user("((nbt)?( ?tag)?) ?compounds?").parser(new Parser<net.minecraft.server.v1_7_R1.NBTTagCompound>() {
					@Override
					public String getVariableNamePattern() {
						return "{.+:.+}";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_7_R1.NBTTagCompound parse(String s, ParseContext context) {
						if (s.startsWith("{")) {
							net.minecraft.server.v1_7_R1.NBTTagCompound NBT = new net.minecraft.server.v1_7_R1.NBTTagCompound();
							net.minecraft.server.v1_7_R1.NBTTagCompound NBT1 = (net.minecraft.server.v1_7_R1.NBTTagCompound) net.minecraft.server.v1_7_R1.MojangsonParser.a(s);
							NBT.set("", NBT1);
							if (NBT.isEmpty() || NBT == null) {
								return null;
							}
							return NBT;
						}
						return null;
					}

					@Override
					public String toString(net.minecraft.server.v1_7_R1.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_7_R1.NBTTagCompound compound) {
						return compound.toString();
					}
				}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.7.5)")) {
				getLogger().info("Successfully found 1.7.5! Registering version specific expressions...");
				exprAmount += 3;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_7_R2.class, net.minecraft.server.v1_7_R2.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_7_R2.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_7_R2.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				//WARNING! HIGHLY EXPERIMENTAL, IT CAN CORRUPT WORLDS AT CURRENT VERSION, USE AT YOUR OWN RISK!
				//Skript.registerExpression(ExprFileNBTv1_7_R2.class, net.minecraft.server.v1_7_R2.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_7_R2.NBTTagCompound>(net.minecraft.server.v1_7_R2.NBTTagCompound.class, "compound").name("NBT Tag Compound").user("((nbt)?( ?tag)?) ?compounds?").parser(new Parser<net.minecraft.server.v1_7_R2.NBTTagCompound>() {
					@Override
					public String getVariableNamePattern() {
						return "nbt:{.+:.+}";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_7_R2.NBTTagCompound parse(String s, ParseContext context) {
						if (s.startsWith("{")) {
							net.minecraft.server.v1_7_R2.NBTTagCompound NBT = new net.minecraft.server.v1_7_R2.NBTTagCompound();
							net.minecraft.server.v1_7_R2.NBTTagCompound NBT1 = (net.minecraft.server.v1_7_R2.NBTTagCompound) net.minecraft.server.v1_7_R2.MojangsonParser.parse(s);
							NBT.set("", NBT1);
							if (NBT.isEmpty() || NBT == null) {
								return null;
							}
							return NBT;
						}
						return null;
					}

					@Override
					public String toString(net.minecraft.server.v1_7_R2.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_7_R2.NBTTagCompound compound) {
						return "nbt:" + compound.toString();
					}
				}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.7.9)")) {
				getLogger().info("Successfully found 1.7.9! Registering version specific expressions...");
				exprAmount += 3;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_7_R3.class, net.minecraft.server.v1_7_R3.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_7_R3.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_7_R3.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				//WARNING! HIGHLY EXPERIMENTAL, IT CAN CORRUPT WORLDS AT CURRENT VERSION, USE AT YOUR OWN RISK!
				//Skript.registerExpression(ExprFileNBTv1_7_R3.class, net.minecraft.server.v1_7_R3.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_7_R3.NBTTagCompound>(net.minecraft.server.v1_7_R3.NBTTagCompound.class, "compound").name("NBT Tag Compound").user("((nbt)?( ?tag)?) ?compounds?").parser(new Parser<net.minecraft.server.v1_7_R3.NBTTagCompound>() {
					@Override
					public String getVariableNamePattern() {
						return "nbt:{.+:.+}";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_7_R3.NBTTagCompound parse(String s, ParseContext context) {
						if (s.startsWith("{")) {
							net.minecraft.server.v1_7_R3.NBTTagCompound NBT = new net.minecraft.server.v1_7_R3.NBTTagCompound();
							net.minecraft.server.v1_7_R3.NBTTagCompound NBT1 = (net.minecraft.server.v1_7_R3.NBTTagCompound) net.minecraft.server.v1_7_R3.MojangsonParser.parse(s);
							NBT.set("", NBT1);
							if (NBT.isEmpty() || NBT == null) {
								return null;
							}
							return NBT;
						}
						return null;
					}

					@Override
					public String toString(net.minecraft.server.v1_7_R3.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_7_R3.NBTTagCompound compound) {
						return "nbt:" + compound.toString();
					}
				}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.7.10)")) {
				getLogger().info("Successfully found 1.7.10! Registering version specific expressions...");
				exprAmount += 3;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_7_R4.class, net.minecraft.server.v1_7_R4.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_7_R4.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_7_R4.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				//WARNING! HIGHLY EXPERIMENTAL, IT CAN CORRUPT WORLDS AT CURRENT VERSION, USE AT YOUR OWN RISK!
				//Skript.registerExpression(ExprFileNBTv1_7_R4.class, net.minecraft.server.v1_7_R4.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_7_R4.NBTTagCompound>(net.minecraft.server.v1_7_R4.NBTTagCompound.class, "compound").user("((nbt)?( ?tag)?) ?compounds?").name("NBT Tag Compound").parser(new Parser<net.minecraft.server.v1_7_R4.NBTTagCompound>() {

					@Override
					public String getVariableNamePattern() {
						return "nbt:{.+:.+}";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_7_R4.NBTTagCompound parse(String s, ParseContext context) {
						if (s.startsWith("{")) {
							net.minecraft.server.v1_7_R4.NBTTagCompound NBT = new net.minecraft.server.v1_7_R4.NBTTagCompound();
							net.minecraft.server.v1_7_R4.NBTTagCompound NBT1 = (net.minecraft.server.v1_7_R4.NBTTagCompound) net.minecraft.server.v1_7_R4.MojangsonParser.parse(s);
							NBT.set("", NBT1);
							if (NBT.isEmpty() || NBT == null) {
								return null;
							}
							return NBT;
						}
						return null;
					}

					@Override
					public String toString(net.minecraft.server.v1_7_R4.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_7_R4.NBTTagCompound compound) {
						return "nbt:" + compound.toString();
					}
				}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.8)")) {
				getLogger().info("Successfully found 1.8! Registering version specific expressions...");
				exprAmount += 4;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_8_R1.class, NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_8_R1.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_8_R1.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				//WARNING! HIGHLY EXPERIMENTAL, IT CAN CORRUPT WORLDS AT CURRENT VERSION, USE AT YOUR OWN RISK!
				Skript.registerExpression(ExprFileNBTv1_8_R1.class, NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Classes.registerClass(new ClassInfo<NBTTagCompound>(NBTTagCompound.class, "compound").name("NBT Tag Compound").user("((nbt)?( ?tag)?) ?compounds?").parser(new Parser<NBTTagCompound>() {

					@Override
					public String getVariableNamePattern() {
						return "nbt:{.+:.+}";
					}

					@Override
					@Nullable
					public NBTTagCompound parse(String s, ParseContext context) {
						if (s.startsWith("{")) {
							NBTTagCompound NBT = new NBTTagCompound();
							NBTTagCompound NBT1 = null;
							NBT1 = MojangsonParser.parse(s);
							NBT.a(NBT1);
							if (NBT.isEmpty() || NBT == null) {
								return null;
							}
							return NBT;
						}
						return null;
					}

					@Override
					public String toString(NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(NBTTagCompound compound) {
						return "nbt:" + compound.toString();
					}
				}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.8.3)")) {
				getLogger().info("Successfully found 1.8.3! Registering version specific expressions...");
				exprAmount += 4;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_8_R2.class, net.minecraft.server.v1_8_R2.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_8_R2.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_8_R2.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				//WARNING! HIGHLY EXPERIMENTAL, IT CAN CORRUPT WORLDS AT CURRENT VERSION, USE AT YOUR OWN RISK!
				Skript.registerExpression(ExprFileNBTv1_8_R2.class, net.minecraft.server.v1_8_R2.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_8_R2.NBTTagCompound>(net.minecraft.server.v1_8_R2.NBTTagCompound.class, "compound").user("((nbt)?( ?tag)?) ?compounds?").name("NBT Tag Compound").parser(new Parser<net.minecraft.server.v1_8_R2.NBTTagCompound>() {

					@Override
					public String getVariableNamePattern() {
						return "nbt:{.+:.+}";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_8_R2.NBTTagCompound parse(String s, ParseContext context) {
						if (s.startsWith("{")) {
							net.minecraft.server.v1_8_R2.NBTTagCompound NBT = new net.minecraft.server.v1_8_R2.NBTTagCompound();
							try {
								net.minecraft.server.v1_8_R2.NBTTagCompound NBT1 = net.minecraft.server.v1_8_R2.MojangsonParser.parse(s);
								NBT.a(NBT1);
							} catch (MojangsonParseException ex) {
								Skript.warning("Error when parsing NBT - " + ex.getMessage());
							}
							if (NBT.isEmpty() || NBT == null) {
								return null;
							}
							return NBT;
						}
						return null;
					}

					@Override
					public String toString(net.minecraft.server.v1_8_R2.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_8_R2.NBTTagCompound compound) {
						return "nbt:" + compound.toString();
					}
				}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.8.4)") || Bukkit.getVersion().contains("(MC: 1.8.5)") || Bukkit.getVersion().contains("(MC: 1.8.6)") || Bukkit.getVersion().contains("(MC: 1.8.7)") || Bukkit.getVersion().contains("(MC: 1.8.8)")) {
				getLogger().info("Successfully found 1.8.4 - 1.8.8! Registering version specific expressions...");
				exprAmount += 4;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_8_R3.class, net.minecraft.server.v1_8_R3.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_8_R3.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_8_R3.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				//WARNING! HIGHLY EXPERIMENTAL, IT CAN CORRUPT WORLDS AT CURRENT VERSION, USE AT YOUR OWN RISK!
				Skript.registerExpression(ExprFileNBTv1_8_R3.class, net.minecraft.server.v1_8_R3.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] from [file] %string%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_8_R3.NBTTagCompound>(net.minecraft.server.v1_8_R3.NBTTagCompound.class, "compound").user("((nbt)?( ?tag)?) ?compounds?").name("NBT Compound").parser(new Parser<net.minecraft.server.v1_8_R3.NBTTagCompound>() {

					@Override
					public String getVariableNamePattern() {
						return "nbt:{.+:.+}";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_8_R3.NBTTagCompound parse(String s, ParseContext context) {
						if (s.startsWith("{")) {
							net.minecraft.server.v1_8_R3.NBTTagCompound NBT = new net.minecraft.server.v1_8_R3.NBTTagCompound();
							try {
								net.minecraft.server.v1_8_R3.NBTTagCompound NBT1 = net.minecraft.server.v1_8_R3.MojangsonParser.parse(s);
								NBT.a(NBT1);
							} catch (net.minecraft.server.v1_8_R3.MojangsonParseException ex) {
								return null;
							}
							if (NBT.isEmpty() || NBT == null) {
								return null;
							}
							return NBT;
						}
						return null;
					}

					@Override
					public String toString(net.minecraft.server.v1_8_R3.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_8_R3.NBTTagCompound compound) {
						return "nbt:" + compound.toString();
					}
				}));
			}
			if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
				getLogger().info("WorldEdit found! Registering WorldEdit stuff...");
				condAmount += 1;
				effAmount += 12;
				exprAmount += 16;
				typeAmount += 1;
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
				Skript.registerExpression(ExprNewEditSession.class, EditSession.class, ExpressionType.PROPERTY, "new edit[ ]session in [world] %world% [with] [max[imum]] [block] limit [change] [of] %integer%");
				Skript.registerExpression(ExprSelectionOfPlayer.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection");
				Skript.registerExpression(ExprSelectionPos1.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] po(s|int)[ ]1 of %player%", "%player%'s [(world[ ]edit|we)] po(s|int)[ ]1");
				Skript.registerExpression(ExprSelectionPos2.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] po(s|int)[ ]2 of %player%", "%player%'s [(world[ ]edit|we)] po(s|int)[ ]2");
				Skript.registerExpression(ExprVolumeOfSelection.class, Integer.class, ExpressionType.SIMPLE, "volume of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection volume");
				Skript.registerExpression(ExprWidthOfSelection.class, Integer.class, ExpressionType.SIMPLE, "(x( |-)size|width) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection (x( |-)size|width)");
				Skript.registerExpression(ExprLengthOfSelection.class, Integer.class, ExpressionType.SIMPLE, "(z( |-)size|length) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we) ]selection (z( |-)size|length)");
				Skript.registerExpression(ExprHeightOfSelection.class, Integer.class, ExpressionType.SIMPLE, "(y( |-)size|height) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we) ]selection (y( |-)size|height)");
				Skript.registerExpression(ExprAreaOfSelection.class, Integer.class, ExpressionType.SIMPLE, "area of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection area");
				Skript.registerExpression(ExprVolumeOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "volume of schem[atic] [from] %string%");
				Skript.registerExpression(ExprWidthOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "(x( |-)size|width) of schem[atic] [from] %string%");
				Skript.registerExpression(ExprHeightOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "(y( |-)size|height) of schem[atic] [from] %string%");
				Skript.registerExpression(ExprLengthOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "(z( |-)size|length) of schem[atic] [from] %string%");
				Skript.registerExpression(ExprAreaOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "area of schem[atic] [from] %string%");
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
					evtWE = true;
				} catch (ClassNotFoundException ex) {
					Skript.error("Unable to register \"On WorldEdit block change\" event! You will need to upgrade to WorldEdit 6.0");
				}
			}
			getLogger().info("Everything ready! Loaded a total of " + condAmount + (condAmount == 1 ? " condition, " : " conditions, ") + effAmount + (effAmount == 1 ? " effect, " : " effects, ") + (evtWE ? "1 event, " : "") + exprAmount + (exprAmount == 1 ? " expression" : " expressions and ") + typeAmount + (typeAmount == 1 ? " type!" : " types!"));
		} else {
			getLogger().info("Unable to find Skript, disabling SkStuff...");
			Bukkit.getPluginManager().disablePlugin(this);;
		}
	}

	public void onDisable() {
		getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully disabled.");
	}
}
