/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.resource.conditions.conditions;

import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

public record RegistryContainsResourceCondition(Identifier registry, List<Identifier> entries) implements ResourceCondition {
	// Cannot use registry-bound codec because they fail parsing if nonexistent,
	// and resource conditions themselves should not fail to parse on condition failure
	public static final MapCodec<RegistryContainsResourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("registry").orElse(RegistryKeys.ITEM.getValue()).forGetter(RegistryContainsResourceCondition::registry),
			Identifier.CODEC.listOf().fieldOf("values").forGetter(RegistryContainsResourceCondition::entries)
	).apply(instance, RegistryContainsResourceCondition::new));

	public RegistryContainsResourceCondition(Identifier registry, Identifier... entries) {
		this(registry, List.of(entries));
	}

	@SafeVarargs
	public <T> RegistryContainsResourceCondition(RegistryKey<T>... entries) {
		this(entries[0].getRegistry(), Arrays.stream(entries).map(RegistryKey::getValue).toList());
	}

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.REGISTRY_CONTAINS;
	}

	@Override
	public boolean test(@Nullable RegistryWrapper.WrapperLookup registryLookup) {
		return ResourceConditionsImpl.registryContains(registryLookup, this.registry(), this.entries());
	}
}
