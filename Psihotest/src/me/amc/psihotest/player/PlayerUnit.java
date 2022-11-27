package me.amc.psihotest.player;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.gson.stream.JsonReader;

public class PlayerUnit {

	UUID uuid;
    WrappedSignedProperty property;

    public PlayerUnit(UUID uuid) {
        this.uuid = uuid;
        this.property = loadPlayerProperty();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public WrappedSignedProperty getProperty(){
        return this.property;
    }

    private WrappedSignedProperty loadPlayerProperty() {
        try {
            HttpURLConnection huc = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "") + "?unsigned=false").openConnection();
            huc.setRequestMethod("GET");
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.connect();
            JsonReader reader = new JsonReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));
            String name = null;
            String signature = null;
            String value = null;
            reader.beginObject();
            while (reader.hasNext()) {
                String objName = reader.nextName();
                if (objName.equals("properties")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String field = reader.nextName();

                            if (field.equals("signature")) {
                                signature = reader.nextString();
                            } else if (field.equals("value")) {
                                value = reader.nextString();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                } else if (objName.equals("name")) {
                    name = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            huc.disconnect();
            if (name != null && signature != null && value != null) {
                return new WrappedSignedProperty(name, value, signature);
            }
        } catch (Exception e) {
            // Did not find fake player mojang profile!
        	// But it's okay!
        }
        return null;
    }
	
}
