package salhackskid.skid.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

public final class Render {

    public static final int[] charWidth = new int[256];
    public static boolean unicodeFlag;
    public static final byte[] glyphWidth = new byte[65536];

    public static float getStringWidth(String p_Name)
    {
        HudModule l_Hud = (HudModule)ModuleManager.Get().GetMod(HudModule.class);

        if (l_Hud != null && !l_Hud.CustomFont.getValue())
            return gsw(p_Name);

        return SalHack.GetFontManager().GetStringWidth(p_Name);
    }

    public static int gsw(String text) {
        if (text == null) {
            return 0;
        } else {
            int i = 0;
            boolean flag = false;

            for(int j = 0; j < text.length(); ++j) {
                char c0 = text.charAt(j);
                int k = getCharWidth(c0);
                if (k < 0 && j < text.length() - 1) {
                    ++j;
                    c0 = text.charAt(j);
                    if (c0 != 'l' && c0 != 'L') {
                        if (c0 == 'r' || c0 == 'R') {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;
                if (flag && k > 0) {
                    ++i;
                }
            }

            return i;
        }
    }

    public static int getCharWidth(char character) {
        if (character == 160) {
            return 4;
        } else if (character == 167) {
            return -1;
        } else if (character == ' ') {
            return 4;
        } else {
            int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(character);
            if (character > 0 && i != -1 && !unicodeFlag) {
                return charWidth[i];
            } else if (glyphWidth[character] != 0) {
                int j = glyphWidth[character] & 255;
                int k = j >>> 4;
                int l = j & 15;
                ++l;
                return (l - k) / 2 + 1;
            } else {
                return 0;
            }
        }
    }

    public static void drawRect(float x, float y, float w, float h, int color, float alpha)
    {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferbuilder.vertex((double) x, (double) h, 0.0D).color(red, green, blue, alpha).next();
        bufferbuilder.vertex((double) w, (double) h, 0.0D).color(red, green, blue, alpha).next();
        bufferbuilder.vertex((double) w, (double) y, 0.0D).color(red, green, blue, alpha).next();
        bufferbuilder.vertex((double) x, (double) y, 0.0D).color(red, green, blue, alpha).next();
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
