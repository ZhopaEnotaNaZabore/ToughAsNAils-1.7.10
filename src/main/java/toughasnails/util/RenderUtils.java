package toughasnails.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

public class RenderUtils
{
    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0D, textureX * f, (textureY + height) * f1);
        tessellator.addVertexWithUV(x + width, y + height, 0.0D, (textureX + width) * f, (textureY + height) * f1);
        tessellator.addVertexWithUV(x + width, y, 0.0D, (textureX + width) * f, textureY * f1);
        tessellator.addVertexWithUV(x, y, 0.0D, textureX * f, textureY * f1);
        tessellator.draw();
    }
}
