package pl.halovisuals;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name, category;
    public final boolean implemented;
    public boolean enabled;
    public final List<Setting> settings = new ArrayList<>();

    // GUI state
    public boolean expanded;
    public float anim, expAnim;

    public Module(String category, String name, boolean implemented, boolean enabled) {
        this.category = category; this.name = name; this.implemented = implemented; this.enabled = enabled;
        this.anim = enabled ? 1f : 0f;
    }

    public Setting s(String n) {
        for (Setting s : settings) if (s.name.equals(n)) return s;
        throw new IllegalArgumentException(name + ": no setting " + n);
    }
    public double num(String n) { return s(n).value; }
    public boolean on(String n) { return s(n).value > 0.5; }
    public int mode(String n) { return (int) s(n).value; }

    public void toggle() {
        enabled = !enabled;
        Features.notify(name + (enabled ? " enabled" : " disabled"));
    }
}
