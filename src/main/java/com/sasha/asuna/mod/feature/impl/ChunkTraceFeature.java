/*
 * Copyright (c) Sasha Stevens (2017 - 2018)
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sasha.asuna.mod.feature.impl;

import com.sasha.asuna.mod.AsunaMod;
import com.sasha.asuna.mod.events.client.ClientEnderPearlSpawnEvent;
import com.sasha.asuna.mod.events.server.ServerLoadChunkEvent;
import com.sasha.asuna.mod.feature.AbstractAsunaTogglableFeature;
import com.sasha.asuna.mod.feature.AsunaCategory;
import com.sasha.asuna.mod.feature.IAsunaRenderableFeature;
import com.sasha.asuna.mod.feature.IAsunaTickableFeature;
import com.sasha.asuna.mod.feature.annotation.FeatureInfo;
import com.sasha.asuna.mod.feature.option.AsunaFeatureOption;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.sasha.asuna.mod.misc.AsunaRender.chunkESP;

/**
 * Created by Sasha on 11/08/2018 at 8:34 PM
 **/
@FeatureInfo(description = "Highlights chunks that have never been generated by the server. Useful for base hunting.")
public class ChunkTraceFeature extends AbstractAsunaTogglableFeature
        implements SimpleListener, IAsunaTickableFeature, IAsunaRenderableFeature {

    private static ArrayList<Chunk> chunks = new ArrayList<>();

    public ChunkTraceFeature() {
        super("ChunkTrace", AsunaCategory.RENDER,
                new AsunaFeatureOption<>("ChunkESP", true),
                new AsunaFeatureOption<>("PearlNotify", true));
    }

    @Override
    public void onTick() {
        LinkedHashMap<String, Boolean> suffixMap = new LinkedHashMap<>();
        suffixMap.put("Chunks", this.getFormattableOptionsMap().get("ChunkESP"));
        suffixMap.put("Pearls", this.getFormattableOptionsMap().get("PearlNotify"));
        this.setSuffix(suffixMap);
    }

    @Override
    public void onRender() {
        if (this.getFormattableOptionsMap().get("ChunkESP")) {
            for (Chunk chunk : chunks) {
                int x, z;
                x = chunk.x * 16;
                z = chunk.z * 16;
                double maxY = AsunaMod.minecraft.player.posY + 25;
                int y = 0;
                chunkESP(x, y, z, 1.0f, 0.0f, 0.0f, 0.5f, maxY);
            }
        }
    }

    @SimpleEventHandler
    public void onEnderPearlSpawn(ClientEnderPearlSpawnEvent e) {
        if (!this.isEnabled() || !this.getFormattableOptionsMap().get("PearlNotify")) return;
        AsunaMod.logMsg(false, "\2478(\247bChunkTrace\2478) \2477Ender pearl loaded @ XYZ *x *y *z".replace("*x", e.getCoordinate()[0] + "")
                .replace("*y", e.getCoordinate()[1] + "").replace("*z", e.getCoordinate()[2] + ""));
    }

    @SimpleEventHandler
    public void onNewChunk(ServerLoadChunkEvent e) {
        if (!this.isEnabled() || !this.getFormattableOptionsMap().get("ChunkESP")) return;
        if (e.getPacketIn().isFullChunk()) return;
        if (!chunks.contains(e.getChunk())) {
            chunks.add(e.getChunk());
        }
    }
}
