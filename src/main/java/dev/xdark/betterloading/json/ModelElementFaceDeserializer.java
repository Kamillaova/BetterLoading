package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.util.math.Direction;

import java.io.IOException;

public final class ModelElementFaceDeserializer extends TypeAdapter<ModelElementFace> {
  public static final ModelElementFaceDeserializer INSTANCE = new ModelElementFaceDeserializer();

  private ModelElementFaceDeserializer() {}

  @Override
  public void write(JsonWriter out, ModelElementFace value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ModelElementFace read(JsonReader in) throws IOException {
    in.beginObject();
    var result = implRead(in);
    in.endObject();
    return result;
  }

  public static ModelElementFace implRead(JsonReader in) throws IOException {
    Direction direction = null;
    var tintindex = -1;
    String texture = null;
    var rotation = 0;
    float[] uv = null;
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "cullface" -> direction = DirectionDeserializer.INSTANCE.read(in);
        case "tintindex" -> tintindex = in.nextInt();
        case "texture" -> texture = in.nextString();
        case "rotation" -> {
          if ((rotation = in.nextInt()) < 0 || rotation % 90 != 0 || rotation / 90 > 3) {
            throw new JsonParseException(
              "Invalid rotation " + rotation + " found, only 0/90/180/270 allowed");
          }
        }
        case "uv" -> {
          in.beginArray();
          uv = new float[]{
            (float) in.nextDouble(),
            (float) in.nextDouble(),
            (float) in.nextDouble(),
            (float) in.nextDouble()
          };
          in.endArray();
        }
        default -> in.skipValue();
      }
    }
    return new ModelElementFace(
      direction,
      tintindex,
      texture,
      new ModelElementTexture(
        uv,
        rotation
      )
    );
  }
}
