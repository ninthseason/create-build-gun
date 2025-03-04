package kl1nge5.create_build_gun.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.saveddata.SavedData;

public class StageData extends SavedData {
    public int stage;

    // Load existing instance of saved data
    public static StageData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        StageData data = new StageData();
        data.stage = tag.getInt("stage");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("stage", IntTag.valueOf(this.stage));
        return tag;
    }
}
