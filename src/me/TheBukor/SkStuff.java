package me.TheBukor;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import me.TheBukor.conditions.CondSelectionContains;
import me.TheBukor.expressions.ExprAreaOfSelection;
import me.TheBukor.expressions.ExprHeightOfSchematic;
import me.TheBukor.expressions.ExprHeightOfSelection;
import me.TheBukor.expressions.ExprItemNBTv1_8_R1;
import me.TheBukor.expressions.ExprItemNBTv1_8_R2;
import me.TheBukor.expressions.ExprItemNBTv1_8_R3;
import me.TheBukor.expressions.ExprLengthOfSchematic;
import me.TheBukor.expressions.ExprLengthOfSelection;
import me.TheBukor.expressions.ExprNBTv1_8_R1;
import me.TheBukor.expressions.ExprNBTv1_8_R2;
import me.TheBukor.expressions.ExprNBTv1_8_R3;
import me.TheBukor.expressions.ExprSelectionOfPlayer;
import me.TheBukor.expressions.ExprSelectionPos1;
import me.TheBukor.expressions.ExprSelectionPos2;
import me.TheBukor.expressions.ExprTagOfv1_8_R1;
import me.TheBukor.expressions.ExprTagOfv1_8_R2;
import me.TheBukor.expressions.ExprTagOfv1_8_R3;
import me.TheBukor.expressions.ExprVolumeOfSchematic;
import me.TheBukor.expressions.ExprVolumeOfSelection;
import me.TheBukor.expressions.ExprWidthOfSchematic;
import me.TheBukor.expressions.ExprWidthOfSelection;
import net.minecraft.server.v1_8_R1.MojangsonParser;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R2.MojangsonParseException;

public class SkStuff extends JavaPlugin {
	private int condAmount = 0;
	private int exprAmount = 0;
	private int typeAmount = 0;
	private int evtAmount = 0;
	public void onEnable() {
		if (Bukkit.getPluginManager().getPlugin("Skript") != null) {
			Skript.registerAddon(this);
			getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully enabled!");
			if (Bukkit.getVersion().contains("(MC: 1.8)")){
				getLogger().info("Successfully found 1.8! Registering version specific expressions...");
				exprAmount += 3;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_8_R1.class, NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_8_R1.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_8_R1.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				Classes.registerClass(new ClassInfo<NBTTagCompound>(NBTTagCompound.class, "compound").name("NBT Tag Compound").parser(new Parser<NBTTagCompound>() {

					@Override
					public String getVariableNamePattern() {
						return ".+";
					}

					@Override
					@Nullable
					public NBTTagCompound parse(String s, ParseContext arg1) {
						NBTTagCompound NBT = new NBTTagCompound();
						NBTTagCompound NBT1 = MojangsonParser.parse(s);
						NBT1.a(NBT);
						if (NBT.isEmpty() || NBT == null) {
							return null;
						}
						return NBT;
					}

					@Override
					public String toString(NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(NBTTagCompound compound) {
						return compound.toString();
					}
			}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.8.3)")){
				getLogger().info("Successfully found 1.8.3! Registering version specific expressions...");
				exprAmount += 3;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_8_R2.class, net.minecraft.server.v1_8_R2.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_8_R2.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_8_R2.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_8_R2.NBTTagCompound>(net.minecraft.server.v1_8_R2.NBTTagCompound.class, "compound").name("NBT Tag Compound").parser(new Parser<net.minecraft.server.v1_8_R2.NBTTagCompound>() {

					@Override
					public String getVariableNamePattern() {
						return ".+";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_8_R2.NBTTagCompound parse(String s, ParseContext arg1) {
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

					@Override
					public String toString(net.minecraft.server.v1_8_R2.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_8_R2.NBTTagCompound compound) {
						return compound.toString();
					}
			}));
			}
			if (Bukkit.getVersion().contains("(MC: 1.8.4)") || Bukkit.getVersion().contains("(MC: 1.8.5)") || Bukkit.getVersion().contains("(MC: 1.8.6)") || Bukkit.getVersion().contains("(MC: 1.8.7)") || Bukkit.getVersion().contains("(MC: 1.8.8)")) {
				getLogger().info("Successfully found 1.8.4 - 1.8.8! Registering version specific expressions...");
				exprAmount += 3;
				typeAmount += 1;
				Skript.registerExpression(ExprNBTv1_8_R3.class, net.minecraft.server.v1_8_R3.NBTTagCompound.class, ExpressionType.PROPERTY, "nbt[[ ]tag[s]] of %entity/block/itemstack%", "%entity/block/itemstack%'s nbt[[ ]tag[s]]");
				Skript.registerExpression(ExprItemNBTv1_8_R3.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with [custom] nbt[[ ]tag[s]] %string%");
				Skript.registerExpression(ExprTagOfv1_8_R3.class, Object.class, ExpressionType.SIMPLE, "[nbt[ ]]tag %string% of [nbt [compound]] %compound%");
				Classes.registerClass(new ClassInfo<net.minecraft.server.v1_8_R3.NBTTagCompound>(net.minecraft.server.v1_8_R3.NBTTagCompound.class, "compound").name("NBT Tag Compound").parser(new Parser<net.minecraft.server.v1_8_R3.NBTTagCompound>() {

					@Override
					public String getVariableNamePattern() {
						return ".+";
					}

					@Override
					@Nullable
					public net.minecraft.server.v1_8_R3.NBTTagCompound parse(String s, ParseContext arg1) {
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

					@Override
					public String toString(net.minecraft.server.v1_8_R3.NBTTagCompound compound, int arg1) {
						return compound.toString();
					}

					@Override
					public String toVariableNameString(net.minecraft.server.v1_8_R3.NBTTagCompound compound) {
						return compound.toString();
					}
			}));
			}
			if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
				getLogger().info("WorldEdit found! Registering WorldEdit stuff...");
				condAmount += 1;
				evtAmount  += 1;
				exprAmount += 12;
				Skript.registerCondition(CondSelectionContains.class, "[(world[ ]edit|we)] selection of %player% (contains|has) %location%", "%player%'s [(world[ ]edit|we)] selection (contains|has) %location%", "[(world[ ]edit|we)] selection of %player% does(n't| not) (contain|have) %location%", "%player%'s [(world[ ]edit|we)] selection does(n't| not) (contain|have) %location%");
				Skript.registerExpression(ExprSelectionOfPlayer.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection");
				Skript.registerExpression(ExprSelectionPos1.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] po(s|int)[ ]1 of %player%", "%player%'s [(world[ ]edit|we)] po(s|int)[ ]1");
				Skript.registerExpression(ExprSelectionPos2.class, Location.class, ExpressionType.PROPERTY, "[(world[ ]edit|we)] po(s|int)[ ]2 of %player%", "%player%'s [(world[ ]edit|we)] po(s|int)[ ]2");
				Skript.registerExpression(ExprVolumeOfSelection.class, Integer.class, ExpressionType.SIMPLE, "volume of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection volume");
				Skript.registerExpression(ExprWidthOfSelection.class, Integer.class, ExpressionType.SIMPLE, "(x( |-)size|width) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection (x( |-)size|width)");
				Skript.registerExpression(ExprLengthOfSelection.class, Integer.class, ExpressionType.SIMPLE, "(z( |-)size|length) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we) ]selection (z( |-)size|length)");
				Skript.registerExpression(ExprHeightOfSelection.class, Integer.class, ExpressionType.SIMPLE, "(y( |-)size|height) of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we) ]selection (y( |-)size|height)");
				Skript.registerExpression(ExprAreaOfSelection.class, Integer.class, ExpressionType.SIMPLE, "area of [(world[ ]edit|we)] selection of %player%", "%player%'s [(world[ ]edit|we)] selection area");
				Skript.registerExpression(ExprVolumeOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "volume of schem[atic] %string% [from [folder] %string%]");
				Skript.registerExpression(ExprWidthOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "(x( |-)size|width) of schem[atic] %string% [from [folder] %string%]");
				Skript.registerExpression(ExprHeightOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "(y( |-)size|height) of schem[atic] %string% [from [folder] %string%]");
				Skript.registerExpression(ExprLengthOfSchematic.class, Integer.class, ExpressionType.SIMPLE, "(z( |-)size|length) of schem[atic] %string% [from [folder] %string%]");
				}
				String pluralCond = "s";
				String pluralType = "s";
				String pluralEvt = "s";
				if (condAmount == 1) {
					pluralCond = "";
				}
				if (typeAmount == 1) {
					pluralType = "";
				}
				if (evtAmount == 1) {
					pluralEvt = "";
				}
				getLogger().info("Everything ready! Loaded a total of " + condAmount + " condition" + pluralCond + ", " + evtAmount + "event" + pluralEvt + ", " + exprAmount + " expressions and " + typeAmount + " type" + pluralType + "!");
			} else {
				getLogger().info("Unable to find Skript, disabling SkStuff...");
				this.onDisable();
			}
		}

	public void onDisable() {
		getLogger().info("SkStuff " + this.getDescription().getVersion() + " has been successfully disabled");
	}
}
