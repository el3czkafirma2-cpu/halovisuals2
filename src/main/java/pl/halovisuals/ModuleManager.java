package pl.halovisuals;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.*;
import java.util.*;

public final class ModuleManager {
    public static final List<Module> ALL = new ArrayList<>();
    private static final Map<String, Module> MAP = new HashMap<>();
    public static final String[] CATS = {"Combat", "HUD", "Animations", "Sound", "World", "Client"};
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("halovisuals.json");

    private ModuleManager() {}

    public static Module get(String n) { return MAP.get(n); }

    private static Module add(String cat, String name, boolean impl, boolean def, Setting... s) {
        Module m = new Module(cat, name, impl, def);
        m.settings.addAll(Arrays.asList(s));
        ALL.add(m); MAP.put(name, m);
        return m;
    }

    static {
        // ---- Combat ----
        add("Combat", "Hit Marker", true, true, Setting.slider("Size", 3, 12, 6), Setting.slider("Duration", 100, 800, 350));
        add("Combat", "Damage Numbers", false, false);
        add("Combat", "Hit Particles", true, false, Setting.slider("Amount", 1, 30, 10));
        add("Combat", "Hit Bubbles", true, false, Setting.slider("Amount", 1, 30, 8));
        add("Combat", "Target ESP", true, false);
        add("Combat", "Kill Effect", true, false);
        add("Combat", "Death Effect", false, false);
        add("Combat", "Totem Pop", false, false);
        add("Combat", "Hit Color", false, false);
        add("Combat", "Hitbox Customizer", false, false);
        // ---- HUD ----
        add("HUD", "Target HUD", true, true);
        add("HUD", "Watermark", true, true);
        add("HUD", "Dynamic Island", true, true);
        add("HUD", "Potions", true, false);
        add("HUD", "Cooldowns", true, false);
        add("HUD", "Combo Counter", true, false);
        add("HUD", "Cooldown Ring", true, false);
        add("HUD", "Crosshair", true, false,
                Setting.mode("Style", 0, "Cross", "Dot", "Circle"),
                Setting.slider("Size", 2, 10, 5), Setting.slider("Gap", 0, 8, 3));
        // ---- Animations ----
        add("Animations", "Swing Animations", false, false);
        add("Animations", "No Hurt Cam", true, false);
        // ---- Sound ----
        add("Sound", "Hit Sounds", true, true,
                Setting.mode("Style", 1, "silent", "halo", "bell", "bonk"), Setting.slider("Volume", 0.1, 1.0, 0.8));
        add("Sound", "Kill Sound", true, false, Setting.slider("Volume", 0.1, 1.0, 0.8));
        // ---- World ----
        add("World", "Jump Circles", true, false);
        add("World", "Jump Particles", true, false);
        add("World", "Ambient Particles", true, false, Setting.slider("Amount", 1, 10, 3));
        add("World", "Trails", true, false, Setting.mode("Style", 0, "Glow", "Flame"));
        add("World", "Halo", true, true,
                Setting.slider("Radius", 0.2, 0.5, 0.32), Setting.toggle("Glow", true), Setting.toggle("Others", true));
        add("World", "China Hat", true, false, Setting.slider("Radius", 0.3, 0.9, 0.55));
        add("World", "Custom Sky", false, false);
        add("World", "Atmosphere", false, false);
        add("World", "Cinematic Text", false, false);
        add("World", "Motion Blur", false, false);
        add("World", "Color Saturation", false, false);
        add("World", "Aspect Ratio", false, false);
        add("World", "Fullbright", true, false);
        // ---- Client ----
        add("Client", "Click GUI", true, true, Setting.key("Key", 73)); // 73 = GLFW_KEY_I
        add("Client", "Discord Presence", false, false);
        add("Client", "Halo Nametag", false, false);
        add("Client", "Cosmetics", false, false);
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ALL) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.enabled);
                JsonObject st = new JsonObject();
                for (Setting s : m.settings) st.addProperty(s.name, s.value);
                o.add("settings", st);
                root.add(m.name, o);
            }
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void load() {
        try {
            if (!Files.exists(FILE)) { save(); return; }
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            for (Module m : ALL) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (o.has("enabled")) { m.enabled = o.get("enabled").getAsBoolean(); m.anim = m.enabled ? 1f : 0f; }
                if (o.has("settings")) {
                    JsonObject st = o.getAsJsonObject("settings");
                    for (Setting s : m.settings) if (st.has(s.name)) s.value = st.get(s.name).getAsDouble();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
