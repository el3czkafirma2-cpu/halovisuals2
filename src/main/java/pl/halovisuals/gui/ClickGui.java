package pl.halovisuals.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pl.halovisuals.Module;
import pl.halovisuals.ModuleManager;
import pl.halovisuals.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClickGui extends Screen {
    private record Hit(int x, int y, int w, int h, Module m, Setting s) {
        boolean in(double mx, double my) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    }

    private final List<Hit> hits = new ArrayList<>();
    private Setting dragging, listening;
    private int dragX, dragW;
    private String filter = "";
    private boolean filterFocused;
    private float open = 0f;

    public ClickGui() { super(Text.literal("Halo Visuals")); }

    @Override public boolean shouldPause() { return false; }

    private static int rowH(Setting s) { return s.type == Setting.Type.SLIDER ? 24 : 18; }
    private static int settingsH(Module m) { int h = 4; for (Setting s : m.settings) h += rowH(s); return h; }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta); // vanilla background + blur
        open += (1f - open) * 0.18f;
        hits.clear();

        // angelic ambient glow
        ctx.fillGradient(0, 0, width, height / 2, 0x38FFD84D, 0x00FFD84D);
        ctx.fillGradient(0, height / 2, width, height, 0x00FF8A1F, 0x30FF8A1F);

        // title
        ctx.getMatrices().push();
        ctx.getMatrices().translate(24, 14, 0);
        ctx.getMatrices().scale(2f, 2f, 1f);
        Draw.text(ctx, "Halo", 0, 0, Draw.YELLOW);
        ctx.getMatrices().pop();
        Draw.text(ctx, "VISUALS", 24 + Draw.width("Halo") * 2 + 6, 14, Draw.ORANGE);
        Draw.text(ctx, "all features free", 24 + Draw.width("Halo") * 2 + 6, 25, Draw.a(Draw.WHITE, 140));

        // filter box
        int fx = width - 24 - 190, fy = 14;
        Draw.panel(ctx, fx, fy, 190, 22, 11);
        String ft = filter.isEmpty() && !filterFocused ? "Type to filter..." : filter + (filterFocused && (System.currentTimeMillis() / 500) % 2 == 0 ? "_" : "");
        Draw.text(ctx, ft, fx + 10, fy + 7, filter.isEmpty() ? Draw.GRAY : Draw.WHITE);
        hits.add(new Hit(fx, fy, 190, 22, null, null));

        String f = filter.toLowerCase(Locale.ROOT);
        int gap = 8;
        int pw = Math.min(150, (width - 24 - gap * 5) / 6);
        int x0 = (width - (pw * 6 + gap * 5)) / 2;
        int y0 = 48 + (int) ((1f - open) * -18);

        for (int ci = 0; ci < ModuleManager.CATS.length; ci++) {
            final String cat = ModuleManager.CATS[ci];
            int px = x0 + ci * (pw + gap);
            List<Module> ms = ModuleManager.ALL.stream()
                    .filter(m -> m.category.equals(cat) && (f.isEmpty() || m.name.toLowerCase(Locale.ROOT).contains(f))).toList();

            int ph = 30 + 6;
            for (Module m : ms) {
                m.expAnim += ((m.expanded ? 1f : 0f) - m.expAnim) * 0.25f;
                ph += 22 + (int) (settingsH(m) * m.expAnim);
            }
            Draw.panel(ctx, px, y0, pw, ph, 10);

            // header
            Draw.rrect(ctx, px + 10, y0 + 11, 8, 8, 4, Draw.accent(0.5f));
            Draw.rrect(ctx, px + 8, y0 + 9, 12, 12, 6, Draw.a(Draw.YELLOW, 40));
            Draw.text(ctx, cat, px + 26, y0 + 11, Draw.WHITE);
            String cnt = String.valueOf(ms.size());
            Draw.text(ctx, cnt, px + pw - 10 - Draw.width(cnt), y0 + 11, Draw.GRAY);
            for (int i = 10; i < pw - 10; i++) ctx.fill(px + i, y0 + 28, px + i + 1, y0 + 29, Draw.a(Draw.accent((i - 10) / (float) pw), 110));

            int y = y0 + 32;
            for (Module m : ms) {
                m.anim += ((m.enabled ? 1f : 0f) - m.anim) * 0.25f;
                boolean hover = mx >= px && mx < px + pw && my >= y && my < y + 22;
                int rx = px + 4, rw = pw - 8;
                if (m.anim > 0.02f) Draw.rrect(ctx, rx, y, rw, 20, 7, Draw.a(Draw.YELLOW, (int) (45 * m.anim)));
                else if (hover) Draw.rrect(ctx, rx, y, rw, 20, 7, Draw.a(Draw.WHITE, 22));
                if (m.anim > 0.02f) Draw.rrect(ctx, px + 6, y + 4, 2, 12, 1, Draw.a(Draw.accent(0.4f), (int) (255 * m.anim)));

                int nameCol = Draw.lerp(m.implemented ? Draw.GRAY : 0xFF807A72, Draw.WHITE, m.anim);
                Draw.text(ctx, m.name, px + 12, y + 6, nameCol);
                if (!m.implemented) Draw.text(ctx, "soon", px + pw - 44 - Draw.width("soon") + 14, y + 6, 0xFF6F6A63);

                if (!m.settings.isEmpty()) Draw.text(ctx, m.expanded ? "v" : ">", px + pw - 50, y + 6, Draw.GRAY);

                // toggle switch
                int tx = px + pw - 34, ty = y + 4;
                Draw.rrect(ctx, tx - 1, ty - 1, 28, 14, 7, Draw.a(Draw.accent(0.5f), (int) (50 * m.anim)));
                Draw.rrect(ctx, tx, ty, 26, 12, 6, Draw.lerp(Draw.a(Draw.WHITE, 55), Draw.accent(0.5f), m.anim));
                Draw.rrect(ctx, tx + 2 + (int) (14 * m.anim), ty + 2, 8, 8, 4, Draw.WHITE);

                hits.add(new Hit(px, y, pw, 22, m, null));
                y += 22;

                int eh = (int) (settingsH(m) * m.expAnim);
                if (eh > 1) {
                    ctx.enableScissor(px, y, px + pw, y + eh);
                    int sy = y + 2;
                    for (Setting s : m.settings) {
                        drawSetting(ctx, px, sy, pw, s);
                        if (m.expAnim > 0.95f) hits.add(new Hit(px, sy, pw, rowH(s), m, s));
                        sy += rowH(s);
                    }
                    ctx.disableScissor();
                }
                y += eh;
            }
        }
    }

    private void drawSetting(DrawContext ctx, int px, int sy, int pw, Setting s) {
        int sx = px + 12, sw = pw - 24;
        switch (s.type) {
            case TOGGLE -> {
                Draw.text(ctx, s.name, sx, sy + 5, Draw.GRAY);
                boolean on = s.value > 0.5;
                Draw.rrect(ctx, px + pw - 22, sy + 4, 10, 10, 3, on ? Draw.accent(0.5f) : Draw.a(Draw.WHITE, 60));
                if (on) Draw.rrect(ctx, px + pw - 19, sy + 7, 4, 4, 1, Draw.WHITE);
            }
            case SLIDER -> {
                Draw.text(ctx, s.name, sx, sy + 2, Draw.GRAY);
                String v = fmt(s);
                Draw.text(ctx, v, sx + sw - Draw.width(v), sy + 2, Draw.YELLOW);
                Draw.rrect(ctx, sx, sy + 15, sw, 4, 2, Draw.a(Draw.WHITE, 55));
                int fw = (int) (sw * (s.value - s.min) / (s.max - s.min));
                Draw.rrect(ctx, sx, sy + 15, Math.max(4, fw), 4, 2, Draw.accent((float) ((s.value - s.min) / (s.max - s.min))));
                Draw.rrect(ctx, sx + fw - 3, sy + 13, 6, 8, 3, Draw.WHITE);
            }
            case MODE -> {
                Draw.text(ctx, s.name, sx, sy + 5, Draw.GRAY);
                String v = s.modes[(int) s.value];
                Draw.text(ctx, v, sx + sw - Draw.width(v), sy + 5, Draw.YELLOW);
            }
            case KEY -> {
                Draw.text(ctx, s.name, sx, sy + 5, Draw.GRAY);
                String v = listening == s ? "..." : "[" + InputUtil.fromKeyCode((int) s.value, 0).getLocalizedText().getString() + "]";
                Draw.text(ctx, v, sx + sw - Draw.width(v), sy + 5, listening == s ? Draw.ORANGE : Draw.YELLOW);
            }
        }
    }

    private static String fmt(Setting s) {
        double v = s.value;
        return Math.abs(v - Math.round(v)) < 0.001 ? String.valueOf(Math.round(v)) : String.format(Locale.ROOT, "%.2f", v);
    }

    private void applySlider(double mx) {
        if (dragging == null) return;
        double r = Math.max(0, Math.min(1, (mx - dragX) / dragW));
        double v = dragging.min + r * (dragging.max - dragging.min);
        v = (dragging.max - dragging.min >= 10) ? Math.round(v) : Math.round(v * 100) / 100.0;
        dragging.value = v;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        filterFocused = false;
        for (Hit h : hits) {
            if (!h.in(mx, my)) continue;
            if (h.m() == null) { filterFocused = true; return true; }
            if (h.s() == null) {
                boolean arrow = mx > h.x() + h.w() - 56 && mx < h.x() + h.w() - 40 && !h.m().settings.isEmpty();
                if (btn == 0 && !arrow) h.m().toggle();
                else if (!h.m().settings.isEmpty()) h.m().expanded = !h.m().expanded;
                ModuleManager.save();
                return true;
            }
            Setting s = h.s();
            switch (s.type) {
                case TOGGLE -> s.value = s.value > 0.5 ? 0 : 1;
                case MODE -> s.value = (s.value + (btn == 1 ? s.modes.length - 1 : 1)) % s.modes.length;
                case KEY -> listening = s;
                case SLIDER -> { dragging = s; dragX = h.x() + 12; dragW = h.w() - 24; applySlider(mx); }
            }
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging != null) { applySlider(mx); return true; }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (dragging != null) { dragging = null; ModuleManager.save(); }
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (listening != null) {
            if (key != GLFW.GLFW_KEY_ESCAPE) listening.value = key;
            listening = null;
            ModuleManager.save();
            return true;
        }
        if (filterFocused) {
            if (key == GLFW.GLFW_KEY_BACKSPACE && !filter.isEmpty()) { filter = filter.substring(0, filter.length() - 1); return true; }
            if (key == GLFW.GLFW_KEY_ENTER) { filterFocused = false; return true; }
        } else if (key == (int) ModuleManager.get("Click GUI").num("Key")) {
            close();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        if (filterFocused && c >= 32) { filter += c; return true; }
        return super.charTyped(c, mods);
    }

    @Override
    public void removed() { ModuleManager.save(); }
}
