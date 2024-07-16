package klepto.placed.handlers.wire_util;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import klepto.placed.registry.block.ModBlocks;

import java.util.Map;

public class WireHandler extends Block{
	public boolean emitsParticles;

	public Vector3f color;

    public ItemStack stackToBeDropped;

	private final BlockState dotState;

    private static final EnumProperty<WireConnection> NORTH = Properties.NORTH_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> EAST = Properties.EAST_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> WEST = Properties.WEST_WIRE_CONNECTION;


    private static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0);

    private static final VoxelShape NORTH_UP_SHAPE = VoxelShapes.union(NORTH_SHAPE, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0));
    private static final VoxelShape EAST_UP_SHAPE = VoxelShapes.union(EAST_SHAPE, Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0));
    private static final VoxelShape SOUTH_UP_SHAPE = VoxelShapes.union(SOUTH_SHAPE, Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0));
    private static final VoxelShape WEST_UP_SHAPE = VoxelShapes.union(WEST_SHAPE, Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0));

	public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST
		)
	);

	

    public WireHandler(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.getStateManager().getDefaultState()
                        .with(NORTH, WireConnection.NONE)
                        .with(EAST, WireConnection.NONE)
                        .with(SOUTH, WireConnection.NONE)
                        .with(WEST, WireConnection.NONE)
                        
        );

		this.dotState = this.getDefaultState()
			.with(NORTH, WireConnection.SIDE)
			.with(EAST, WireConnection.SIDE)
			.with(SOUTH, WireConnection.SIDE)
			.with(WEST, WireConnection.SIDE);
    }

    @SuppressWarnings("incomplete-switch")
    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection connection = state.get(directionToWire(direction));
            if (connection == WireConnection.SIDE) {
                switch (direction) {
                    case NORTH -> voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
                    case SOUTH -> voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
                    case EAST -> voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
                    case WEST -> voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
                }
                continue;
            }
            if (connection != WireConnection.UP) continue;
            switch (direction) {
                case NORTH -> voxelShape = VoxelShapes.union(voxelShape, NORTH_UP_SHAPE);
                case SOUTH -> voxelShape = VoxelShapes.union(voxelShape, SOUTH_UP_SHAPE);
                case EAST -> voxelShape = VoxelShapes.union(voxelShape, EAST_UP_SHAPE);
                case WEST -> voxelShape = VoxelShapes.union(voxelShape, WEST_UP_SHAPE);
            }
        }
        return voxelShape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShapeForState(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), getDefaultState(), ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            EnumProperty<WireConnection> connection = directionToWire(direction);
            if (!state.get(connection).isConnected()) {
                WireConnection wireConnection = this.getConnection(world, pos, direction);
                state = state.with(connection, wireConnection);
            }
        }

        return state;
    }

    private WireConnection getConnection(BlockView world, BlockPos pos, Direction direction) {
        BlockPos offset = pos.offset(direction);
        BlockState blockstate = world.getBlockState(offset);
        if (!world.getBlockState(pos.up()).isSolidBlock(world, pos)) {
            if (this.canTop(world, offset, blockstate) && connectsTo(world.getBlockState(offset.up()))) {
                if (blockstate.isSideSolidFullSquare(world, offset, direction.getOpposite())) {
                    return WireConnection.UP;
                }
                return WireConnection.NONE;
            }
        }

        if (connectsTo(blockstate, direction)) {
            return WireConnection.SIDE;
        }
        if (!blockstate.isSolidBlock(world, offset) && connectsTo(world.getBlockState(offset.down()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

	public static boolean stronglyConnectsTo(BlockState state) {
        return state.isOf(ModBlocks.GLOWSTONE_WIRE) || state.isOf(ModBlocks.GLOWSTONE_WIRE);
    }


    private static boolean connectsTo(BlockState state) {
        return connectsTo(state, null);
    }

    private static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (stronglyConnectsTo(state)){
			return true;
		}
       
        return dir != null;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 ->
                    state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90 ->
                    state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90 ->
                    state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default -> state;
        };
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK -> state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default -> super.mirror(state, mirror);
        };
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    private void addParticles(World world, Random random, BlockPos pos, Vector3f color, Direction direction, Direction direction2, float f, float g) {
		float h = g - f;
		if (!(random.nextFloat() >= 0.2F * h)) {
			float j = f + h * random.nextFloat();
			double d = 0.5 + (double)(0.4375F * (float)direction.getOffsetX()) + (double)(j * (float)direction2.getOffsetX());
			double e = 0.5 + (double)(0.4375F * (float)direction.getOffsetY()) + (double)(j * (float)direction2.getOffsetY());
			double k = 0.5 + (double)(0.4375F * (float)direction.getOffsetZ()) + (double)(j * (float)direction2.getOffsetZ());
			world.addParticle(new DustParticleEffect(color, 1.0F), (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + k, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (emitsParticles) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				WireConnection wireConnection = state.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
				switch (wireConnection) {
					case UP:
						this.addParticles(world, random, pos, this.color, direction, Direction.UP, -0.5F, 0.5F);
					case SIDE:
						this.addParticles(world, random, pos, this.color, Direction.DOWN, direction, 0.0F, 0.5F);
						break;
					case NONE:
					default:
						this.addParticles(world, random, pos, this.color, Direction.DOWN, direction, 0.0F, 0.3F);
				}
			}
		}
	}

    

    

    

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos down = pos.down();
        BlockState downState = world.getBlockState(down);
        return canTop(world, down, downState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            if (!this.canTop(world, neighborPos, neighborState)) {
                return Blocks.AIR.getDefaultState();
            }
            return state;
        }

        if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        }

        WireConnection wireConnection = this.getConnection(world, pos, direction);
        return state.with(directionToWire(direction), wireConnection);
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection connection = state.get(directionToWire(direction));

            if (connection == WireConnection.NONE || stronglyConnectsTo(world.getBlockState(pos.offset(direction)))) continue;

            BlockPos downSide = pos.offset(direction).down();
            BlockState downSideState = world.getBlockState(downSide);

            if (stronglyConnectsTo(downSideState)) {
                BlockPos down = pos.down();
                world.replaceWithStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(down), downSide, down, flags, maxUpdateDepth);
            }

            BlockPos upSide = pos.offset(direction).up();
            BlockState upSideState = world.getBlockState(upSide);

            if (!stronglyConnectsTo(upSideState)) continue;

            BlockPos up = pos.down();
            world.replaceWithStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(up), upSide, up, flags, maxUpdateDepth);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock()) || world.isClient) {
            return;
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.updateDeepNeighbors(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, false);
        if (world.isClient) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.updateDeepNeighbors(world, pos);
    }

    private void updateDeepNeighbors(World world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos offset = pos.offset(direction);
            if (world.getBlockState(offset).isSolidBlock(world, offset)) {
                this.updateNeighbors(world, offset.up());
                continue;
            }
            this.updateNeighbors(world, offset.down());
        }
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (!stronglyConnectsTo(world.getBlockState(pos))) {
            return;
        }
        world.updateNeighborsAlways(pos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

   
	private static boolean isFullyConnected(BlockState state) {
		return ((WireConnection)state.get(NORTH)).isConnected()
			&& ((WireConnection)state.get(SOUTH)).isConnected()
			&& ((WireConnection)state.get(EAST)).isConnected()
			&& ((WireConnection)state.get(WEST)).isConnected();
	}

	private static boolean isNotConnected(BlockState state) {
		return !((WireConnection)state.get(NORTH)).isConnected()
			&& !((WireConnection)state.get(SOUTH)).isConnected()
			&& !((WireConnection)state.get(EAST)).isConnected()
			&& !((WireConnection)state.get(WEST)).isConnected();
	}
    

    @Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!player.getAbilities().allowModifyWorld) {
			return ActionResult.PASS;
		} else {
			if (isFullyConnected(state) || isNotConnected(state)) {
				BlockState blockState = isFullyConnected(state) ? this.getDefaultState() : this.dotState;
				blockState = this.getPlacementState(world, blockState, pos);
				if (blockState != state) {
					world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
					this.updateForNewState(world, pos, state, blockState);
					return ActionResult.SUCCESS;
				}
			}

			return ActionResult.PASS;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (((WireConnection)oldState.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()
					!= ((WireConnection)newState.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()
				&& world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
				world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite());
			}
		}
	}

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient)
            return;
        if (state.canPlaceAt(world, pos)) {
            for (Direction dir : Direction.values()) {
                EnumProperty<WireConnection> connection = directionToWire(dir);
                if (connection != null && !state.get(connection).isConnected())
                    continue;
                
            }
        } else {
            WireHandler.dropStack(world, pos, stackToBeDropped);
            world.removeBlock(pos, false);
        }
    }

    

    private boolean canTop(BlockView world, BlockPos pos, BlockState state) {
        return state.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    private EnumProperty<WireConnection> directionToWire(Direction direction) {
        return switch (direction) {
            case DOWN, UP -> null;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    /* 
    private Direction wireToDirection(EnumProperty<WireConnection> wire) {
        if (wire == NORTH)
            return Direction.NORTH;
        if (wire == SOUTH)
            return Direction.SOUTH;
        if (wire == EAST)
            return Direction.EAST;
        if (wire == WEST)
            return Direction.WEST;
        return null;
    }
    */
}