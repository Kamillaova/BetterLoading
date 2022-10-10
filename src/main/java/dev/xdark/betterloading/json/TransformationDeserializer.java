package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.math.Vec3f;

import java.io.IOException;

public final class TransformationDeserializer extends TypeAdapter<Transformation> {
  public static final TransformationDeserializer INSTANCE = new TransformationDeserializer();

  private static final Vec3f DEFAULT_ROTATION = new Vec3f(0.0F, 0.0F, 0.0F);
  private static final Vec3f DEFAULT_TRANSLATION = new Vec3f(0.0F, 0.0F, 0.0F);
  private static final Vec3f DEFAULT_SCALE = new Vec3f(1.0F, 1.0F, 1.0F);

  private TransformationDeserializer() {}

  @Override
  public void write(JsonWriter out, Transformation value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Transformation read(JsonReader in) throws IOException {
    in.beginObject();
    var result = implRead(in);
    in.endObject();
    return result;
  }

  public static Transformation implRead(JsonReader in) throws IOException {
    var rotation = DEFAULT_ROTATION;
    var translation = DEFAULT_TRANSLATION;
    var scale = DEFAULT_SCALE;
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "rotation" -> rotation = Vec3fDeserializer.INSTANCE.read(in);
        case "translation" -> {
          translation = Vec3fDeserializer.INSTANCE.read(in);
          translation.scale(0.0625F);
          translation.clamp(-5.0F, 5.0F);
        }
        case "scale" -> {
          scale = Vec3fDeserializer.INSTANCE.read(in);
          scale.clamp(-4.0F, 4.0F);
        }
        default -> in.skipValue();
      }
    }
    return new Transformation(rotation, translation, scale);
  }
}
