package me.ht9.rose.util.config;

import com.google.gson.*;
import me.ht9.rose.Rose;
import me.ht9.rose.feature.gui.clickgui.RoseGui;
import me.ht9.rose.feature.gui.component.impl.windows.ModuleWindow;
import me.ht9.rose.feature.registry.Registry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class FileUtils
{
    public static final File MAIN_FOLDER = new File("rose");
    public static final File MODULES_FILE = new File(MAIN_FOLDER, "modules");

    static
    {
        if (!MAIN_FOLDER.exists())
        {
            MAIN_FOLDER.mkdir();
        }
        if (!MODULES_FILE.exists())
        {
            MODULES_FILE.mkdir();
        }
    }

    public static void saveModules()
    {
        Registry.modules().forEach(m ->
        {
            try
            {
                JsonObject module = m.serialize();
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(MODULES_FILE, m.name().toLowerCase() + ".json")));
                bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(module.toString())));
                bw.close();
            } catch (Throwable t)
            {
                Rose.logger().error("Failed to save module {}: ", m.name(), t);
            }
        });
    }

    public static void loadModules()
    {
        Registry.modules().forEach(m ->
        {
            try
            {
                File modConfig = new File(MODULES_FILE, m.name().toLowerCase() + ".json");
                if (modConfig.exists())
                {
                    JsonObject module = JsonParser.parseReader(new FileReader(modConfig)).getAsJsonObject();
                    m.deserialize(module);
                }
            } catch (Throwable t)
            {
                Rose.logger().error("Failed to load module {}: ", m.name(), t);
            }
        });
    }

    public static void saveClickGUI()
    {
        try
        {
            JsonObject clickGUI = new JsonObject();
            JsonArray windows = new JsonArray();
            for (ModuleWindow window : RoseGui.instance().windows())
            {
                JsonObject properties = new JsonObject();
                properties.add("name", new JsonPrimitive(window.name()));
                properties.add("x", new JsonPrimitive(window.x()));
                properties.add("y", new JsonPrimitive(window.y()));
                properties.add("open", new JsonPrimitive(window.isOpened()));
                windows.add(properties);
            }
            clickGUI.add("windows", windows);

            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(MAIN_FOLDER, "clickgui.json")));
            bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(clickGUI.toString())));
            bw.close();
        } catch (Throwable t)
        {
            Rose.logger().error("Failed to save clickgui: ", t);
        }
    }

    public static void loadClickGUI()
    {
        try
        {
            File clickGUIConfig = new File(MAIN_FOLDER, "clickgui.json");
            if (clickGUIConfig.exists())
            {
                JsonObject clickGui = JsonParser.parseReader(new FileReader(clickGUIConfig)).getAsJsonObject();
                JsonArray clickguiArray = clickGui.get("windows").getAsJsonArray();
                for (ModuleWindow window : RoseGui.instance().windows())
                {
                    clickguiArray.forEach(element ->
                    {
                        JsonObject object = element.getAsJsonObject();
                        if (window.name().equalsIgnoreCase(object.get("name").getAsString()))
                        {
                            window.setX(object.get("x").getAsFloat());
                            window.setY(object.get("y").getAsFloat());
                            window.setOpened(object.get("open").getAsBoolean());
                        }
                    });
                }
            }
        } catch (Throwable t)
        {
            Rose.logger().error("Failed to load clickgui: ", t);
        }
    }

    public static void saveFriends()
    {
        try
        {
            JsonObject friends = new JsonObject();
            JsonArray friendsArray = new JsonArray();
            for (String friend : Registry.friends())
            {
                friendsArray.add(friend);
            }
            friends.add("friends", friendsArray);

            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(MAIN_FOLDER, "friends.json")));
            bw.write(new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(JsonParser.parseString(friends.toString()))
            );
            bw.close();
        }
        catch (Throwable t)
        {
            Rose.logger().error("Failed to save friends: ", t);
        }
    }

    public static void loadFriends()
    {
        try
        {
            File friendsFile = new File(MAIN_FOLDER, "friends.json");
            if (friendsFile.exists())
            {
                JsonObject friends = JsonParser.parseReader(new FileReader(friendsFile)).getAsJsonObject();
                JsonArray friendsArray = friends.get("friends").getAsJsonArray();
                friendsArray.forEach(element -> Registry.friends().add(element.getAsString()));
            }
        }
        catch (Throwable t)
        {
            Rose.logger().error("Failed to load friends: ", t);
        }
    }
}