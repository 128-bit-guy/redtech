package _128_bit_guy.redtech.client.model.item;

import _128_bit_guy.redtech.common.part.key.WireModelKey;
import alexiil.mc.lib.multipart.impl.client.model.SinglePartBakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

import java.util.Collections;

public class WireModel extends SinglePartBakedModel<WireModelKey> {
    public WireModel() {
        super(new WireModelKey(Direction.NORTH, Collections.emptySet()), WireModelKey.class);
    }

    @Override
    public ModelTransformation getTransformation() {
        Transformation transformation = new Transformation(new Vector3f(0f, 0f, 0f), new Vector3f(), new Vector3f(0.7f, 0.7f, 0.7f));
        return new ModelTransformation(transformation, transformation, transformation, transformation, transformation, transformation, transformation, transformation);
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return ModelItemPropertyOverrideList.EMPTY;
    }
}
