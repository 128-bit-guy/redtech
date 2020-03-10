package _128_bit_guy.redtech.common.mixin;

import _128_bit_guy.redtech.common.part.DynamicRedstonePart;
import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.property.MultipartProperties;
import alexiil.mc.lib.multipart.api.property.MultipartPropertyContainer;
import alexiil.mc.lib.multipart.impl.MultipartBlock;
import alexiil.mc.lib.multipart.impl.MultipartBlockEntity;
import alexiil.mc.lib.multipart.impl.PartHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.stream.Collectors;

@Mixin(MultipartBlock.class)
public class MultipartBlockMixin extends Block {
    public MultipartBlockMixin(Settings settings) {
        super(settings);
    }

    /**
     * @author 128_bit_guy
     */
    @Overwrite
    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction oppositeFace) {
        BlockEntity be = view.getBlockEntity(pos);
        if (be instanceof MultipartBlockEntity) {
            int result = ((MultipartBlockEntity) be).getContainer().getProperties()
                .getValue(MultipartProperties.getStrongRedstonePower(oppositeFace.getOpposite()));
            for(AbstractPart part : ((MultipartBlockEntity)be).getContainer().parts.stream().map(PartHolder::getPart).collect(Collectors.toList())) {
                if(part instanceof DynamicRedstonePart) {
                    result = Math.max(result, ((DynamicRedstonePart)part).getStrongRedstonePower(oppositeFace.getOpposite()));
                }
            }
            return result;
        }
        return 0;
    }

    /**
     * @author 128_bit_guy
     */
    @Overwrite
    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction oppositeFace) {
        BlockEntity be = view.getBlockEntity(pos);
        if (be instanceof MultipartBlockEntity) {
            MultipartPropertyContainer properties = ((MultipartBlockEntity) be).getContainer().getProperties();
            int result = Math.max(
                properties.getValue(MultipartProperties.getStrongRedstonePower(oppositeFace.getOpposite())), //
                properties.getValue(MultipartProperties.getWeakRedstonePower(oppositeFace.getOpposite()))
            );
            for(AbstractPart part : ((MultipartBlockEntity)be).getContainer().parts.stream().map(PartHolder::getPart).collect(Collectors.toList())) {
                if(part instanceof DynamicRedstonePart) {
                    result = Math.max(result, ((DynamicRedstonePart)part).getWeakRedstonePower(oppositeFace.getOpposite()));
                    result = Math.max(result, ((DynamicRedstonePart)part).getStrongRedstonePower(oppositeFace.getOpposite()));
                }
            }
            return result;
        }
        return 0;
    }
}
