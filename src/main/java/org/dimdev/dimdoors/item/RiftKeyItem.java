package org.dimdev.dimdoors.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.mixin.ListTagAccessor;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.util.NbtType;

public class RiftKeyItem extends Item {
	public RiftKeyItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (isEmpty(stack)) {
			tooltip.add(new TranslatableText("item.dimdoors.rift_key.no_links"));
		} else if (context.isAdvanced()) {
			tooltip.add(LiteralText.EMPTY);
			tooltip.add(new TranslatableText("item.dimdoors.rift_key.ids"));
			for (UUID id : getIds(stack)) {
				tooltip.add(new LiteralText(" " + id.toString()));
			}
		}
		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return !isEmpty(stack);
	}

	@Override
	public boolean shouldSyncTagToClient() {
		return super.shouldSyncTagToClient();
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 30;
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = super.getDefaultStack();
		stack.putSubTag("Ids", ListTagAccessor.createListTag(new ArrayList<>(), (byte) NbtType.INT_ARRAY));
		return stack;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getWorld().isClient) {
			return ActionResult.CONSUME;
		}
		PlayerEntity player = context.getPlayer();
		BlockState state = context.getWorld().getBlockState(context.getBlockPos());
		if (player != null && state.getBlock() instanceof RiftProvider && player.isSneaky()) {
			RiftBlockEntity riftBlockEntity = ((RiftProvider<?>) state.getBlock()).getRift(context.getWorld(), context.getBlockPos(), state);
			if (riftBlockEntity.isDetached()) {
				return super.useOnBlock(context);
			}
			EntranceRiftBlockEntity entranceRiftBlockEntity = ((EntranceRiftBlockEntity) riftBlockEntity);
			Rift rift = DimensionalRegistry.getRiftRegistry().getRift(new Location(entranceRiftBlockEntity.getWorld().getRegistryKey(), entranceRiftBlockEntity.getPos()));
			if (entranceRiftBlockEntity.isLocked()) {
				if (tryRemove(context.getStack(), rift.getId())) {
					entranceRiftBlockEntity.setLocked(false);
					entranceRiftBlockEntity.markDirty();
					EntityUtils.chat(player, new TranslatableText("rifts.unlocked"));
					return ActionResult.SUCCESS;
				} else {
					EntityUtils.chat(player, new TranslatableText("rifts.cantUnlock"));
				}
			} else {
				entranceRiftBlockEntity.setLocked(true);
				add(context.getStack(), rift.getId());
				entranceRiftBlockEntity.markDirty();
				EntityUtils.chat(player, new TranslatableText("rifts.locked"));
				return ActionResult.SUCCESS;
			}
		}
		return super.useOnBlock(context);
	}

	public static boolean tryRemove(ItemStack stack, UUID id) {
		IntArrayTag arrayTag = new IntArrayTag(DynamicSerializableUuid.toIntArray(id));
		return stack.getOrCreateTag().getList("Ids", NbtType.LIST).remove(arrayTag);
	}

	public static void add(ItemStack stack, UUID id) {
		if (!has(stack, id)) {
			stack.getOrCreateTag().getList("Ids", NbtType.LIST).add(new IntArrayTag(DynamicSerializableUuid.toIntArray(id)));
		}
	}

	public static boolean has(ItemStack stack, UUID id) {
		return stack.getOrCreateTag().getList("Ids", NbtType.INT_ARRAY).contains(new IntArrayTag(DynamicSerializableUuid.toIntArray(id)));
	}

	public static boolean isEmpty(ItemStack stack) {
		return stack.getOrCreateTag().getList("Ids", NbtType.INT_ARRAY).isEmpty();
	}

	public static List<UUID> getIds(ItemStack stack) {
		return stack.getOrCreateTag()
				.getList("ids", NbtType.INT_ARRAY)
				.stream()
				.map(IntArrayTag.class::cast)
				.map(IntArrayTag::getIntArray)
				.map(DynamicSerializableUuid::toUuid)
				.collect(Collectors.toList());
	}
}
