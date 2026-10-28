package pl.halovisuals.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class Draw {
    public static final int YELLOW = 0xFFFFD84D, ORANGE = 0xFFFF8A1F, WHITE = 0xFFFFFFFF;
    public static final int GLASS = 0xB0261E26, GRAY = 0xFFB9B2A6;

    private Draw() {}

    public static int a(int c, int alpha) { return (c & 0xFFFFFF) | (Math.max(0, Math.min(255, alpha)) << 24); }

    public static int lerp(int c1, int c2, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int A = (int) (((c1 >>> 24) & 255) * (1 - t) + ((c2 >>> 24) & 255) * t);
        int R = (int) (((c1 >> 16) & 255) * (1 - t) + ((c2 >> 16) & 255) * t);
        int G = (int) (((c1 >> 8) & 255) * (1 - t) + ((c2 >> 8) & 255) * t);
        int B = (int) ((c1 & 255) * (1 - t) + (c2 & 255) * t);
        return (A << 24) | (R << 16) | (G << 8) | B;
    }

    public static int accent(float t) { return lerp(YELLOW, ORANGE, t); }

    /** Rounded rectangle drawn row by row (anti-alias free but smooth enough at GUI scale). */
    public static void rrect(DrawContext c, int x, int y, int w, int h, int r, int col) {
        if (w <= 0 || h <= 0) return;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        for (int i = 0; i < h; i++) {
            int inset = 0;
            if (i < r) {
                double dy = r - i - 0.5;
                inset = (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
            } else if (i >= h - r) {
                double dy = i - (h - r) + 0.5;
                inset = (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
            }
            c.fill(x + inset, y + i, x + w - inset, y + i + 1, col);
        }
    }

    /** Glassmorphism panel: soft golden glow + white border + smoky glass body. */
    public static void panel(DrawContext c, int x, int y, int w, int h, int r) {
        for (int k = 4; k >= 1; k--) rrect(c, x - k, y - k, w + 2 * k, h + 2 * k, r + k, a(YELLOW, 9));
        rrect(c, x - 1, y - 1, w + 2, h + 2, r + 1, a(WHITE, 70));
        rrect(c, x, y, w, h, r, GLASS);
    }

    public static int width(String s) { return MinecraftClient.getInstance().textRenderer.getWidth(s); }

    public static void text(DrawContext c, String s, int x, int y, int col) {
        c.drawText(MinecraftClient.getInstance().textRenderer, s, x, y, col, true);
    }

    public static void ctext(DrawContext c, String s, int cx, int y, int col) {
        text(c, s, cx - width(s) / 2, y, col);
    }

    /** Gradient progress bar (yellow -> orange). */
    public static void bar(DrawContext c, int x, int y, int w, int h, float frac) {
        rrect(c, x, y, w, h, h / 2, a(WHITE, 40));
        int fw = (int) (w * Math.max(0f, Math.min(1f, frac)));
        for (int i = 0; i < fw; i++) c.fill(x + i, y, x + i + 1, y + h, accent(i / (float) Math.max(1, w)));
    }

    /** Circle outline made of 1px dots; progress 0..1 starting at the top, clockwise. */
    public static void ring(DrawContext c, float cx, float cy, float r, float progress, int col) {
        int total = (int) (2 * Math.PI * r);
        int steps = (int) (total * Math.max(0f, Math.min(1f, progress)));
        for (int i = 0; i < steps; i++) {
            double ang = -Math.PI / 2 + (i / (double) total) * 2 * Math.PI;
            int px = Math.round(cx + (float) Math.cos(ang) * r), py = Math.round(cy + (float) Math.sin(ang) * r);
            c.fill(px, py, px + 1, py + 1, col);
        }
    }
}
