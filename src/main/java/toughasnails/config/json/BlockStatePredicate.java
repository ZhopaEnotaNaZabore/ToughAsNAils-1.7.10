package toughasnails.config.json;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.gson.annotations.SerializedName;

import net.minecraft.block.Block;

/** Matches a legacy block and, optionally, selected bits of its metadata. */
public class BlockStatePredicate implements Predicate<Block>
{
    @SerializedName("block")
    private String blockRegistryName;
    private Integer metadata;
    private Integer metadataMask;

    public BlockStatePredicate(Block block)
    {
        this(block, 0, 0);
    }

    public BlockStatePredicate(Block block, int metadata, int metadataMask)
    {
        String name = (String) Block.blockRegistry.getNameForObject(block);
        this.blockRegistryName = name == null ? "" : name;
        this.metadata = metadata;
        this.metadataMask = metadataMask;
    }

    @Override
    public boolean apply(@Nullable Block input)
    {
        return input != null && input == getBlock();
    }

    public boolean matches(Block input, int inputMetadata)
    {
        if (!apply(input))
            return false;
        int mask = metadataMask == null ? 0 : metadataMask;
        return mask == 0 || (inputMetadata & mask) == (metadata == null ? 0 : metadata & mask);
    }

    public Block getBlock()
    {
        return (Block) Block.blockRegistry.getObject(blockRegistryName);
    }

    public String getBlockRegistryName()
    {
        return blockRegistryName;
    }
}
