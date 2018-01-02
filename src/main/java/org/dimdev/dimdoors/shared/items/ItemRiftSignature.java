package org.dimdev.dimdoors.shared.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.DimDoors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.dimdev.dimdoors.shared.RotatedLocation;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.GlobalDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.sound.ModSounds;

import java.util.List;

import static org.dimdev.ddutils.I18nUtils.translateAndAdd;

public class ItemRiftSignature extends Item {
    public static final String ID = "rift_signature";

    public ItemRiftSignature() {
        setMaxStackSize(1);
        setMaxDamage(1);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("destination");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        // Return false on the client side to pass this request to the server
        if (world.isRemote) {
            return EnumActionResult.FAIL;
        }

        // Fail if the player can't place a block there TODO: spawn protection, other plugin support
        if (!player.canPlayerEdit(pos, side.getOpposite(), stack)) {
            return EnumActionResult.PASS;
        }
        pos = pos.offset(side);

        RotatedLocation source = getSource(stack);

        if (source != null) {
            // Place a rift at the destination point
            world.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            TileEntityRift rift1 = (TileEntityRift) world.getTileEntity(pos);
            rift1.setSingleDestination(new GlobalDestination(source.getLocation()));
            rift1.setRotation(source.getYaw(), 0);
            rift1.register();

            // Place a rift at the source point
            World sourceWorld = source.getLocation().getWorld();
            sourceWorld.setBlockState(source.getLocation().getPos(), ModBlocks.RIFT.getDefaultState());
            TileEntityRift rift2 = (TileEntityRift) source.getLocation().getTileEntity();
            rift2.setSingleDestination(new GlobalDestination(rift1.getLocation()));
            rift2.setRotation(source.getYaw(), 0);
            rift2.register();

            stack.damageItem(1, player); // TODO: calculate damage based on position?

            clearSource(stack);
            DimDoors.chat(player, "Rift Created");
            world.playSound(player, player.getPosition(), ModSounds.RIFT_END, SoundCategory.BLOCKS, 0.6f, 1);
        } else {
            // The link signature has not been used. Store its current target as the first location.
            setSource(stack, new RotatedLocation(new Location(world, pos), player.rotationYaw));
            DimDoors.chat(player, "Location Stored in Rift Signature");
            world.playSound(player, player.getPosition(), ModSounds.RIFT_START, SoundCategory.BLOCKS, 0.6f, 1);
        }

        return EnumActionResult.SUCCESS;
    }

    public static void setSource(ItemStack itemStack, RotatedLocation destination) {
        if (!itemStack.hasTagCompound()) itemStack.setTagCompound(new NBTTagCompound());
        itemStack.getTagCompound().setTag("destination", destination.writeToNBT(new NBTTagCompound()));
    }

    public static void clearSource(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            itemStack.getTagCompound().removeTag("destination");
        }
    }

    public static RotatedLocation getSource(ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("destination")) {
            RotatedLocation transform = new RotatedLocation();
            transform.readFromNBT(itemStack.getTagCompound().getCompoundTag("destination"));
            return transform;
        } else {
            return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        RotatedLocation transform = getSource(stack);
        if (transform != null) {
            tooltip.add(I18n.translateToLocalFormatted("info.rift_signature.bound", transform.getLocation().getX(), transform.getLocation().getY(), transform.getLocation().getZ(), transform.getLocation().getDim()));
        } else {
            translateAndAdd("info.rift_signature.unbound", tooltip);
        }
    }
}
