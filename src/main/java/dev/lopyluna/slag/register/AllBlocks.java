package dev.lopyluna.slag.register;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.FluidEntry;
import dev.lopyluna.slag.SlagEmbers;
import dev.lopyluna.slag.content.blocks.basin.BasinBlock;
import dev.lopyluna.slag.content.blocks.crucible.CrucibleBlock;
import dev.lopyluna.slag.content.blocks.crucible.CrucibleItem;
import dev.lopyluna.slag.content.blocks.crucible_interface.InterfaceBlock;
import dev.lopyluna.slag.content.blocks.drain.DrainBlock;
import dev.lopyluna.slag.content.blocks.melter.MelterBE;
import dev.lopyluna.slag.content.blocks.melter.MelterBlock;
import dev.lopyluna.slag.content.blocks.table.TableBlock;
import dev.lopyluna.slag.content.datagen.*;
import dev.lopyluna.slag.content.utils.BlockHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Function;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;
import static dev.lopyluna.slag.SlagEmbers.REG;
import static dev.lopyluna.slag.content.blocks.crucible.CrucibleBE.*;
import static dev.lopyluna.slag.content.datagen.AlloyingRecipeBuilder.fluid;
import static dev.lopyluna.slag.content.datagen.MeltingRecipeBuilder.*;
import static dev.lopyluna.slag.content.utils.BlockHelper.getExistingModel;

@SuppressWarnings({"removal", "unused"})
public class AllBlocks {
    public static final BlockEntry<Block> ROSE_GOLD_BLOCK = REG.block("rose_gold_block", Block::new)
            .lang("Block of Rose Gold")
            .initialProperties(() -> Blocks.GOLD_BLOCK)
            .properties(p -> p.mapColor(MapColor.COLOR_PINK).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, BlockTags.BEACON_BASE_BLOCKS, AllTags.blockC("storage_blocks"), AllTags.blockC("storage_blocks/rose_gold"))
            .item()
            .tag(AllTags.itemC("storage_blocks"), AllTags.itemC("storage_blocks/rose_gold"))
            .build()
            .register();

    public static final BlockEntry<Block> DEEP_ALLOY_BLOCK = REG.block("deep_alloy_block", Block::new)
            .lang("Block of Deep Alloy")
            .initialProperties(() -> Blocks.GOLD_BLOCK)
            .properties(p -> p.sound(AllSoundTypes.CRUCIBLE).noOcclusion().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, BlockTags.BEACON_BASE_BLOCKS, AllTags.blockC("storage_blocks"), AllTags.blockC("storage_blocks/deep_alloy"))
            .item()
            .tag(AllTags.itemC("storage_blocks"), AllTags.itemC("storage_blocks/deep_alloy"))
            .build()
            .register();

    public static final BlockEntry<CrucibleBlock> CRUCIBLE = REG.block("crucible", CrucibleBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .blockstate((c, p) -> p.getVariantBuilder(c.get()).forAllStates(state -> {
                Function<BlockState, ModelFile> modelFunc = s -> {
                    var model = getExistingModel(c, p,
                            switch (s.getValue(SHAPE)) { case PLAIN -> ""; case INNER -> "inner"; case NW -> "nw"; case SW -> "sw"; case NE -> "ne"; case SE -> "se"; case NORTH -> "north"; case SOUTH -> "south"; case WEST -> "west"; case EAST -> "east"; },
                            s.getValue(TOP) ? "top" : "",
                            s.getValue(BOTTOM) ? "bottom" : ""
                    );
                    var loc = model.getLocation();
                    var wModel = p.models().withExistingParent(loc.getPath() + "_window", loc).texture("1", "block/crucible_side_window");
                    return s.getValue(WINDOW) ? wModel : model;
                };
                return ConfiguredModel.builder().modelFile(modelFunc.apply(state)).build();
            })).properties(p -> p.sound(AllSoundTypes.CRUCIBLE).noOcclusion().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops())
            .recipe((c, p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 4)
                        .pattern("D D")
                        .pattern("D D")
                        .pattern("DBD")
                        .define('D', AllTags.itemC("ingots/deep_alloy")).define('B', AllTags.itemC("storage_blocks/deep_alloy"))
                        .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName()));

                AlloyingRecipeBuilder.create(AllFluids.MOLTEN_ROSE_GOLD.getSource(), MelterBE.NUGGET_SIZE * 2, fluid(AllFluids.MOLTEN_GOLD.getSource(), MelterBE.NUGGET_SIZE), fluid(AllFluids.MOLTEN_COPPER.getSource(), MelterBE.NUGGET_SIZE))
                        .unlockedBy("has_lava", has(Items.LAVA_BUCKET)).save(p, SlagEmbers.loc("alloying/molten_rose_gold"));

                AlloyingRecipeBuilder.create(AllFluids.MOLTEN_BRASS.getSource(), MelterBE.NUGGET_SIZE * 2, fluid(AllFluids.MOLTEN_ZINC.getSource(), MelterBE.NUGGET_SIZE), fluid(AllFluids.MOLTEN_COPPER.getSource(), MelterBE.NUGGET_SIZE))
                        .unlockedBy("has_lava", has(Items.LAVA_BUCKET)).save(p, SlagEmbers.loc("alloying/molten_brass"));
            })
            .addLayer(() -> RenderType::cutoutMipped)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item(CrucibleItem::new)
            .model((c, p) -> p.withExistingParent("item/" + c.getName(), p.modLoc("block/"+c.getName()+"/block_top_bottom")))
            .build()
            .register();

    public static final BlockEntry<TableBlock> TABLE = REG.block("table", TableBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .lang("Casting Table")
            .blockstate((c, p) -> p.simpleBlock(c.get(), p.models().getExistingFile(p.modLoc("block/table"))))
            .properties(p -> p.sound(AllSoundTypes.CRUCIBLE).noOcclusion().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .recipe((c, p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                        .pattern("AAA")
                        .pattern("A A")
                        .define('A', AllTags.itemC("ingots/deep_alloy"))
                        .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName()));

                /*
                create(p, "gem", "emerald", Items.EMERALD, AllFluids.MOLTEN_EMERALD, MelterBE.INGOT_SIZE, AllTags.CAST_GEMS);
                create(p, "gem", "lapis", Items.LAPIS_LAZULI, AllFluids.MOLTEN_LAPIS, MelterBE.INGOT_SIZE, AllTags.CAST_GEMS);

                create(p, "gem", "quartz", Items.QUARTZ, AllFluids.MOLTEN_QUARTZ, MelterBE.INGOT_SIZE, AllTags.CAST_GEMS);
                create(p, "gem", "amethyst", Items.AMETHYST_SHARD, AllFluids.MOLTEN_AMETHYST, MelterBE.INGOT_SIZE, AllTags.CAST_GEMS);
                create(p, "gem", "prismarine", Items.PRISMARINE, AllFluids.MOLTEN_PRISMARINE, MelterBE.INGOT_SIZE, AllTags.CAST_GEMS);

                create(p, "dust", "redstone", Items.REDSTONE, AllFluids.MOLTEN_REDSTONE, MelterBE.INGOT_SIZE, AllTags.CAST_DUSTS);
                */

                create(p, "ingot", "copper", Items.COPPER_INGOT, AllFluids.MOLTEN_COPPER, MelterBE.INGOT_SIZE, AllTags.CAST_INGOTS);
                create(p, "ingot", "gold", Items.GOLD_INGOT, AllFluids.MOLTEN_GOLD, MelterBE.INGOT_SIZE, AllTags.CAST_INGOTS);
                create(p, "ingot", "iron", Items.IRON_INGOT, AllFluids.MOLTEN_IRON, MelterBE.INGOT_SIZE, AllTags.CAST_INGOTS);
                create(p, "ingot", "rose_gold", AllItems.ROSE_GOLD_INGOT.get(), AllFluids.MOLTEN_ROSE_GOLD, MelterBE.INGOT_SIZE, AllTags.CAST_INGOTS);
                create(p, "ingot", "netherite", Items.NETHERITE_INGOT, AllFluids.MOLTEN_NETHERITE, MelterBE.INGOT_SIZE, AllTags.CAST_INGOTS);

                create(p, "gem", "diamond", Items.DIAMOND, AllFluids.MOLTEN_DIAMOND, MelterBE.INGOT_SIZE, AllTags.CAST_GEMS);

                create(p, "nugget", "gold", Items.GOLD_NUGGET, AllFluids.MOLTEN_GOLD, MelterBE.NUGGET_SIZE, AllTags.CAST_NUGGETS);
                create(p, "nugget", "iron", Items.IRON_NUGGET, AllFluids.MOLTEN_IRON, MelterBE.NUGGET_SIZE, AllTags.CAST_NUGGETS);
                create(p, "nugget", "rose_gold", AllItems.ROSE_GOLD_NUGGET.get(), AllFluids.MOLTEN_ROSE_GOLD, MelterBE.NUGGET_SIZE, AllTags.CAST_NUGGETS);

                //Create
                create(p, "ingot", "zinc", com.simibubi.create.AllItems.ZINC_INGOT.get(), AllFluids.MOLTEN_ZINC, MelterBE.INGOT_SIZE, AllTags.CAST_INGOTS);
                create(p, "ingot", "brass", com.simibubi.create.AllItems.BRASS_INGOT.get(), AllFluids.MOLTEN_BRASS, MelterBE.INGOT_SIZE, AllTags.CAST_INGOTS);

                create(p, "nugget", "copper", com.simibubi.create.AllItems.COPPER_NUGGET.get(), AllFluids.MOLTEN_COPPER, MelterBE.NUGGET_SIZE, AllTags.CAST_NUGGETS);
                create(p, "nugget", "zinc", com.simibubi.create.AllItems.ZINC_NUGGET.get(), AllFluids.MOLTEN_ZINC, MelterBE.NUGGET_SIZE, AllTags.CAST_NUGGETS);
                create(p, "nugget", "brass", com.simibubi.create.AllItems.BRASS_NUGGET.get(), AllFluids.MOLTEN_BRASS, MelterBE.NUGGET_SIZE, AllTags.CAST_NUGGETS);

                create(p, "sheet", "copper", com.simibubi.create.AllItems.COPPER_SHEET.get(), AllFluids.MOLTEN_COPPER, MelterBE.INGOT_SIZE, AllTags.CAST_SHEETS);
                create(p, "sheet", "brass", com.simibubi.create.AllItems.BRASS_SHEET.get(), AllFluids.MOLTEN_BRASS, MelterBE.INGOT_SIZE, AllTags.CAST_SHEETS);
                create(p, "sheet", "iron", com.simibubi.create.AllItems.IRON_SHEET.get(), AllFluids.MOLTEN_IRON, MelterBE.INGOT_SIZE, AllTags.CAST_SHEETS);
                create(p, "sheet", "gold", com.simibubi.create.AllItems.GOLDEN_SHEET.get(), AllFluids.MOLTEN_GOLD, MelterBE.INGOT_SIZE, AllTags.CAST_SHEETS);

                create(p, "ingot", "chocolate", com.simibubi.create.AllItems.BAR_OF_CHOCOLATE.get(), com.simibubi.create.AllFluids.CHOCOLATE, MelterBE.SIMPLE_BLOCK_SIZE/4, AllTags.CAST_INGOTS);
            })
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent("item/" + c.getName(), p.modLoc("block/table")))
            .build()
            .register();

    public static void create(RegistrateRecipeProvider p, String key, String type, Item result, FluidEntry<?> fluid, int mb, TagKey<Item> castType) {
        create(p, key, type, result, fluid.getSource(), mb, castType);
    }
    public static void create(RegistrateRecipeProvider p, String key, String type, Item result, Fluid fluid, int mb, TagKey<Item> castType) {
        TableCastingRecipeBuilder.create(result, fluid, mb, castType)
                .unlockedBy("has_" + key, has(AllTags.itemC(key + "s/" + type))).save(p, SlagEmbers.loc("casting/table/" + type + "_" + key));
    }

    public static final BlockEntry<BasinBlock> BASIN = REG.block("basin", BasinBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .lang("Casting Basin")
            .blockstate((c, p) -> p.simpleBlock(c.get(), p.models().getExistingFile(p.modLoc("block/basin"))))
            .properties(p -> p.sound(AllSoundTypes.CRUCIBLE).noOcclusion().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .recipe((c, p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                        .pattern("A A")
                        .pattern("A A")
                        .pattern("AAA")
                        .define('A', AllTags.itemC("ingots/deep_alloy"))
                        .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName()));
                /*
                BasinCastingRecipeBuilder.create(Items.AMETHYST_BLOCK, 1, AllFluids.MOLTEN_AMETHYST.getSource(), MelterBE.SMALL_BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/amethyst"))).save(p, SlagEmbers.loc("casting/basin/amethyst_block"));
                BasinCastingRecipeBuilder.create(Items.EMERALD_BLOCK, 1, AllFluids.MOLTEN_EMERALD.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/emerald"))).save(p, SlagEmbers.loc("casting/basin/emerald_block"));
                BasinCastingRecipeBuilder.create(Items.LAPIS_BLOCK, 1, AllFluids.MOLTEN_LAPIS.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/lapis"))).save(p, SlagEmbers.loc("casting/basin/lapis_block"));
                BasinCastingRecipeBuilder.create(Items.QUARTZ_BLOCK, 1, AllFluids.MOLTEN_QUARTZ.getSource(), MelterBE.SMALL_BLOCK_SIZE).unlockedBy("has_block", has(AllTags.QUARTZ_BLOCKS)).save(p, SlagEmbers.loc("casting/basin/quartz_block"));
                BasinCastingRecipeBuilder.create(Items.REDSTONE_BLOCK, 1, AllFluids.MOLTEN_REDSTONE.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/redstone"))).save(p, SlagEmbers.loc("casting/basin/redstone_block"));
                BasinCastingRecipeBuilder.create(Items.OBSIDIAN, 1, AllFluids.MOLTEN_OBSIDIAN.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(Tags.Items.OBSIDIANS)).save(p, SlagEmbers.loc("casting/basin/obsidian"));
                */

                BasinCastingRecipeBuilder.create(Items.COPPER_BLOCK, 1, AllFluids.MOLTEN_COPPER.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.COPPER_BLOCKS)).save(p, SlagEmbers.loc("casting/basin/copper_block"));
                BasinCastingRecipeBuilder.create(Items.DIAMOND_BLOCK, 1, AllFluids.MOLTEN_DIAMOND.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/diamond"))).save(p, SlagEmbers.loc("casting/basin/diamond_block"));
                BasinCastingRecipeBuilder.create(Items.GOLD_BLOCK, 1, AllFluids.MOLTEN_GOLD.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/gold"))).save(p, SlagEmbers.loc("casting/basin/gold_block"));
                BasinCastingRecipeBuilder.create(Items.IRON_BLOCK, 1, AllFluids.MOLTEN_IRON.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/iron"))).save(p, SlagEmbers.loc("casting/basin/iron_block"));
                BasinCastingRecipeBuilder.create(Items.NETHERITE_BLOCK, 1, AllFluids.MOLTEN_NETHERITE.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/netherite"))).save(p, SlagEmbers.loc("casting/basin/netherite_block"));
                BasinCastingRecipeBuilder.create(ROSE_GOLD_BLOCK.asItem(), 1, AllFluids.MOLTEN_ROSE_GOLD.getSource(), MelterBE.BLOCK_SIZE).unlockedBy("has_block", has(AllTags.itemC("storage_blocks/rose_gold"))).save(p, SlagEmbers.loc("casting/basin/rose_gold_block"));
                BasinCastingRecipeBuilder.create(Items.STONE, 1, Fluids.LAVA.getSource(), MelterBE.SIMPLE_BLOCK_SIZE).unlockedBy("has_block", has(Items.STONE)).save(p, SlagEmbers.loc("casting/basin/stone"));

                //Create
                BasinCastingRecipeBuilder.create(com.simibubi.create.AllBlocks.ZINC_BLOCK.asItem(), 1, AllFluids.MOLTEN_ZINC.getSource(), MelterBE.BLOCK_SIZE)
                        .unlockedBy("has_block", has(com.simibubi.create.AllBlocks.ZINC_BLOCK.asItem())).save(p, SlagEmbers.loc("casting/basin/zinc_block"));
                BasinCastingRecipeBuilder.create(com.simibubi.create.AllBlocks.BRASS_BLOCK.asItem(), 1, AllFluids.MOLTEN_BRASS.getSource(), MelterBE.BLOCK_SIZE)
                        .unlockedBy("has_block", has(com.simibubi.create.AllBlocks.BRASS_BLOCK.asItem())).save(p, SlagEmbers.loc("casting/basin/brass_block"));
            })
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent("item/" + c.getName(), p.modLoc("block/basin")))
            .build()
            .register();

    public static final BlockEntry<DrainBlock> DRAIN = REG.block("drain", DrainBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .blockstate(BlockHelper::genHorizontalDirectional)
            .properties(p -> p.sound(AllSoundTypes.CRUCIBLE).noOcclusion().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .recipe((c, p) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, c.get(), 1)
                    .requires(AllTags.itemC("ingots/deep_alloy")).unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName())))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent("item/" + c.getName(), p.modLoc("block/drain")))
            .build()
            .register();

    public static final BlockEntry<MelterBlock> MELTER = REG.block("melter", MelterBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .blockstate(BlockHelper::genHorizontalDirectional)
            .properties(p -> p.sound(AllSoundTypes.CRUCIBLE).noOcclusion().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .recipe((c, p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                        .pattern("AAA")
                        .pattern("RDR")
                        .pattern("AAA")
                        .define('A', AllTags.itemC("ingots/deep_alloy")).define('R', AllTags.itemC("ingots/copper")).define('D', DRAIN.get())
                        .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName()));

                /*
                gemMeltable(p, "emerald", AllFluids.MOLTEN_EMERALD.getSource(), Tags.Items.STORAGE_BLOCKS_EMERALD, Tags.Items.GEMS_EMERALD, AllTags.itemC("nuggets/emerald"));
                gemMeltable(p, "lapis", AllFluids.MOLTEN_LAPIS.getSource(), Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.GEMS_LAPIS, AllTags.itemC("nuggets/lapis"));
                dustMeltable(p, "redstone", AllFluids.MOLTEN_REDSTONE.getSource(), Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Items.DUSTS_REDSTONE);
                crystalMeltable(p, "quartz", AllFluids.MOLTEN_QUARTZ.getSource(), AllTags.QUARTZ_BLOCKS, Tags.Items.GEMS_QUARTZ);
                crystalMeltable(p, "amethyst", AllFluids.MOLTEN_AMETHYST.getSource(), AllTags.AMETHYST_BLOCKS, Tags.Items.GEMS_AMETHYST);
                crystalMeltable(p, "prismarine", AllFluids.MOLTEN_PRISMARINE.getSource(), null, Tags.Items.GEMS_PRISMARINE);

                oreMeltableGem(p, "raw_emerald", AllFluids.MOLTEN_EMERALD.getSource(), null, Tags.Items.ORES_EMERALD, null);
                oreMeltableGem(p, "raw_quartz", AllFluids.MOLTEN_QUARTZ.getSource(), null, Tags.Items.ORES_QUARTZ, null);
                */

                gemMeltable(p, "diamond", AllFluids.MOLTEN_DIAMOND.getSource(), Tags.Items.STORAGE_BLOCKS_DIAMOND, Tags.Items.GEMS_DIAMOND, AllTags.itemC("nuggets/diamond"));
                ingotMeltable(p, "copper", AllFluids.MOLTEN_COPPER.getSource(), AllTags.COPPER_BLOCKS, Tags.Items.INGOTS_COPPER, AllTags.itemC("nuggets/copper"));
                ingotMeltable(p, "gold", AllFluids.MOLTEN_GOLD.getSource(), Tags.Items.STORAGE_BLOCKS_GOLD, Tags.Items.INGOTS_GOLD, Tags.Items.NUGGETS_GOLD);
                ingotMeltable(p, "iron", AllFluids.MOLTEN_IRON.getSource(), Tags.Items.STORAGE_BLOCKS_IRON, Tags.Items.INGOTS_IRON, Tags.Items.NUGGETS_IRON);
                ingotMeltable(p, "netherite", AllFluids.MOLTEN_NETHERITE.getSource(), Tags.Items.STORAGE_BLOCKS_NETHERITE, Tags.Items.INGOTS_NETHERITE, AllTags.itemC("nuggets/netherite"));
                ingotMeltable(p, "rose_gold", AllFluids.MOLTEN_ROSE_GOLD.getSource(), AllTags.itemC("storage_blocks/rose_gold"), AllTags.itemC("ingots/rose_gold"), AllTags.itemC("nuggets/rose_gold"));

                oreMeltable(p, "raw_copper", AllFluids.MOLTEN_COPPER.getSource(), Tags.Items.STORAGE_BLOCKS_RAW_COPPER, AllTags.COPPER_RAW_MATERIALS, AllTags.COPPER_CRUSHED);
                oreMeltable(p, "raw_iron", AllFluids.MOLTEN_IRON.getSource(), Tags.Items.STORAGE_BLOCKS_RAW_IRON, AllTags.IRON_RAW_MATERIALS, AllTags.IRON_CRUSHED);
                oreMeltable(p, "raw_gold", AllFluids.MOLTEN_GOLD.getSource(), Tags.Items.STORAGE_BLOCKS_RAW_GOLD, AllTags.GOLD_RAW_MATERIALS, AllTags.GOLD_CRUSHED);

                oreMeltableGem(p, "raw_diamond", AllFluids.MOLTEN_DIAMOND.getSource(), null, Tags.Items.ORES_DIAMOND, null);

                MeltingRecipeBuilder.create(Fluids.LAVA.getSource(), MelterBE.SIMPLE_BLOCK_SIZE, AllTags.MELTS_INTO_LAVA)
                        .unlockedBy("has_melts_into_lava", has(AllTags.MELTS_INTO_LAVA))
                        .save(p, SlagEmbers.loc("melting/melts_into_lava"));
                MeltingRecipeBuilder.create(Fluids.WATER.getSource(), MelterBE.SIMPLE_BLOCK_SIZE, AllTags.MELTS_INTO_WATER)
                        .unlockedBy("has_melts_into_water", has(AllTags.MELTS_INTO_WATER))
                        .save(p, SlagEmbers.loc("melting/melts_into_water"));

                ingotMeltable(p, "zinc", AllFluids.MOLTEN_ZINC.getSource(), AllTags.itemC("storage_blocks/zinc"), AllTags.itemC("ingots/zinc"), AllTags.itemC("nuggets/zinc"));
                ingotMeltable(p, "brass", AllFluids.MOLTEN_BRASS.getSource(), AllTags.itemC("storage_blocks/brass"), AllTags.itemC("ingots/brass"), AllTags.itemC("nuggets/brass"));

                oreMeltable(p, "raw_zinc", AllFluids.MOLTEN_ZINC.getSource(), AllTags.itemC("storage_blocks/raw_zinc"), AllTags.itemC("raw_materials/zinc"), AllTags.ZINC_CRUSHED);

                sheetMeltable(p, "iron", AllFluids.MOLTEN_IRON.getSource(), AllTags.itemC("plates/iron"));
                sheetMeltable(p, "gold", AllFluids.MOLTEN_GOLD.getSource(), AllTags.itemC("plates/gold"));
                sheetMeltable(p, "copper", AllFluids.MOLTEN_COPPER.getSource(), AllTags.itemC("plates/copper"));
                sheetMeltable(p, "brass", AllFluids.MOLTEN_BRASS.getSource(), AllTags.itemC("plates/brass"));

                MeltingRecipeBuilder.create(com.simibubi.create.AllFluids.CHOCOLATE.getSource(), MelterBE.SIMPLE_BLOCK_SIZE/4, com.simibubi.create.AllItems.BAR_OF_CHOCOLATE)
                        .unlockedBy("has_meltable_chocolate", has(com.simibubi.create.AllItems.BAR_OF_CHOCOLATE))
                        .save(p, SlagEmbers.loc("melting/meltable_chocolate"));

                /*
                MeltingRecipeBuilder.create(AllFluids.MOLTEN_AMETHYST.getSource(), MelterBE.INGOT_SIZE * 3, Items.LARGE_AMETHYST_BUD)
                        .unlockedBy("has_meltable_buds", has(Tags.Items.BUDS))
                        .save(p, SlagEmbers.loc("melting/large_amethyst_bud"));
                MeltingRecipeBuilder.create(AllFluids.MOLTEN_AMETHYST.getSource(), MelterBE.INGOT_SIZE * 2, Items.MEDIUM_AMETHYST_BUD)
                        .unlockedBy("has_meltable_buds", has(Tags.Items.BUDS))
                        .save(p, SlagEmbers.loc("melting/medium_amethyst_bud"));
                MeltingRecipeBuilder.create(AllFluids.MOLTEN_AMETHYST.getSource(), MelterBE.INGOT_SIZE, Items.SMALL_AMETHYST_BUD)
                        .unlockedBy("has_meltable_buds", has(Tags.Items.BUDS))
                        .save(p, SlagEmbers.loc("melting/small_amethyst_bud"));
                */
            })
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .model((c, p) -> p.withExistingParent("item/" + c.getName(), p.modLoc("block/melter")))
            .build()
            .register();

    public static final BlockEntry<InterfaceBlock> INTERFACE = REG.block("crucible_interface", InterfaceBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(AllSoundTypes.CRUCIBLE).requiresCorrectToolForDrops())
            .blockstate(BlockHelper::genHorizontalDirectional)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                    .pattern("AA")  .pattern("GG")
                    .define('A', AllTags.itemC("ingots/deep_alloy")).define('G', AllTags.itemC("ingots/brass"))
                    .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName())))
            .lang("Alloying Interface")
            .simpleItem()
            .register();

    public static final BlockEntry<Block> DEEP_ALLOY_BRICKS = REG.block("deep_alloy_bricks", Block::new)
            .lang("Deep Alloy Bricks")
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(AllSoundTypes.CRUCIBLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 4)
                    .pattern("DD").pattern("DD").define('D', AllTags.itemC("ingots/deep_alloy"))
                    .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName())))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> DEEP_ALLOY_BRICK_STAIRS = REG.block("deep_alloy_brick_stairs", p -> new StairBlock(DEEP_ALLOY_BRICKS.getDefaultState(), p))
            .lang("Deep Alloy Brick Stairs")
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(AllSoundTypes.CRUCIBLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 4)
                    .pattern("D  ").pattern("DD ").pattern("DDD").define('D', DEEP_ALLOY_BRICKS.get())
                    .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName())))
            .blockstate((c, p) -> p.stairsBlock(c.get(), SlagEmbers.loc("block/deep_alloy_bricks")))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> DEEP_ALLOY_BRICK_SLAB = REG.block("deep_alloy_brick_slab", SlabBlock::new)
            .initialProperties(() -> Blocks.DEEPSLATE_BRICK_SLAB)
            .lang("Deep Alloy Brick Slab")
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(AllSoundTypes.CRUCIBLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 6)
                    .pattern("DDD").define('D', DEEP_ALLOY_BRICKS.get())
                    .unlockedBy("has_deep_alloy", has(AllTags.itemC("ingots/deep_alloy"))).save(p, SlagEmbers.loc("crafting/" + c.getName())))
            .blockstate((c, p) -> p.slabBlock(c.get(), SlagEmbers.loc("block/deep_alloy_bricks"), SlagEmbers.loc("block/deep_alloy_bricks")))
            .loot((p, c) -> p.add(c, p.createSlabItemTable(c)))
            .simpleItem()
            .register();

    public static void register() {}
}
