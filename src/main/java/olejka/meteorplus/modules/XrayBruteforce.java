package olejka.meteorplus.modules;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.render.search.SBlock;
import meteordevelopment.meteorclient.systems.modules.render.search.SBlockData;
import meteordevelopment.meteorclient.systems.modules.render.search.SChunk;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.UnorderedArrayList;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.meteorclient.utils.world.TickRate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import olejka.meteorplus.MeteorPlus;

import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import olejka.meteorplus.utils.GenerationBlock;
import olejka.meteorplus.utils.XBlock;
import olejka.meteorplus.utils.XChunk;
import olejka.meteorplus.utils.XGroup;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class XrayBruteforce extends Module {
    public XrayBruteforce() {
        super(MeteorPlus.CATEGORY, "xray-bruteForce", "Bypasses anti-xray.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgSVisual = settings.createGroup("Scaner");
    private final SettingGroup sgRVisual = settings.createGroup("Scaner Render Visuals");
	private final SettingGroup sgSRenderer = settings.createGroup("Scanned Renderer");

	public final Setting<Boolean> new_render = sgSRenderer.add(new BoolSetting.Builder()
		.name("New Render")
		.description("New Render.")
		.defaultValue(true)
		.build()
	);

    private final Setting<List<Block>> whblocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("whitelist")
        .description("Which blocks to show x-rayed.")
        .defaultValue(
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.ANCIENT_DEBRIS
        )
        .onChanged(v -> scanned.clear())
        .build()
    );


	private final Setting<SBlockData> defaultBlockConfig = sgGeneral.add(new GenericSetting.Builder<SBlockData>()
		.name("whitelist-default-config")
		.description("Default block config.")
		.defaultValue(
			new SBlockData(
				ShapeMode.Lines,
				new SettingColor(0, 255, 200),
				new SettingColor(0, 255, 200, 25),
				false,
				new SettingColor(0, 255, 200, 125)
			)
		)
		.build()
	);

	private final Setting<Map<Block, SBlockData>> blockConfigs = sgGeneral.add(new BlockDataSetting.Builder<SBlockData>()
		.name("whitelist-block-configs")
		.description("Config for each block.")
		.defaultData(defaultBlockConfig)
		.onChanged(v -> {
			synchronized (ores) {
				for (RenderOre ore : ores) {
					SBlockData data = getBlockData(ore.block);
					ore.linecolor = data.lineColor;
					ore.sidecolor = data.sideColor;
					ore.tracercolor = data.tracerColor;
					ore.shapeMode = data.shapeMode;
				}
			}
		})
		.build()
	);

    private final Setting<PacketMode> packet_first = sgGeneral.add(new EnumSetting.Builder<PacketMode>()
        .name("packet-#1")
        .description("First packet.")
        .defaultValue(PacketMode.Start)
        .build()
    );

	private final Setting<PacketMode> packet_two = sgGeneral.add(new EnumSetting.Builder<PacketMode>()
		.name("packet-#2")
		.description("Two packet.")
		.defaultValue(PacketMode.Abort)
		.build()
	);

	private final Setting<GenerationType> generationType = sgGeneral.add(new EnumSetting.Builder<GenerationType>()
		.name("Generation-type")
		.description("Ores generation type.")
		.defaultValue(GenerationType.Old)
		.build()
	);

    public enum PacketMode
    {
		None,
        Start,
        Abort,
		Stop,
    }

    public final Setting<Boolean> clear_cache_blocks = sgGeneral.add(new BoolSetting.Builder()
        .name("Clear-cache")
        .description("Clear saved cache.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> clear_cache_ores = sgGeneral.add(new BoolSetting.Builder()
        .name("Clear-rendered-ores-cache")
        .description("Clear saved ores cache.")
        .defaultValue(false)
        .build()
    );

	public final Setting<Boolean> tps_sync = sgGeneral.add(new BoolSetting.Builder()
		.name("TPS-sync")
		.description("TPS sync scaning.")
		.defaultValue(true)
		.build()
	);

    public final Setting<Boolean> fps_sync = sgGeneral.add(new BoolSetting.Builder()
        .name("FPS-sync")
        .description("FPS sync scaning.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> auto_height = sgSVisual.add(new BoolSetting.Builder()
        .name("Auto-height")
        .description("Auto detect height.")
        .defaultValue(false)
        .build()
    );

	public final Setting<Boolean> auto_dimension = sgSVisual.add(new BoolSetting.Builder()
		.name("Auto-dimension")
		.description("Auto detect dimension.")
		.defaultValue(true)
		.visible(auto_height::get)
		.build()
	);

	public final Setting<Boolean> expanded = sgSVisual.add(new BoolSetting.Builder()
		.name("exposed")
		.description("Scan exposed blocks.")
		.defaultValue(true)
		.build()
	);

	private final Setting<ScanPriority> scanPriority = sgSVisual.add(new EnumSetting.Builder<ScanPriority>()
		.name("Scan priority")
		.description("Ores generation type.")
		.defaultValue(ScanPriority.Normal)
		.build()
	);

	private final Setting<Integer> cavesRange = sgSVisual.add(new IntSetting.Builder()
		.name("Caves range")
		.description("Caves range.")
		.defaultValue(2)
		.visible(() -> scanPriority.get() == ScanPriority.Caves)
		.build()
	);

	private final Setting<Integer> cavesRangeY = sgSVisual.add(new IntSetting.Builder()
		.name("Caves Y range")
		.description("Caves y range.")
		.defaultValue(2)
		.visible(() -> scanPriority.get() == ScanPriority.Caves)
		.build()
	);

	public enum ScanPriority {
		Caves,
		Normal
	}

    private final Setting<Integer> range = sgSVisual.add(new IntSetting.Builder()
        .name("range")
        .description("Bruteforce range.")
        .defaultValue(40)
        .min(3)
        .sliderRange(3, 512)
        .build()
    );
    private final Setting<Integer> y_range = sgSVisual.add(new IntSetting.Builder()
        .name("y-range")
        .description("Bruteforce range.")
        .defaultValue(13)
        .min(3)
        .sliderRange(3, 255)
        .build()
    );

    private final Setting<Integer> delaymin = sgGeneral.add(new IntSetting.Builder()
        .name("Scan delay min")
        .description("Bruteforce delay min .")
        .defaultValue(30)
        .min(0)
        .sliderRange(0, 150)
        .build()
    );

	private final Setting<Integer> delaymax = sgGeneral.add(new IntSetting.Builder()
		.name("Scan delay max")
		.description("Bruteforce delay max .")
		.defaultValue(35)
		.min(0)
		.sliderRange(0, 150)
		.build()
	);

	private final Setting<Boolean> pauseBind = sgGeneral.add(new BoolSetting.Builder()
		.name("pause-bind")
		.description("Allow use pause bind.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Keybind> pausekeybind = sgGeneral.add(new KeybindSetting.Builder()
		.name("pause-keybind")
		.description("The bind for pause.")
		.defaultValue(Keybind.fromKey(GLFW_KEY_X))
		.visible(pauseBind::get)
		.build()
	);

	private final Setting<Boolean> outline = sgRVisual.add(new BoolSetting.Builder()
		.name("outline")
		.description("Outline to block.")
		.defaultValue(true)
		.build()
	);

	private final Setting<SettingColor> outlineColor = sgRVisual.add(new ColorSetting.Builder()
		.name("outline-color")
		.description("Outline color to block.")
		.defaultValue(new SettingColor(255, 255, 0, 255))
		.visible(outline::get)
		.build()
	);

	private final Setting<Boolean> tracer = sgRVisual.add(new BoolSetting.Builder()
		.name("tracer")
		.description("Tracer to block.")
		.defaultValue(true)
		.build()
	);

	private final Setting<SettingColor> tracerColor = sgRVisual.add(new ColorSetting.Builder()
		.name("tracer-color")
		.description("Tracer color to block.")
		.defaultValue(new SettingColor(255, 255, 0, 255))
		.visible(tracer::get)
		.build()
	);

	private final Setting<Integer> clusterRange = sgSVisual.add(new IntSetting.Builder()
		.name("Cluster range")
		.description("Cluster range.")
		.defaultValue(2)
		.min(1)
		.sliderRange(1, 2)
		.build()
	);

	private boolean isPressed() {
		return (pausekeybind.get().isPressed() && pauseBind.get());
	}

    private BlockPos currentScanBlock;

	private boolean pause_toggle = false;

	@EventHandler
	private void onKeyEvent(KeyEvent event)
	{
		if (event.action == KeyAction.Press && isPressed()) {
			pause_toggle = !pause_toggle;
			if (pause_toggle) {
				currentScanBlock = null;
				info("§c" + "Paused");
			}
			else {
				info("§a" + "Un paused");
			}
		}
	}

	public class RenderOre {
        public Block block = Blocks.AIR;
        public BlockPos blockPos = null;

		public XBlock sBlock = null;

		/* Visual settings */
		public SettingColor linecolor = null;
		public SettingColor sidecolor = null;
		public SettingColor tracercolor = null;
		public ShapeMode shapeMode = null;
    }

	public ArrayList<XBlock> oresV2 = new ArrayList<XBlock>();
	private final List<XGroup> groups = new UnorderedArrayList<>();

	public XBlock getSBlockData(BlockPos pos) {
		for (XBlock ore : oresV2.toArray(new XBlock[0])) {
			if (ore.x == pos.getX() && ore.y == pos.getY() && ore.z == pos.getZ()) {
				return ore;
			}
		}
		return null;
	}

	public XGroup newGroup(Block block) {
		synchronized (chunks) {
			XGroup group = new XGroup(block);
			groups.add(group);
			return group;
		}
	}

	public void removeGroup(XGroup group) {
		synchronized (chunks) {
			groups.remove(group);
		}
	}

	private final Long2ObjectMap<XChunk> chunks = new Long2ObjectOpenHashMap<>();

	public XBlock getBlock(int x, int y, int z) {
		XChunk chunk = chunks.get(ChunkPos.toLong(x >> 4, z >> 4));
		return chunk == null ? null : chunk.get(x, y, z);
	}

	private void setColors(RenderOre ore)
	{
		if (ore != null && ore.block != null && ore.linecolor == null && ore.sidecolor == null && ore.tracercolor == null && ore.shapeMode == null && ore.sBlock == null) {
			assert mc.world != null;
			BlockState state = mc.world.getBlockState(ore.blockPos);
			if (ore.block == Blocks.AIR) {
				ore.block = state.getBlock();
			}
			SBlockData blockdata = getBlockData(state.getBlock());
			XBlock sbp = new XBlock(ore.blockPos.getX(), ore.blockPos.getY(), ore.blockPos.getZ());

			ore.sBlock = sbp;
			ore.sBlock.update();
			ore.linecolor = blockdata.lineColor;
			ore.sidecolor = blockdata.sideColor;
			ore.tracercolor = blockdata.tracerColor;
			ore.shapeMode = blockdata.shapeMode;
		}
	}

    private static final List<RenderOre> ores = new ArrayList<>();
    private RenderOre get(BlockPos pos) {
		synchronized (ores) {
			for (RenderOre cur : ores) {
				if (cur.block != null && cur.blockPos.equals(pos)) {
					return cur;
				}
			}
			return null;
		}
	}
    private void addRenderBlock(BlockPos blockPos) {
        synchronized (ores) {
            RenderOre ore = get(blockPos);
            if (ore == null) {
                RenderOre ne = new RenderOre();
                ne.blockPos = blockPos;
                ores.add(ne);
            }
			else if (ore.sBlock != null) {
				ore.sBlock.update();
			}
        }
    }

	public enum GenerationType {
		Old,
		New,
	}

	public ArrayList<BlockScanned> need_rescan = new ArrayList<BlockScanned>();

	public class BlockScanned {
		public BlockPos pos;
		public long rescanTime;
	}

	private void addNeedRescan(BlockPos pos, int delay) {
		synchronized (need_rescan) {
			BlockScanned scanned = new BlockScanned();
			scanned.pos = pos;
			scanned.rescanTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + delay;
			need_rescan.add(scanned);
		}
	}

    @EventHandler
    public void blockUpdateEvent(BlockUpdateEvent event) {
		if (event.oldState.getMaterial() != Material.AIR) {
			if (!scanned.contains(event.pos)) {
				scanned.add(event.pos);
			}
			BlockState state = mc.world.getBlockState(event.pos);
			if (whblocks.get().contains(state.getBlock())) {
				addNeedRescan(event.pos, 1250);
			}
		}
    }

    @EventHandler
    private void minedBlock(BreakBlockEvent event) {
        new Thread(() -> {
            RenderOre ore = get(event.blockPos);
            if (ore != null) {
                ore.block = Blocks.AIR;
                ores.remove(ore);
            }
        }).start();
    }

    private void addBlock(BlockPos pos, Boolean ignore) {
        if (!scanned.contains(pos) && !ignore) {
            blocks.add(pos);
			scanned.add(pos);
        } else if (ignore) {
			blocks.add(pos);
			if (!scanned.contains(pos)) {
				scanned.add(pos);
			}
		}
    }

	public SBlockData getBlockData(Block block) {
		SBlockData blockData = blockConfigs.get().get(block);
		return blockData == null ? defaultBlockConfig.get() : blockData;
	}

	private void updateRenderedOres() {
		if (ores.size() > 0) {
			for (RenderOre pos : ores.toArray(new RenderOre[0])) {
				BlockState state = mc.world.getBlockState(pos.blockPos);
				if (state.getBlock() == pos.block) {
					pos.sBlock.update();
				}
			}
		}
	}

    private void renderOres(Render3DEvent event) {
		int renderBlocks = 0;
		if (ores.size() > 0) {
			for (RenderOre pos : ores.toArray(new RenderOre[0])) {
				BlockState state = mc.world.getBlockState(pos.blockPos);
				setColors(pos);
				if (!new_render.get()) {
					if (EntityUtils.isInRenderDistance(pos.blockPos) && pos.block != null && whblocks.get().contains(pos.block)) {
						renderOreBlock(event, pos);
						renderBlocks++;
					}
				}
				else {
					if (pos.sBlock != null) {
						if (EntityUtils.isInRenderDistance(pos.blockPos) && pos.block != null && whblocks.get().contains(pos.block)) {
							pos.sBlock.render(event, pos);
							renderBlocks++;
						}
					}
				}
			}
		}
		renderedBlocks = renderBlocks;
	}
    private int renderedBlocks = 0;
    private void renderOreBlock(Render3DEvent event, RenderOre ore)
    {
        if (ore.block != null && mc.world != null && ore.tracercolor != null && ore.sidecolor != null && ore.linecolor != null) {
			BlockState state = mc.world.getBlockState(ore.blockPos);
            VoxelShape shape = state.getOutlineShape(mc.world, ore.blockPos);
			SBlockData blockdata = getBlockData(ore.block);
			if (shape.isEmpty()) return;
            for (Box b : shape.getBoundingBoxes()) {
                event.renderer.box(ore.blockPos.getX() + b.minX, ore.blockPos.getY() + b.minY, ore.blockPos.getZ() + b.minZ, ore.blockPos.getX() + b.maxX, ore.blockPos.getY() + b.maxY, ore.blockPos.getZ() + b.maxZ, ore.sidecolor, ore.linecolor, blockdata.shapeMode, 0);
            }
			if (blockdata.tracer) {
				event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, ore.blockPos.getX(), ore.blockPos.getY(), ore.blockPos.getZ(), ore.tracercolor);
			}
        }
    }
    private boolean lagging = false;
    @EventHandler
    private void render2d(Render2DEvent event)
    {
        lagging = false;
    }
    @EventHandler
    private void onRender(Render3DEvent event) {
        if (clear_cache_blocks.get())
        {
            scanned.clear();
            clear_cache_blocks.set(false);
            ChatUtils.info("Xray BruteForce", "Cache checked blocks cleared");
        }
        if (clear_cache_ores.get())
        {
            ores.clear();
            clear_cache_ores.set(false);
            ChatUtils.info("Xray BruteForce", "Cache render ores cleared");
        }
        renderOres(event);
        if (currentScanBlock != null) {
            BlockPos bp = currentScanBlock;
			assert mc.world != null;
			BlockState state = mc.world.getBlockState(bp);
            VoxelShape shape = state.getOutlineShape(mc.world, bp);

            if (shape.isEmpty()) return;
            if (outline.get()) {
                for (Box b : shape.getBoundingBoxes()) {
                    event.renderer.box(bp.getX() + b.minX, bp.getY() + b.minY, bp.getZ() + b.minZ, bp.getX() + b.maxX, bp.getY() + b.maxY, bp.getZ() + b.maxZ, new SettingColor(255, 255, 255, 255), outlineColor.get(), ShapeMode.Lines, 0);
                }
            }
            if (tracer.get()) {
                event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, bp.getX(), bp.getY(), bp.getZ(), tracerColor.get());
            }
        }
    }

	private void addExposedBlocks() {
		if (mc.world != null) {
			Iterable<Chunk> chunks = Utils.chunks();
			for (Chunk chunk : chunks) {
				if (expanded.get()) {
					SChunk s = SChunk.searchChunk(chunk, whblocks.get());
					if (s.blocks != null) {
						for (SBlock sBlock : s.blocks.values()) {
							BlockPos pos = new BlockPos(sBlock.x, sBlock.y, sBlock.z);
							if (whblocks.get().contains(mc.world.getBlockState(pos).getBlock())) {
								if (isExposedOre(pos)) {
									if (auto_dimension.get()) {
										GenerationBlock generationBlock = GenerationBlock.getGenerationBlock(mc.world.getBlockState(pos).getBlock(), false);
										if (generationBlock != null && generationBlock.dimension == PlayerUtils.getDimension()) {
											addRenderBlock(pos);
											addBlock(pos, false);
											List<BlockPos> post = getBlocks(pos, clusterRange.get(), clusterRange.get());
											for (BlockPos pos2 : post) {
												addBlock(pos2, false);
											}
										}
									} else {
										addRenderBlock(pos);
										addBlock(pos, false);
										List<BlockPos> post = getBlocks(pos, clusterRange.get(), clusterRange.get());
										for (BlockPos pos2 : post) {
											addBlock(pos2, false);
										}
									}
								}
							}
						}
					}
				}
				if (scanPriority.get() == ScanPriority.Caves) {
					ArrayList<Block> caf = new ArrayList<Block>();
					caf.add(Blocks.AIR);
					SChunk s = SChunk.searchChunk(chunk, caf);
					if (s.blocks != null) {
						for (SBlock sBlock : s.blocks.values()) {
							for (Block bp : whblocks.get()) {
								boolean newGeneration = generationType.get() == GenerationType.New;
								GenerationBlock generationBlock = GenerationBlock.getGenerationBlock(bp, newGeneration);
								if (generationBlock != null) {
									BlockPos pos = new BlockPos(sBlock.x, sBlock.y, sBlock.z);
									if (sBlock.y >= generationBlock.min_height && sBlock.y <= generationBlock.max_height) {
										List<BlockPos> post = getBlocks(pos, cavesRange.get(), cavesRangeY.get());
										for (BlockPos pos2 : post) {
											if (!isExposedOre(pos2) && pos2.getY() >= generationBlock.min_height && pos2.getY() <= generationBlock.max_height) {
												addBlock(pos2, false);
											}
										}
									}
								}
							}
						}
					}
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {

				}
			}
		}
	}

	private boolean isExposedBlock(Block block) {
		return (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA);
	}

	private boolean isExposedBlock(BlockState state) {
		Block block = state.getBlock();
		return isExposedBlock(block);
	}

	private boolean isExposedOre(BlockPos pos) {
		if (mc.world != null) {
			BlockState def = mc.world.getBlockState(pos);
			if (whblocks.get().contains(def.getBlock())) {
				if (isExposedBlock(mc.world.getBlockState(pos.add(0, 1, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(0, -1, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(1, 0, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(-1, 0, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(0, 0, 1))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(-0, 0, -1))))
					return true;
			}
		}
		return false;
	}

    private List<BlockPos> blocks = new ArrayList<>();
    @Override
    public String getInfoString() {
		if (pause_toggle) {
			return "paused | " + Integer.toString(renderedBlocks);
		}
        else {
			return Integer.toString(renderedBlocks);
		}
    }

    private boolean send(BlockPos blockpos)
    {
		boolean sucess = true;
        if (blockpos == null) {
			sucess = false;
		}
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn == null) {
			sucess = false;
		}
		if (mc.world == null) {
			sucess = false;
		}
		else {
			BlockState state = mc.world.getBlockState(blockpos);
			if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER) {
				sucess = false;
			}
		}

		if (sucess) {
			currentScanBlock = blockpos;
			PlayerActionC2SPacket packet_one = getPacket(blockpos, packet_first);
			if (packet_one != null) {
				conn.sendPacket(packet_one);
			}
			PlayerActionC2SPacket packet_tw = getPacket(blockpos, packet_two);
			if (packet_tw != null) {
				conn.sendPacket(packet_tw);
			}
			addNeedRescan(blockpos, 2500);
		}
		else {
			currentScanBlock = null;
		}
		scanned.add(blockpos);
        return sucess;
    }

	private PlayerActionC2SPacket getPacket(BlockPos blockpos, Setting<PacketMode> setting) {
		if (setting.get() == PacketMode.Abort) {
			PlayerActionC2SPacket abort = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, new BlockPos(blockpos), Direction.UP);
			return abort;
		}
		else if (setting.get() == PacketMode.Start) {
			PlayerActionC2SPacket start = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, new BlockPos(blockpos), Direction.UP);
			return start;
		}
		else if (setting.get() == PacketMode.Stop) {
			PlayerActionC2SPacket stop = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(blockpos), Direction.UP);
			return stop;
		}
		return null;
	}

    long millis = 0;
    private void checker()
    {
		float timeSinceLastTick = TickRate.INSTANCE.getTimeSinceLastTick();
        if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() >= millis && !pause_toggle)
        {
            if (blocks != null && blocks.size() > 0) {
				if (tps_sync.get() && timeSinceLastTick <= 1f) {
					work();
				}
				else if (!tps_sync.get()) {
					work();
				}
            }
            else
            {
				if (tps_sync.get() && timeSinceLastTick <= 1f) {
					addRandomBlock();
					work();
				}
				else if (!tps_sync.get()) {
					addRandomBlock();
					work();
				}
            }
        }
    }

    private void work()
    {
        try {
            Iterator<BlockPos> blocksIterator = blocks.iterator();
            if (blocksIterator.hasNext()) {
                if (fps_sync.get()) {
                    if (!lagging) {
                        BlockPos block = blocksIterator.next();
                        blocksIterator.remove();
                        if (send(block)) {
                            millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + Utils.random(delaymin.get(), delaymax.get());
                            lagging = true;
                        }
                    }
                }
                else
                {
                    BlockPos block = blocksIterator.next();
                    blocksIterator.remove();
                    if (send(block)) {
                        millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + Utils.random(delaymin.get(), delaymax.get());
                        lagging = true;
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    private void addRandomBlock() {
		assert mc.player != null;
		int x = Utils.random(mc.player.getBlockPos().getX() -range.get(), mc.player.getBlockPos().getX() + range.get());
        int y;
        int z = Utils.random(mc.player.getBlockPos().getZ() -range.get(), mc.player.getBlockPos().getZ() + range.get());

        if (auto_height.get())
        {
			List<Block> findBlocks = whblocks.get();
			boolean newGeneration = generationType.get() == GenerationType.New;
			for (Block block : findBlocks) {
				GenerationBlock b = GenerationBlock.getGenerationBlock(block, newGeneration);
				if (b != null) {
					y = Utils.random(b.min_height, b.max_height);
					if (auto_dimension.get() && PlayerUtils.getDimension() == b.dimension) {
						addBlock(new BlockPos(x, y, z), false);
					}
					else if (!auto_dimension.get()) {
						addBlock(new BlockPos(x, y, z), false);
					}
				}
				else {
					y = Utils.random(mc.player.getBlockPos().getY() -y_range.get(), mc.player.getBlockPos().getY() + y_range.get());
					if (!scanned.contains(new BlockPos(x, y, z))) {
						if (auto_dimension.get() && PlayerUtils.getDimension() == Dimension.Overworld) {
							addBlock(new BlockPos(x, y, z), false);
						}
						else if (!auto_dimension.get()) {
							addBlock(new BlockPos(x, y, z), false);
						}
					}
				}
			}
        }
		else {
			y = Utils.random(mc.player.getBlockPos().getY() -y_range.get(), mc.player.getBlockPos().getY() + y_range.get());
			if (!scanned.contains(new BlockPos(x, y, z))) {
				if (auto_dimension.get() && PlayerUtils.getDimension() == Dimension.Overworld) {
					addBlock(new BlockPos(x, y, z), false);
				}
				else if (!auto_dimension.get()) {
					addBlock(new BlockPos(x, y, z), false);
				}
			}
		}
    }
    private Thread clickerThread;
	private void reScaner()
	{
		synchronized (need_rescan) {
			Iterator<BlockScanned> iterator = need_rescan.iterator();
			while (iterator.hasNext()) {
				BlockScanned blockscanned = iterator.next();
				if (!scanned.contains(blockscanned.pos)) {
					scanned.add(blockscanned.pos);
				}
				if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() >= blockscanned.rescanTime && mc.world != null) {
					BlockState state = mc.world.getBlockState(blockscanned.pos);
					if (whblocks.get().contains(state.getBlock())) {
						addRenderBlock(blockscanned.pos);
						for (BlockPos pos : getBlocks(blockscanned.pos, clusterRange.get(), clusterRange.get())) {
							addBlock(pos, true);
							addRenderBlock(blockscanned.pos);
						}
					}
					iterator.remove();
				}
			}
		}
	}
	private Thread exposedthread = null;
    @Override
    public void onActivate() {
		scan = true;
        millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        clickerThread = new Thread(() -> {
			addRandomBlock();
            while (scan)
            {
                checker();
				reScaner();
            }
        });
		clickerThread.start();

		exposedthread = new Thread(() -> {
			while (scan)
			{
				addExposedBlocks();
				updateRenderedOres();
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {

				}
			}
		});
		exposedthread.start();
    }
	private boolean scan = false;
    @Override
    public void onDeactivate() {
		scan = false;
        blocks.clear();
        currentScanBlock = null;
        if (clickerThread != null)
        {
            clickerThread = null;
        }
		if (exposedthread != null)
		{
			exposedthread = null;
		}
    }

    private static final List<BlockPos> scanned = new ArrayList<>();
    private boolean calculating = false;

    private List<BlockPos> getBlocks(BlockPos startPos, int y_radius, int radius)
    {
        List<BlockPos> temp = new ArrayList<>();
        calculating = true;
        for (int dy = -y_radius; dy <= y_radius; dy++) {
            if ((startPos.getY() + dy) < 1 || (startPos.getY() + dy) > 255) continue;
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    BlockPos blockPos = new BlockPos(startPos.getX() + dx, startPos.getY() + dy, startPos.getZ() + dz);
                    if (EntityUtils.isInRenderDistance(blockPos)) {
						assert mc.world != null;
						BlockState state = mc.world.getBlockState(blockPos);
                        if (state.getMaterial() == Material.STONE) {
                            if (!scanned.contains(blockPos)) {
                                temp.add(blockPos);
                            }
                        }
                    }
                }
            }
        }
        calculating = false;
        return temp;
    }
}
