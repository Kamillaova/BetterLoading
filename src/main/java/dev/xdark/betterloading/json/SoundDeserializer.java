package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.sound.Sound;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

public final class SoundDeserializer extends TypeAdapter<Sound> {
  public static final SoundDeserializer INSTANCE = new SoundDeserializer();

  private SoundDeserializer() {}

  @Override
  public void write(JsonWriter out, Sound value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Sound read(JsonReader in) throws IOException {
    Sound result;
    var token = in.peek();
    if (token == JsonToken.BEGIN_OBJECT) {
      in.beginObject();
      result = implRead(in);
      in.endObject();
    } else if (token == JsonToken.STRING) {
      result = new Sound(
        in.nextString(),
        r -> 1.0F,
        r -> 1.0F,
        1,
        Sound.RegistrationType.FILE,
        false,
        false,
        16
      );
    } else {
      throw new JsonParseException("Unexpected sound token");
    }
    return result;
  }

  public static Sound implRead(JsonReader in) throws IOException {
    String name = null;
    var type = Sound.RegistrationType.FILE;
    var volume = 1.0F;
    var pitch = 1.0F;
    var weight = 1;
    var preload = false;
    var stream = false;
    var attenuation_distance = 16;
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "name" -> name = in.nextString();
        case "type" -> {
          if ((type = RegistrationTypeDeserializer.INSTANCE.read(in)) == null) {
            throw new JsonParseException("Unknown sound registration type");
          }
        }
        case "volume" -> Validate.isTrue((volume = (float) in.nextDouble()) > 0.0F, "Invalid volume");
        case "pitch" -> Validate.isTrue((pitch = (float) in.nextDouble()) > 0.0F, "Invalid pitch");
        case "weight" -> Validate.isTrue((weight = in.nextInt()) > 0, "Invalid weight");
        case "preload" -> preload = in.nextBoolean();
        case "stream" -> stream = in.nextBoolean();
        case "attenuation_distance" -> attenuation_distance = in.nextInt();
        default -> in.skipValue();
      }
    }
    if (name == null) {
      throw new JsonParseException("Missing name on Sound");
    }
    var finalVolume = volume;
    var finalPitch = pitch;
    return new Sound(name, r -> finalVolume, r -> finalPitch, weight, type, stream, preload, attenuation_distance);
  }
}
