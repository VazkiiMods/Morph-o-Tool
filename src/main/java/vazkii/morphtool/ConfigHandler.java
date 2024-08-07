package vazkii.morphtool;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ConfigHandler {

	public static ForgeConfigSpec.BooleanValue allItems;
	public static ForgeConfigSpec.BooleanValue invertHandShift;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedItems, whitelistedNames, blacklistedMods;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> aliasesList;
	static final ConfigHandler CONFIG;
	static final ForgeConfigSpec CONFIG_SPEC;

	static {
		final Pair<ConfigHandler, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigHandler::new);
		CONFIG = specPair.getLeft();
		CONFIG_SPEC = specPair.getRight();
	}

	public ConfigHandler(ForgeConfigSpec.Builder builder) {
		allItems = builder.define("Allow all items to be added", false);
		invertHandShift = builder.define("Morph in the offhand instead of mainhand", false);

		whitelistedItems = builder.defineList("Whitelisted Items",
				Lists.newArrayList("botania:twig_wand",
						"appliedenergistics2:network_tool",
						"immersiveengineering:tool",
						"buildersguides:mallet",
						"environmentaltech:tool_multiblock_assembler",
						"bloodmagic:ritual_reader",
						"draconicevolution:crystal_binder",
						"crossroads:omnimeter"),
				Predicates.alwaysTrue());

		whitelistedNames = builder.defineList("Whitelisted Names",
				Lists.newArrayList("wrench",
						"screwdriver",
						"hammer",
						"rotator",
						"configurator",
						"crowbar"),
				Predicates.alwaysTrue());

		blacklistedMods = builder.defineList("Blacklisted Mods",
				Lists.newArrayList("tconstruct",
						"intangible"),
				Predicates.alwaysTrue());

		aliasesList = builder.defineList("Mod Aliases",
				Lists.newArrayList("nautralpledge=botania",
						"thermalexpansion=thermalfoundation",
						"thermaldynamics=thermalfoundation",
						"thermalcultivation=thermalfoundation",
						"redstonearsenal=thermalfoundation",
						"rftoolsdim=rftools",
						"rftoolspower=rftools",
						"rftoolscontrol=rftools",
						"ae2stuff=appliedenergistics2",
						"animus=bloodmagic",
						"integrateddynamics=integratedtunnels",
						"mekanismgenerators=mekanism",
						"mekanismtools=mekanism",
						"deepresonance=rftools",
						"xnet=rftools",
						"buildcrafttransport=buildcraft",
						"buildcraftfactory=buildcraft",
						"buildcraftsilicon=buildcraft",
						"cabletiers=refinedstorage",
						"extrastorage=refinedstorage"),
				Predicates.alwaysTrue());

	}

}
