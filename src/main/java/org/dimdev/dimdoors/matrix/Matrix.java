package org.dimdev.dimdoors.matrix;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * An Annotation Based Registration Library.
 *
 * <p>Matrix allows registering items, blocks, etc. without
 * calling {@code Registry.register(...)} a bunch of times or
 * registering at static init. Registering at static init
 * is not safe as you might just register the entries much
 * before vanilla registers its entries.</p>
 *
 * <p>Using Annotations is simple and easy to migrate to.
 * You no longer have to worry about skipping a call to
 * {@code Registry.register(...)}.</p>
 */
public class Matrix {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 *
	 * @param clazz The class that should be scanned for registry entries.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T, S> void register(Class<T> clazz, DefaultedRegistry registry) {
		Registrar registrar = clazz.getAnnotation(Registrar.class);
		if (registrar == null) {
			return;
		}

		LOGGER.atWarn().log("Starting to register...");
		String modid = registrar.modid();

		Class<?> element = registrar.element();
		Arrays.stream(clazz.getFields())
			.filter(field -> field.isAnnotationPresent(RegistryEntry.class)
				&& Modifier.isPublic(field.getModifiers())
				&& Modifier.isStatic(field.getModifiers())
				&& Modifier.isFinal(field.getModifiers())
				&& element.isAssignableFrom(field.getType())
			)
			.forEach(field -> {
				try {
					Object value = field.get(null);
					Registry.register(registry, new Identifier(modid, field.getAnnotation(RegistryEntry.class).value()), value);
					LOGGER.warn("Matrix registered " + field.getAnnotation(RegistryEntry.class).value());

					if (value instanceof BlockItem) {
						((BlockItem) value).appendBlocks(Item.BLOCK_ITEMS, (Item) value);
					}
				} catch (IllegalAccessException e) {
					throw new AssertionError(e);
				} catch (ClassCastException e) {
					//TODO: Make this print more concise details than a stacktrace
					LOGGER.atError().withThrowable(e).log("Something went very wrong");
				}
			});
	}
}
