package vazkii.morphtool;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import net.neoforged.neoforge.common.ModConfigSpec;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MorphToolConfig {

	public static ModConfigSpec.BooleanValue allItems;
	public static ModConfigSpec.BooleanValue invertHandShift;
	public static ModConfigSpec.ConfigValue<List<? extends String>> whitelistedItems, whitelistedNames, blacklistedMods;
	public static ModConfigSpec.ConfigValue<List<? extends String>> aliasesList;
	static final MorphToolConfig CONFIG;
	static final ModConfigSpec CONFIG_SPEC;

	static {
		final Pair<MorphToolConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(MorphToolConfig::new);
		CONFIG = specPair.getLeft();
		CONFIG_SPEC = specPair.getRight();
	}

	public MorphToolConfig(ModConfigSpec.Builder builder) {
		allItems = builder.define("allow_all_items", false); //Allow all items to be added
		invertHandShift = builder.define("offhand_morph", false); //Morph in the offhand instead of mainhand

		whitelistedItems = builder.defineList("whitelist_items", //"Whitelisted Items"
				Lists.newArrayList("botania:twig_wand",
						"ae2:network_tool",
						"immersiveengineering:tool",
						"buildersguides:mallet",
						"environmentaltech:tool_multiblock_assembler",
						"bloodmagic:ritual_reader",
						"draconicevolution:crystal_binder",
						"crossroads:omnimeter",
						"storagedrawers:keyring"
				),
				String::new,
				Predicates.alwaysTrue());

		whitelistedNames = builder.defineList("whitelist_names", //"Whitelisted Names"
				Lists.newArrayList("wrench",
						"screwdriver",
						"hammer",
						"rotator",
						"configurator",
						"crowbar"
				),
				String::new,
				Predicates.alwaysTrue());

		blacklistedMods = builder.defineList("blacklist_mods", //Blacklisted Mods
				Lists.newArrayList("tconstruct",
						"intangible"),
				String::new,
				Predicates.alwaysTrue());

		aliasesList = builder.defineList("mod_aliasses", //Mod Aliases
				Lists.newArrayList("nautralpledge=botania",
						"thermalexpansion=thermalfoundation",
						"thermaldynamics=thermalfoundation",
						"thermalcultivation=thermalfoundation",
						"redstonearsenal=thermalfoundation",
						"rftoolsdim=rftools",
						"rftoolspower=rftools",
						"rftoolscontrol=rftools",
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
				String::new,
				Predicates.alwaysTrue());

	}

}
